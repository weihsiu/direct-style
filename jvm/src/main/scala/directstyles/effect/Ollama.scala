package directstyles.effect

import directstyles.*
import upickle.default.*
import gears.async.*
import gears.async.default.given
import java.io.InputStreamReader
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import scala.util.boundary
import java.nio.ByteBuffer
import directstyles.async.Channels.*
import ujson.Value
import geny.*
import scala.util.Try
import scala.util.chaining.*

object Ollama:
  type Result[A] = Either[Throwable, A]

  type HttpCapReq = requests.Requester.type
  type HttpCap[A] = Cap[HttpCapReq, A]
  object HttpCap:
    def post[A: Writer, B: Reader](url: String, data: A): HttpCap[Result[B]] =
      Try(read(requests.post(url, data = write(data).tap(println)).text())).toEither

    def postStream[A: Writer, B: Reader](url: String, data: A)(using
        Async.Spawn
    ): HttpCap[ReadableChannel[Result[B]]] =
      val channel = SyncChannel[Result[B]]()
      Future:
        val readable = requests.post.stream(url, data = write(data))
        readable.readBytesThrough(is =>
          val bs = Array.ofDim[Byte](2048)
          var len = -1
          while
            len = is.read(bs)
            len != -1
          do channel.send(Try(read(ByteBuffer.wrap(bs, 0, len))).toEither)
          channel.close()
        )
      channel.asReadable

    def run[A](body: HttpCap[A]): A =
      given requests.Requester.type = requests.Requester
      body

  case class Completion(model: String, response: String, done: Boolean) derives ReadWriter
  case class Message(role: String, content: String, tool_calls: List[ujson.Obj] = List.empty)
      derives ReadWriter
  case class Chat(model: String, message: Message, done: Boolean) derives ReadWriter

  type OllamaCapReq = HttpCapReq
  type OllamaCap[A] = Cap[OllamaCapReq, A]
  object OllamaCap:
    private case class ChatRequest(
        model: String,
        messages: List[Message],
        format: String,
        stream: Boolean,
        tools: List[ujson.Obj]
    ) derives ReadWriter

    def generate(
        baseUrl: String,
        model: String,
        prompt: String,
        format: String = ""
    ): OllamaCap[Result[Completion]] =
      HttpCap.post(
        baseUrl + "/api/generate",
        ujson.Obj("model" -> model, "prompt" -> prompt, "format" -> format, "stream" -> false)
      )

    def generateStream(
        baseUrl: String,
        model: String,
        prompt: String,
        format: String = ""
    )(using Async.Spawn): OllamaCap[ReadableChannel[Result[Completion]]] =
      HttpCap
        .postStream(
          baseUrl + "/api/generate",
          ujson.Obj("model" -> model, "prompt" -> prompt, "format" -> format, "stream" -> true)
        )

    def chat(
        baseUrl: String,
        model: String,
        messages: List[Message],
        tools: List[ujson.Obj] = List.empty,
        format: String = ""
    ): OllamaCap[Result[Chat]] =
      HttpCap.post(
        baseUrl + "/api/chat",
        ChatRequest(model, messages, format, false, tools)
      )

    def chatStream(
        baseUrl: String,
        model: String,
        messages: List[Message],
        tools: List[ujson.Obj] = List.empty,
        format: String = ""
    )(using Async.Spawn): OllamaCap[ReadableChannel[Result[Chat]]] =
      HttpCap.postStream(
        baseUrl + "/api/chat",
        ChatRequest(model, messages, format, true, tools)
      )

    def run[A](body: OllamaCap[A]): A =
      HttpCap.run(body)

  @main
  def runOllama(): Unit =
    // HttpCap.run:
    //   val resp =
    //     HttpCap.post1[String, Map[String, String]]("https://echo.free.beeceptor.com", "hello")
    //   println(resp)
    Async.blocking:
      OllamaCap.run:
        // val r = OllamaCap.chatStream(
        //   "http://localhost:11434",
        //   "llama3.1",
        //   List(Message("user", "tell me joke"))
        // )
        // Future:
        //   boundary:
        //     while true do
        //       r.read() match
        //         case Left(_)  => boundary.break()
        //         case Right(x) => println(x)
        // .await
        val reply = OllamaCap.chat(
          "http://localhost:11434",
          "llama3.1",
          List(Message("user", "What is the weather today in Paris?")),
          List(read[ujson.Obj]("""
    {
      "type": "function",
      "function": {
        "name": "get_current_weather",
        "description": "Get the current weather for a location",
        "parameters": {
          "type": "object",
          "properties": {
            "location": {
              "type": "string",
              "description": "The location to get the weather for, e.g. San Francisco, CA"
            },
            "format": {
              "type": "string",
              "description": "The format to return the weather in, e.g. 'celsius' or 'fahrenheit'",
              "enum": ["celsius", "fahrenheit"]
            }
          },
          "required": ["location", "format"]
        }
      }
    }
          """))
        )

        println(reply)
