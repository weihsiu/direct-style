package directstyles.network

import ox.*
import sttp.tapir.*
import sttp.tapir.server.netty.sync.NettySyncServer

object SimpleHttpServer:
  val hello = endpoint.get
    .in("hello")
    .in(query[String]("name"))
    .out(stringBody)
    .handleSuccess(name => s"hello, $name")

  @main
  def runSimpleHttpServer() =
    supervised:
      val binding = useInScope(NettySyncServer().port(8080).addEndpoint(hello).start())(_.stop())
      println(s"listening on port ${binding.port}")
      never
