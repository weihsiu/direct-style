package directstyles.effect

import org.openapitools.client.api.DefaultApi
import org.openapitools.client.model.*
import scala.jdk.CollectionConverters.*
import scala.util.chaining.*
import sttp.client4.*
import sttp.model.HttpVersion
import org.json4s.JObject
import org.json4s.*
object ChromaTest:

  extension [T](request: Request[T])
    def send1_1(backend: SyncBackend): Response[T] =
      request.httpVersion(HttpVersion.HTTP_1_1).send(backend)

  @main
  def runChromaTest(): Unit =
    implicit val formats = DefaultFormats
    val backend = DefaultSyncBackend()
    val defaultApi = DefaultApi("http://localhost:8000")
    println(defaultApi.version().send1_1(backend))
    val collection =
      defaultApi
        .createCollection(CreateCollection("collection1", get_or_create = Some(true)), None, None)
        .tap(req => println(req.body))
        .send1_1(backend)
        .body
        .getOrElse(???)
        .extract[Map[String, Any]]
    println(collection)

    // val r1 = defaultApi.upsert(
    //   collection("id"),
    //   AddEmbedding()
    //     .addDocumentsItem(
    //       AddEmbeddingDocumentsInner("This is a document about pineapple")
    //     )
    //     .addDocumentsItem(
    //       AddEmbeddingDocumentsInner("This is a document about oranges")
    //     )
    //     .addIdsItem("id1")
    //     .addIdsItem("id2")
    // )
    // println(r1)
    // val result = defaultApi.getNearestNeighbors(
    //   collection("id"),
    //   QueryEmbedding().addQueryEmbeddingsItem("This is a query document about florida").nResults(2)
    // )
    // println(result)

//   @main
//   def runChromaTest(): Unit =
//     val defaultApi = DefaultApi()
//     defaultApi.setCustomBaseUrl("http://localhost:8000")
//     println(defaultApi.version())
//     val collection = defaultApi
//       .createCollection(CreateCollection().name("collection1").getOrCreate(true), null, null)
//       .asInstanceOf[java.util.Map[String, String]]
//       .asScala
//     println(collection)
//     val r1 = defaultApi.upsert(
//       collection("id"),
//       AddEmbedding()
//         .addDocumentsItem(
//           AddEmbeddingDocumentsInner("This is a document about pineapple")
//         )
//         .addDocumentsItem(
//           AddEmbeddingDocumentsInner("This is a document about oranges")
//         )
//         .addIdsItem("id1")
//         .addIdsItem("id2")
//     )
//     println(r1)
//     val result = defaultApi.getNearestNeighbors(
//       collection("id"),
//       QueryEmbedding().addQueryEmbeddingsItem("This is a query document about florida").nResults(2)
//     )
//     println(result)
// POST /api/v1/collections HTTP/1.1
// Host: localhost:7999
// User-Agent: curl/8.7.1
// Accept: */*
// Content - Type: application / json
// Content - Length: 22
