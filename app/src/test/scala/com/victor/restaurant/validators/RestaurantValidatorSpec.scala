package com.victor.restaurant.validators

import com.victor.restaurant.db.entities.Restaurant
import org.scalatest._

import scala.util.Right

class RestaurantValidatorSpec extends WordSpec with Matchers {
  import RestaurantValidator._
  import com.victor.restaurant.utils.Errors._


  val noIdRestaurant = Restaurant(name = "Name1", description = "description1", phoneNumber = "01235123121", cuisinesOffered = "cuisine1", address = "address1")
  val withIdRestaurant = Restaurant(id = Some(2), name = "Name2", description = "description2", phoneNumber = "01235123122", cuisinesOffered = "cuisine2", address = "address2")
  val noName = Restaurant(id = Some(3), name = "", description = "description3", phoneNumber = "01235123123", cuisinesOffered = "cuisine3", address = "address3")
  val noDescription = Restaurant(name = "Name4", description = "", phoneNumber = "01235123124", cuisinesOffered = "cuisine4", address = "address4")
  val allWrong =  Restaurant(id = Some(3), name = "", description = "", phoneNumber = "", cuisinesOffered = "", address = "")

  "restaurantValidator" should {
    "succeed for valid data" in {
      validateRestaurantNoId(noIdRestaurant) should be(Right(noIdRestaurant))
      validateRestaurantWithId(withIdRestaurant) should be(Right(withIdRestaurant))
    }

    "fail for invalid data" in {
      validateRestaurantWithId(noName) should be(Left(Seq(CannotBeEmpty("name"))))
      validateRestaurantNoId(noDescription) should be(Left(Seq(CannotBeEmpty("description"))))
      validateRestaurantNoId(allWrong).left.get should have size 6
    }
  }

}
