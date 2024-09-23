package directstyles.network

import ox.*
import sttp.client4.*

object SimpleHttpClient:
  @main
  def runSimpleHttpClient() =
    supervised:
      forkUser:
        val req = basicRequest.get(uri"http://localhost:8080/hello?name=walter")
        val backend = DefaultSyncBackend()
        val resp = req.send(backend)
        println(resp)
      .join()
