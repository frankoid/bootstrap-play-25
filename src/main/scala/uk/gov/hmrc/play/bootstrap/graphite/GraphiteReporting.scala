/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.play.bootstrap.graphite

import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}

import com.codahale.metrics.graphite.GraphiteReporter
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Logger}

import scala.concurrent.Future

trait GraphiteReporting

class DisabledGraphiteReporting @Inject() extends GraphiteReporting {

  Logger.info("Graphite metrics disabled")
}

@Singleton
class EnabledGraphiteReporting @Inject() (
                                  config: Configuration,
                                  reporter: GraphiteReporter,
                                  lifecycle: ApplicationLifecycle
                                ) extends GraphiteReporting {

  protected def interval: Long = config.getLong("microservice.metrics.graphite.interval").getOrElse(10L)

  Logger.info("Graphite metrics enabled, starting the reporter")

  // start graphite reporter
  reporter.start(interval, TimeUnit.SECONDS)

  // stop graphite reporter on application shut down
  lifecycle.addStopHook {
    () =>
      Future.successful(reporter.stop())
  }
}
