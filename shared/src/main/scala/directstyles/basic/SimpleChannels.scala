package directstyles.basic

import gears.async.*
import gears.async.default.given
import scala.util.{Random, Try}

object SimpleChannels:
  val random = Random()
  @main
  def runSyncChannel(): Unit =
    Async.blocking:
      val channel = SyncChannel[Int]() // BufferedChannel(), UnboundedChannel()
      Future:
        while true do println(channel.read().getOrElse(???))
      for i <- 1 to 10 do
        channel.send(i)
        AsyncOperations.sleep(random.nextInt(1000))
      channel.close()

  @main
  def runSleepSort2(): Unit =
    val ns = Seq(200, 50, 80, 10, 60, 40, 100)
    Async.blocking:
      val mux = ChannelMultiplexer[Int]()
      ns.foreach(n =>
        val pub = BufferedChannel[Int]()
        mux.addPublisher(pub)
        Future:
          AsyncOperations.sleep(n)
          pub.send(n)
      )
      val sub = BufferedChannel[Try[Int]]()
      mux.addSubscriber(sub)
      Future:
        for _ <- 1 to ns.length do println(sub.read().getOrElse(???).get)
        mux.close()
      mux.run()
