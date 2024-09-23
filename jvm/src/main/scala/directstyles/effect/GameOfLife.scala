package directstyles.effect

import com.googlecode.lanterna.terminal.*
import com.googlecode.lanterna.screen.*
import gears.async.*
import gears.async.default.given
import directstyles.*
import scala.util.Success
import scala.util.Try
import scala.io.StdIn
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor

// https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
object GameOfLife:
  case class Cell(x: Int, y: Int, state: CellState)

  enum CellState:
    case Live, Dead

  enum CellEvent:
    case Tick
    case ReportCell(cell: Cell)

  final case class MyScreen(screen: Screen)

  final case class Config(gridWidth: Int, gridHeight: Int, tick: Int)

  type GridCapReq = (Async.Spawn, Config, MyScreen)
  type GridCap[A] = Cap[GridCapReq, A]
  object GridCap:
    def run(
        program: ChannelMultiplexer[CellEvent] => GridCap[Unit]
    )(using Async.Spawn, Config): Unit =
      val config = summon[Config]
      val terminalFactory = DefaultTerminalFactory()
      val screen = TerminalScreen(terminalFactory.createTerminal())
      screen.startScreen()
      screen.setCursorPosition(null)
      given MyScreen = MyScreen(screen)
      val multiplexer = ChannelMultiplexer[CellEvent]()
      val sender = BufferedChannel[CellEvent]()
      val reader = BufferedChannel[Try[CellEvent]]()
      multiplexer.addPublisher(sender)
      multiplexer.addSubscriber(reader)
      Future:
        var count = 0
        while true do
          reader.read() match
            case Right(Success(event)) =>
              event match
                case CellEvent.ReportCell(cell) =>
                  screen.setCharacter(
                    cell.x,
                    cell.y,
                    TextCharacter(
                      ' ',
                      TextColor.ANSI.DEFAULT,
                      if cell.state == CellState.Live then TextColor.ANSI.WHITE
                      else TextColor.ANSI.BLACK
                    )
                  )
                  count += 1
                case CellEvent.Tick =>
                  println(s"count = $count")
                  count = 0
                case _ => ()
            case _ => throw (Exception("channel closed"))
      program(multiplexer)
      Future(multiplexer.run())
      Future:
        while true do
          screen.refresh()
          sender.send(CellEvent.Tick)
          AsyncOperations.sleep(config.tick)

  type CellCapReq = Reqs[(Async.Spawn, Config)]
  type CellCap[A] = Cap[CellCapReq, A]
  object CellCap:
    private def areNeighbors(cell1: Cell, cell2: Cell): CellCap[Boolean] =
      val config = summon[Config]
      if cell1 == cell2 then false
      else
        val xDiff = Math.abs(cell1.x - cell2.x)
        val yDiff = Math.abs(cell1.y - cell2.y)
        (xDiff <= 1 || xDiff == config.gridWidth - 1) && (yDiff <= 1 || yDiff == config.gridHeight - 1)

    def attachCell(multiplexer: ChannelMultiplexer[CellEvent], cell: Cell): CellCap[Unit] =
      val sender = BufferedChannel[CellEvent]()
      val reader = BufferedChannel[Try[CellEvent]]()
      multiplexer.addPublisher(sender.asReadable)
      multiplexer.addSubscriber(reader.asSendable)
      Future:
        var selfCell = cell
        var liveNeighbors = 2 // for live cell to survive the first tick
        while true do
          reader.read() match
            case Right(Success(event)) =>
              event match
                case CellEvent.Tick =>
                  selfCell = selfCell.copy(state =
                    if selfCell.state == CellState.Live then
                      if liveNeighbors == 2 || liveNeighbors == 3 then CellState.Live
                      else CellState.Dead
                    else if liveNeighbors == 3 then CellState.Live
                    else CellState.Dead
                  )
                  liveNeighbors = 0
                  sender.send(CellEvent.ReportCell(selfCell))
                case CellEvent.ReportCell(cell) =>
                  if cell.state == CellState.Live && areNeighbors(cell, selfCell) then
                    liveNeighbors += 1
            case _ => throw Exception("channel closed")
      ()

    def run(program: CellCap[Unit])(using Async.Spawn, Config): Unit =
      program

  @main
  def runGameOfLife(): Unit =
    val block = Set((1, 1), (1, 2), (2, 1), (2, 2))
    val blinker = Set((2, 1), (2, 2), (2, 3))
    val glider = Set((1, 1), (2, 2), (3, 2), (1, 3), (2, 3))
    val liveCoords = glider
    Async.blocking:
      given Config = Config(10, 10, 100)
      GridCap.run: multiplexer =>
        CellCap.run:
          val config = summon[Config]
          for x <- 0 until config.gridWidth do
            for y <- 0 until config.gridHeight do
              CellCap.attachCell(
                multiplexer,
                Cell(x, y, if liveCoords((x, y)) then CellState.Live else CellState.Dead)
              )
      StdIn.readLine()
