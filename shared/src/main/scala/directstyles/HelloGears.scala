package directstyles

import gears.async.*
import gears.async.default.given

object HelloGears:
  @main
  def runHello() =
    Async.blocking:
      val hello = Future:
        print("hello")
      val world = Future:
        AsyncOperations.sleep(1000)
        hello.await
        println(", world")
      world.await
