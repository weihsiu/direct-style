/**
 * FastAPI
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 0.1.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openapitools.client.model

import org.openapitools.client.model.IncludeEnum._

case class QueryEmbedding(
  where: Option[Any] = None,
  whereDocument: Option[Any] = None,
  queryEmbeddings: Seq[org.json4s.JValue],
  nResults: Option[Int] = None,
  include: Option[Seq[IncludeEnum]] = None
)

