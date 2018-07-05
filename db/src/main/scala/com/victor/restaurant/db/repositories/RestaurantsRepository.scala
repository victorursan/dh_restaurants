package com.victor.restaurant.db.repositories

import com.victor.restaurant.db.DbConfig
import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.db.queries.RestaurantsQueries
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait RestaurantsRepository {
  def addRestaurant(restaurant: Restaurant): Future[Long]
  def getRestaurant(id: Long): Future[Option[Restaurant]]
  def getRestaurants: Future[Iterable[Restaurant]]
  def updateRestaurant(newRestaurant: Restaurant): Future[Int]
  def deleteRestaurant(toDeleteId: Long): Future[Int]
}

class RestaurantsDao(override val dbConfig: DatabaseConfig[JdbcProfile]) extends RestaurantsRepository with RestaurantsQueries with DbConfig {
  override val profile = dbConfig.profile
  protected def run[R] = dbConfig.db.run[R] _

  override def addRestaurant(restaurant: Restaurant): Future[Long] =
    run(addRestaurantQ(restaurant))
  override def getRestaurant(id: Long): Future[Option[Restaurant]] =
    run(getRestaurantQ(id))
  override def getRestaurants: Future[Iterable[Restaurant]] =
    run(getRestaurantsQ)
  override def updateRestaurant(newRestaurant: Restaurant): Future[Int] =
    run(updateRestaurantQ(newRestaurant.id.get, newRestaurant))
  override def deleteRestaurant(toDeleteId: Long): Future[Int] =
    run(deleteRestaurantQ(toDeleteId))
}

object RestaurantsDao {
  def apply(databaseConfig: DatabaseConfig[JdbcProfile]): RestaurantsDao = new RestaurantsDao(databaseConfig)
}
