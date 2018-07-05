package com.victor.restaurant.services

import java.lang.management.ManagementFactory

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import com.victor.restaurant.conf.Db
import com.victor.restaurant.utils.JsonSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global //this is only used for mapping and recovering

trait HealthService extends BaseService with JsonSupport with LazyLogging {

  def healthStatus: Future[HealthStatus]

  val routes: Route = pathPrefix("healthcheck") {
    get {
      logger.info("[GET] .../healthcheck executed")
      onSuccess(healthStatus) { status =>
        complete(OK -> status)
      }
    }
  }
}

object HealthService extends HealthService {

  override def healthStatus: Future[HealthStatus] = {
    import Db.config.profile.api._
    lazy val uptime = ManagementFactory.getRuntimeMXBean.getUptime
    val dbConfigStr = Db.config.config.toString
    val healthStatus: Boolean => HealthStatus = HealthStatus(uptime, _, dbConfigStr)
    Db.config.db.run(Query(1).result).map(_ => healthStatus(true))
      .recover { case t =>
        logger.error(t.getMessage, t)
        healthStatus(false)
      }
  }
}

case class HealthStatus(upTime: Long, dbUp: Boolean, dbConfig: String)
