package directstyles.async

import gears.async.*
import gears.async.default.given

object Sleeps:
  @main
  def runSleeps() =
    time:
      val n = 10
      Async.blocking:
        (1 to n)
          .map(_ => Future(AsyncOperations.sleep(5000)))
          .awaitAll

  @main
  def runSleepSort() =
    Async.blocking:
      val origin = Seq(200, 50, 80, 10, 60, 40, 100)
      val buf = scala.collection.mutable.ArrayBuffer[Int]()
      origin
        .map: n =>
          Future:
            AsyncOperations.sleep(n)
            buf.synchronized:
              buf += n
        .awaitAll
      println(buf)
