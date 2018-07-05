package com.victor.restaurant

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import com.victor.restaurant.conf.HttpConfig
import com.victor.restaurant.services.{HealthService, RestaurantService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object HttpService extends App with Directives with HttpConfig with StrictLogging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future foreach in the end
  implicit val executionContext = system.dispatcher


  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1000))

  val routes: Route = pathPrefix("api" / "v1")  {HealthService.routes ~ RestaurantService.routes}

  Http(system).bindAndHandle(routes, httpInterface, httpPort)
    .onComplete {
      case Success(s) => logger.info(s.toString)
      case Failure(t) => logger.error(s"Failed to bind to $httpInterface:$httpPort!", t)
    }
}
