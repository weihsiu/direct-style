package directstyles.basic

import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.TextColor

object TerminalUI:
  @main
  def runTerminalUI(): Unit =
    val terminalFactory = DefaultTerminalFactory()
    val terminal = terminalFactory.createTerminal()
    "Hello\n".foreach(terminal.putCharacter)
    terminal.flush()
    Thread.sleep(2000)
    terminal.setBackgroundColor(TextColor.ANSI.BLUE)
    terminal.setForegroundColor(TextColor.ANSI.YELLOW)
    "Yellow on blue".foreach(terminal.putCharacter)
    terminal.flush()
