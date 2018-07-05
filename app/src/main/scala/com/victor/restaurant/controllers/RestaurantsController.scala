package com.victor.restaurant.controllers

import com.typesafe.scalalogging.LazyLogging
import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.db.repositories.{RestaurantsDao, RestaurantsRepository}
import com.victor.restaurant.utils.Errors._
import com.victor.restaurant.validators.RestaurantValidator

import scala.concurrent.ExecutionContext.Implicits.global //this is only used for mapping and recovering
import scala.concurrent.Future

trait RestaurantsController extends LazyLogging {
  protected val restaurantsRepository: RestaurantsRepository

  type FutureE[T] = Future[Either[Seq[BaseError], T]]

  def addRestaurant(restaurant: Restaurant): FutureE[RestaurantId] = {
    RestaurantValidator.validateRestaurantNoId(restaurant) match {
      case Left(errors) => Future.successful(Left(errors))
      case Right(validRestaurant) => restaurantsRepository.addRestaurant(validRestaurant)
        .map(id => Right(RestaurantId(id)))
    }
  }

  def getRestaurant(id: Long): FutureE[Restaurant] = {
    restaurantsRepository.getRestaurant(id).map {
      case Some(restaurant) => Right(restaurant)
      case None => Left(Seq(NoRestaurantFound))
    }
  }

  def getRestaurants: FutureE[Iterable[Restaurant]] = {
    restaurantsRepository.getRestaurants
      .map(Right.apply)
  }

  def updateRestaurant(restaurant: Restaurant): FutureE[Restaurant] = {
    RestaurantValidator.validateRestaurantWithId(restaurant) match {
      case Left(errors) => Future.successful(Left(errors))
      case Right(validRestaurant) => restaurantsRepository.updateRestaurant(validRestaurant)
        .map(rows => if (rows == 0) Left(List(NoRestaurantFound)) else Right(restaurant))
    }
  }

  def deleteRestaurant(id: Long): FutureE[RestaurantId] = {
    restaurantsRepository.deleteRestaurant(id)
      .map(rows => if (rows == 0) Left(List(NoRestaurantFound)) else Right(RestaurantId(id)))
  }
}

object RestaurantsController {
  def apply(): RestaurantsController = new RestaurantsController {
    import com.victor.restaurant.conf.Db.config
    override val restaurantsRepository: RestaurantsRepository = RestaurantsDao(config)
  }

  def apply(restaurantsRepo: RestaurantsRepository): RestaurantsController = new RestaurantsController {
    override val restaurantsRepository: RestaurantsRepository = restaurantsRepo
  }
}

final case class RestaurantId(restaurantId: Long)
