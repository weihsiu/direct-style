package directstyles.network

import directstyles.async.Channels
import gears.async.*
import gears.async.default.given
import java.io.*
import java.net.Socket
import scodec.bits.ByteVector
import scodec.codecs.utf8

trait SocketClient:
  def data(socket: Socket)(using
      Async.Spawn
  ): (ReadableChannel[ByteVector], SendableChannel[ByteVector], () => Unit)

object SocketClient:
  def apply(): SocketClient = new SocketClient:
    def data(socket: Socket)(using
        Async.Spawn
    ): (ReadableChannel[ByteVector], SendableChannel[ByteVector], () => Unit) =
      val readableChannel = Channels.inputStreamChannel(socket.getInputStream())
      val sendableChannel = Channels.outputStreamChannel(socket.getOutputStream())
      (
        readableChannel,
        sendableChannel,
        () =>
          readableChannel.close()
          sendableChannel.close()
      )

  @main
  def runSocketClient() =
    def connect()(using Async.Spawn): Unit =
      val socket = Socket("localhost", 8080)
      println("connected")
      val (readable, sendable, stop) = SocketClient().data(socket)
      sendable.send(utf8.encode("hello").getOrElse(???).toByteVector)
      println("sent")
      val resp = utf8.decode(readable.read().right.get.toBitVector).getOrElse(???).value
      println("received")
      assert(resp == "HELLO")

    Async.blocking:
      (1 to 10)
        .map: _ =>
          Future(connect())
        .awaitAll
