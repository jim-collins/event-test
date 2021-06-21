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

package uk.gov.hmrc.eventhub.controllers

import cats.effect.IO
import play.api.libs.json.{JsError, JsResult, JsValue, Json}
import play.api.mvc.{Action, ActionBuilder, AnyContent, AnyContentAsEmpty, BaseController, BaseControllerHelpers, BodyParser, BodyParsers, ControllerComponents, Request, Result}
import uk.gov.hmrc.eventhub.model.{Event, SaveError, Subscriber}
import uk.gov.hmrc.eventhub.service.PublishEventService

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventHubController @Inject()(val controllerComponents: ControllerComponents,
                                   eventService: PublishEventService)
                                  (implicit ec: ExecutionContext)extends BaseController with PlayIO {
//
//  def publishEvent(topic: String) = Action.async(parse.json) { implicit request =>
//    val event: JsResult[Event] = request.body.validate[Event]
//    event.fold(
//      errors => {
//        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
//      },
//      e => {
//        eventService.processEvent(topic, e).map{ r =>
//          if ( r == SaveError) InternalServerError
//          else Created(s"$r")
//        }
//      }
//    )
//  }

  def publishEvent(topic: String) = Action.async(parse.json) { implicit request =>
    val event: JsResult[Event] = request.body.validate[Event]
    event.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      e => {
        eventService.processEvent(topic, e).map{ r =>
          if ( r == SaveError) InternalServerError
          else Created(s"$r")
        }
      }.unsafeToFuture()
    )
  }

  def testSubsriber(topic: String) = Action(parse.json) { implicit request =>
    println(s"received event for topic $topic, body ${request.body}")
    topic match {
      case "fail" => InternalServerError
      case _ => Accepted
    }
  }

  def index = Action.async {implicit request =>
    Future.successful(Accepted)
  }

  def indexIO = Action.io{implicit request =>

    IO.pure(Accepted)
  }
}

trait PlayIO { self: BaseControllerHelpers =>
  implicit class IOActionBuilder[A](actionBuilder: ActionBuilder[Request, A]) {
    def io(block: Request[A] => IO[Result]): Action[A] = {
      actionBuilder.apply(block.andThen(_.unsafeRunSync()))
    }
    def parseIO(bodyParser: BodyParser[A])(block: Request[A] => IO[Result]): Action[A] = {
      actionBuilder.apply(bodyParser)(block.andThen(_.unsafeRunSync()))
    }
  }
}