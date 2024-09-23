package directstyles.network

import sttp.tapir.*
import sttp.tapir.CodecFormat.*
import sttp.tapir.server.netty.sync.{NettySyncServer, OxStreams}
import directstyles.basic.ContextFunction.A

object WebSocketServer:
  type Id[A] = A

  val wsEndpoint =
    endpoint.get.in("echo").out(webSocketBody[String, TextPlain, String, TextPlain](OxStreams))
  val wsProcessor: OxStreams.Pipe[String, String] = _.map(msg => s"you said: $msg")
  val wsServerEndpoint = wsEndpoint.serverLogicSuccess[Id](_ => wsProcessor)
  @main
  def runEchoWebSocketServer() = NettySyncServer().addEndpoint(wsServerEndpoint).startAndWait()
