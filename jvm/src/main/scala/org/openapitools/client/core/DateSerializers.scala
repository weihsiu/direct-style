package org.openapitools.client.core

import java.time.{LocalDate, OffsetDateTime}
import java.time.format.DateTimeFormatter

object DateSerializers {
    import org.json4s.{Serializer, CustomSerializer, JNull}
    import org.json4s.JsonAST.JString
    import scala.util.Try
    import java.time.{LocalDateTime, ZoneId}

      case object DateTimeSerializer extends CustomSerializer[OffsetDateTime](_ => ( {
        case JString(s) =>
          Try(OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME)) orElse
            Try(LocalDateTime.parse(s).atZone(ZoneId.systemDefault()).toOffsetDateTime) getOrElse (null)
        case JNull => null
      }, {
        case d: OffsetDateTime =>
          JString(d.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
      }))

      case object LocalDateSerializer extends CustomSerializer[LocalDate]( _ => ( {
        case JString(s) => LocalDate.parse(s)
        case JNull => null
      }, {
        case d: LocalDate =>
         JString(d.format(DateTimeFormatter.ISO_LOCAL_DATE))
      }))

  def all: Seq[Serializer[?]] = Seq[Serializer[?]]() :+ LocalDateSerializer :+ DateTimeSerializer
}
