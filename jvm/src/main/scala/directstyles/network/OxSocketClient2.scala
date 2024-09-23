package directstyles.network

import java.io.*
import java.net.*
import ox.*
import scala.util.Try

object OxSocketClient2:
  def connect(host: String, port: Int): Unit =
    supervised:
      useInScope(Try(Socket(host, port)).toEither)(_.foreach(_.close())) match
        case Left(error) => error.printStackTrace()
        case Right(socket) =>
          try
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            writer.write("hello\n")
            writer.flush()
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val response = reader.readLine()
            assert(response == "hello")
          catch case error => println(error)

  @main
  def runOxSocketClient2() =
    val n = 150
    supervised:
      val result = forkAll((1 to n).map(_ => () => connect("localhost", 8080)))
        .join()
      assert(result.length == n)
