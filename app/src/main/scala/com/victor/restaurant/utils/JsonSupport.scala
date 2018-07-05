package com.victor.restaurant.utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.victor.restaurant.controllers.RestaurantId
import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.services.HealthStatus
import com.victor.restaurant.utils.Errors.BaseError
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends DefaultJsonProtocol with SprayJsonSupport  {
  implicit val restaurantFormat: RootJsonFormat[Restaurant] = jsonFormat6(Restaurant)
  implicit val baseErrorFormat: RootJsonFormat[BaseError] = jsonFormat2(BaseError)
  implicit val restaurantIdFormat: RootJsonFormat[RestaurantId] = jsonFormat1(RestaurantId)
  implicit val healthStatusFormat: RootJsonFormat[HealthStatus] = jsonFormat3(HealthStatus)
}
