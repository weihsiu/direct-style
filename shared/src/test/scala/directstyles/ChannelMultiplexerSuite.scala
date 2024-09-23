package directstyles

import gears.async.*
import gears.async.default.given
import scala.compiletime.ops.double
import gears.async.Channel.Closed
import scala.util.Try

class ChannelMultiplexerSuite extends munit.FunSuite:
  test("multiplexer"):
    Async.blocking:
      val pub = BufferedChannel[Int](10)
      // val pub = SyncChannel[Int]()
      val sub = BufferedChannel[Try[Int]](10)
      // val sub = SyncChannel[Try[Int]]()
      val mux = ChannelMultiplexer[Int]()
      mux.addPublisher(pub)
      mux.addSubscriber(sub)
      Future(mux.run())
      Future:
        (1 to 3).foreach(pub.send)
        // mux.close()
        // println("mux is closed")
      // Future:
      //   while true do
      //     val n = pub.read().right.get
      //     println(s"*** $n received")
      Future:
        while true do
          sub.read() match
            case Left(Closed) => println("channel closed")
            case Right(x)     => println(s"${x.toEither} received")
      AsyncOperations.sleep(5000)
      pub.close()
    println("done")
