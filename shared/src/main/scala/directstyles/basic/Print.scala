// https://noelwelsh.com/posts/direct-style/
package directstyles.basic

import directstyles.*
import scala.util.chaining.*

type Console = Console.type

type PrintReq = Console
type PrintCap[A] = Cap[PrintReq, A]

object PrintCap:
  inline def apply[A](inline body: PrintCap[A]): PrintCap[A] = body

  def print(msg: Any)(using console: Console): Unit =
    console.print(msg)

  def println(msg: Any)(using console: Console): Unit =
    console.println(msg)

  def run[A](print: PrintCap[A]): A =
    given Console.type = Console
    print

extension [A](print: PrintCap[A])
  def prefix(first: PrintCap[Unit]): PrintCap[A] =
    PrintCap:
      first
      print

  def red: PrintCap[A] =
    PrintCap:
      PrintCap.print(Console.RED)
      print.tap(_ => PrintCap.print(Console.RESET))

@main
def runPrint(): Unit =
  val message: PrintCap[Unit] = PrintCap.println("hello world")
  val red: PrintCap[Unit] = PrintCap.println("Amazing").prefix(PrintCap.print("> ").red)
  PrintCap.run(message)
  PrintCap.run(red)
