package com.victor.restaurant.db.queries

import com.victor.restaurant.db.entities.Restaurant
import slick.jdbc.JdbcProfile

trait RestaurantsQueries {
  val profile: JdbcProfile
  import profile.api._

  class RestaurantsTable(tag: Tag) extends Table[Restaurant](tag, "restaurants") {

    def * = (id.?, name, description, phoneNumber, cuisinesOffered, address) <> (Restaurant.tupled, Restaurant.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def phoneNumber = column[String]("phone_number")

    def cuisinesOffered = column[String]("cuisines_offered")

    def address = column[String]("address")
  }

  val restaurants = TableQuery[RestaurantsTable]

  protected def addRestaurantQ(restaurant: Restaurant): DBIO[Long] =
    restaurants returning restaurants.map(_.id) += restaurant

  protected def getRestaurantQ(id: Long): DBIO[Option[Restaurant]] =
    restaurants.filter(_.id === id).result.headOption

  protected def getRestaurantsQ: DBIO[Iterable[Restaurant]] =
    restaurants.result

  protected def updateRestaurantQ(oldRestaurantId: Long, newRestaurant: Restaurant): DBIO[Int] =
    restaurants.filter(_.id === oldRestaurantId).update(newRestaurant)

  protected def deleteRestaurantQ(toDeleteId: Long): DBIO[Int] =
    restaurants.filter(_.id === toDeleteId).delete
}

