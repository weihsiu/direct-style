package directstyles.basic

import gears.async.*
import gears.async.default.given

object StructuredConcurrency:

  @main
  def runSum(): Unit =
    Async.blocking:
      val sum =
        val f1 = Future(scala.io.StdIn.readLine().toInt)
        val f2 = Future(scala.io.StdIn.readLine().toInt)
        f1.await + f2.await
      println(sum)
