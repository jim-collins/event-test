/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.eventhub.connector

import com.google.common.net.HttpHeaders
import com.google.inject.Inject
import play.api.libs.ws.WSClient
import uk.gov.hmrc.eventhub.model.Event

import scala.concurrent.{ExecutionContext, Future}
import play.api.http.MimeTypes._
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse

class EventConnector @Inject()(ws: WSClient)(implicit ec: ExecutionContext){

  def postEvent(event: Event, url: String): Future[WSResponse] = {
    ws.url(url).withHttpHeaders(acceptJsonHeader).post(Json.toJson(event))
  }

  val acceptJsonHeader = HttpHeaders.ACCEPT -> JSON
}
