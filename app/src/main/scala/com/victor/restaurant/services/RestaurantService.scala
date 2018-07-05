package com.victor.restaurant.services

import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.LazyLogging
import com.victor.restaurant.controllers.RestaurantsController
import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.utils.Errors.NoRestaurantFound
import com.victor.restaurant.utils.JsonSupport

trait RestaurantService extends BaseService with JsonSupport with LazyLogging {
  import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK, NotFound}
  val restaurantsController: RestaurantsController

  override val routes: Route = pathPrefix("restaurants") {
    updateRestaurant ~ deleteRestaurant ~ getRestaurant ~ getRestaurants ~ addRestaurant
  }

  def getRestaurant: Route = (get & pathPrefix(LongNumber)) { id =>
    logger.info("[GET] .../restaurants/{} executed", id)
    onSuccess(restaurantsController.getRestaurant(id)) {
      case Right(restaurant) => complete(OK -> restaurant)
      case Left(errors) if errors.contains(NoRestaurantFound) => complete(NotFound -> NoRestaurantFound)
      case Left(errors) => complete(BadRequest -> errors)
    }
  }

  protected def getRestaurants: Route = get {
    logger.info("[GET] .../restaurants/ executed")
    onSuccess(restaurantsController.getRestaurants) {
      case Right(restaurants) => complete(OK -> restaurants)
      case Left(errors) => complete(BadRequest -> errors)
    }
  }

  protected def addRestaurant: Route = (post & entity(as[Restaurant])) { restaurant =>
    logger.info("[POST] .../restaurants/ executed")
    onSuccess(restaurantsController.addRestaurant(restaurant)) {
      case Right(restaurantId) => complete(OK -> restaurantId)
      case Left(errors) => complete(BadRequest -> errors)
    }
  }

  protected def updateRestaurant: Route = (put & entity(as[Restaurant])) { restaurant =>
    logger.info("[PUT] .../restaurants/ executed")
    onSuccess(restaurantsController.updateRestaurant(restaurant)) {
      case Right(updatedRestaurant) => complete(OK -> updatedRestaurant)
      case Left(errors) if errors.contains(NoRestaurantFound) => complete(NotFound -> NoRestaurantFound)
      case Left(errors) => complete(BadRequest -> errors)
    }
  }

  protected def deleteRestaurant: Route = (delete & pathPrefix(LongNumber)) { restaurantId: Long =>
    logger.info("[DELETE] .../restaurants/{} executed", restaurantId)
    onSuccess(restaurantsController.deleteRestaurant(restaurantId)) {
      case Right(deletedId) => complete(OK -> deletedId)
      case Left(errors) if errors.contains(NoRestaurantFound) => complete(NotFound -> NoRestaurantFound)
      case Left(errors) => complete(BadRequest -> errors)
    }
  }
}

object RestaurantService extends RestaurantService {
  override val restaurantsController = RestaurantsController()
}
