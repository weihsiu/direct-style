package directstyles.async

import gears.async.*
import gears.async.default.given
import scala.compiletime.ops.double

object Futures:
  def interruptableSleep(millis: Int)(using Async.Spawn): Future[Unit] =
    Future:
      AsyncOperations.sleep(millis)

  def uninterruptableSleep(millis: Int)(using Async.Spawn): Future[Unit] =
    Future:
      uninterruptible(AsyncOperations.sleep(millis))

  def evalMany(times: Int, body: => Unit)(using Async): Unit =
    assert(times >= 0)
    (1 to times).foreach(n =>
      body
      checkInterruption()
    )

  @main
  def runInterruptableSleep() =
    time:
      Async.blocking:
        // val sleep = interruptableSleep(5000)
        val sleep = uninterruptableSleep(5000)
        sleep.cancel()

  @main
  def runEvalMany() =
    Async.blocking:
      val future = Future(evalMany(1000, println("hello")))
      future.cancel()

  @main
  def runCancelAwake() =
    Async.blocking:
      // exit immediately because there is no await for the Future
      Future:
        try Future(AsyncOperations.sleep(5000)).await
        catch case _: CancellationException => println("cancelled")
    println("done")
