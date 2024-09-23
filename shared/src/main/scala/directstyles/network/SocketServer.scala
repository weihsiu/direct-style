package directstyles.network

import directstyles.async.Channels
import gears.async.*
import gears.async.Async.Source
import gears.async.Channel.Closed
import gears.async.default.given
import scodec.bits.ByteVector
import scodec.codecs.*

import java.io.*
import java.net.*
import java.util.concurrent.atomic.AtomicBoolean
import scala.compiletime.ops.double
import scala.concurrent.duration.*
import scala.util.Try
import scala.util.Using
import scala.util.control.Breaks.*

trait SocketServer:
  def sockets(serverSocket: ServerSocket, capacity: Int = 1000)(using
      Async.Spawn
  ): ReadableChannel[Socket] & Closeable
  def data(socket: Socket, capacity: Int = 10)(using
      Async.Spawn
  ): (ReadableChannel[ByteVector], SendableChannel[ByteVector], () => Unit)
  def start(port: Int)(using Async.Spawn): () => Unit

object SocketServer:
  def apply(): SocketServer = new SocketServer:
    def sockets(serverSocket: ServerSocket, capacity: Int)(using
        Async.Spawn
    ): ReadableChannel[Socket] & Closeable =
      val socketChannel = BufferedChannel[Socket](capacity)
      Future:
        while true do
          val socket = serverSocket.accept()
          socketChannel.send(socket)
      socketChannel

    def data(socket: Socket, capacity: Int)(using
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

    def start(port: Int)(using Async.Spawn): () => Unit =
      val continue = AtomicBoolean(true)
      Future:
        Using(ServerSocket(port)): serverSocket =>
          while continue.get() do
            Future:
              Using(serverSocket.accept()): socket =>
                println(s"accepting connection on ${Thread.currentThread().getName()}")
                Future:
                  val request =
                    BufferedReader(InputStreamReader(socket.getInputStream())).readLine()
                  val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                  writer.write(request)
                  writer.flush()
                .await
            .await
      () => continue.set(false)

@main
def runSocketServerStart() =
  Async.blocking:
    val stop = SocketServer().start(8080)
    AsyncOperations.sleep(60.seconds)
    stop()

@main
def runSocketServerSockets() =
  val socketServer = SocketServer()
  Async.blocking:
    val serverSocket = ServerSocket(8080)
    val sockets = socketServer.sockets(serverSocket)
    Async
      .race(
        Future:
          println("press <enter> to quit")
          scala.io.StdIn.readLine()
          sockets.close()
          serverSocket.close()
        ,
        Future:
          breakable:
            while true do
              sockets.read() match
                case Left(_) => break
                case Right(socket) =>
                  Future:
                    val (readable, sendable, stop) = socketServer.data(socket)
                    val bs = readable.read().right.get
                    val data = utf8.decodeValue(bs.toBitVector).getOrElse(???)
                    println(s"receiving $data")
                    sendable.send(utf8.encode(data.toUpperCase()).getOrElse(???).toByteVector)
                    AsyncOperations.sleep(500)
                    stop()
                    socket.close()
      )
      .await
