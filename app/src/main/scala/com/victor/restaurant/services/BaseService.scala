package com.victor.restaurant.services

import akka.http.scaladsl.server.{Directives, Route}

trait BaseService extends Directives {
  protected val routes: Route
}



