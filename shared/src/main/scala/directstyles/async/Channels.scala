package directstyles.async

import gears.async.*
import gears.async.Channel.Closed
import gears.async.default.given
import java.io.*
import scodec.bits.ByteVector
import scala.util.control.Breaks.*

object Channels:
  def inputStreamChannel(
      inputStream: InputStream,
      maxSize: Int = 1024,
      capacity: Int = 100
  )(using Async.Spawn): ReadableChannel[ByteVector] & Closeable =
    val channel = BufferedChannel[ByteVector](capacity)
    Future:
      var ba: Array[Byte] = null
      var len = -1
      while
        ba = Array.ofDim[Byte](maxSize)
        len = inputStream.read(ba)
        len != -1
      do channel.send(ByteVector.view(ba, 0, len))
    channel

  def outputStreamChannel(
      outputStream: OutputStream,
      capacity: Int = 100
  )(using Async.Spawn): SendableChannel[ByteVector] & Closeable =
    val channel = BufferedChannel[ByteVector](capacity)
    Future:
      breakable:
        while true do
          channel.read() match
            case Left(_) => break
            case Right(bs) =>
              outputStream.write(bs.toArray)
              outputStream.flush()
    channel

  extension [A](readable: ReadableChannel[A])
    def map[B](f: A => B): ReadableChannel[B] = new ReadableChannel[B]:
      val readSource = readable.readSource.transformValuesWith(_.map(f))

  extension [A](sendable: SendableChannel[A])
    def contramap[B](f: B => A): SendableChannel[B] = new SendableChannel[B]:
      def sendSource(x: B): Async.Source[Either[Closed.type, Unit]] =
        sendable.sendSource(f(x))
