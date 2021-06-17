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

package uk.gov.hmrc.eventhub.model

sealed abstract class PublishStatus{
  def isPublishEvent: Boolean = this match {
    case PublishEvent(_) => true
    case _ => false
  }
}

case object DuplicateEvent extends PublishStatus
case object NoSubscribers extends PublishStatus
case object SaveError extends PublishStatus
case class FoundSubscribers(subscribers: List[SubscriberWorkItem]) extends PublishStatus
case class PublishEvent(subscribers: List[SubscriberWorkItem]) extends PublishStatus

