package com.victor.restaurant.services

import java.util.concurrent.atomic.AtomicLong

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.victor.restaurant.controllers.{RestaurantId, RestaurantsController}
import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.db.repositories.RestaurantsRepository
import com.victor.restaurant.utils.Errors._
import org.scalatest._

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.util.Try

class RestaurantServiceSpec extends AsyncWordSpec with Matchers with ScalatestRouteTest with RestaurantService {
  import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK, NotFound}

  val restaurant1 = Restaurant(id = Some(1), name = "Name1", description = "description1", phoneNumber = "01235123121", cuisinesOffered = "cuisine1", address = "address1")
  val restaurant2 = Restaurant(id = Some(2), name = "Name2", description = "description2", phoneNumber = "01235123122", cuisinesOffered = "cuisine2", address = "address2")
  val restaurant3 = Restaurant(id = Some(3), name = "Name3", description = "description3", phoneNumber = "01235123123", cuisinesOffered = "cuisine3", address = "address3")
  val restaurant4 = Restaurant(id = Some(4), name = "Name4", description = "description4", phoneNumber = "01235123124", cuisinesOffered = "cuisine4", address = "address4")
  val restaurants = Iterable(restaurant1, restaurant2, restaurant3, restaurant4)

  override val restaurantsController: RestaurantsController = RestaurantsController(new TestRestaurantRepo())

  private class TestRestaurantRepo extends RestaurantsRepository {

    val restaurantsMap: TrieMap[Long, Restaurant] = new TrieMap[Long, Restaurant]()
    var idIndex = new AtomicLong(restaurants.size)

    restaurants.foreach(restaurant => restaurantsMap.put(restaurant.id.get, restaurant))


    override def addRestaurant(restaurant: Restaurant): Future[Long] = {
      val newId: Long = idIndex.incrementAndGet()
      Future.fromTry {
        val newREs = restaurant.copy(id = Some(newId))
        Try(restaurantsMap.put(newId, newREs))

      }.map(_ => newId)
    }

    override def getRestaurant(id: Long): Future[Option[Restaurant]] = Future {
      restaurantsMap.get(id)
    }

    override def getRestaurants: Future[Iterable[Restaurant]] = Future {
      restaurantsMap.values
    }

    override def updateRestaurant(newRestaurant: Restaurant): Future[Int] = Future {
      if (restaurantsMap.contains(newRestaurant.id.get)) {
        restaurantsMap.update(newRestaurant.id.get, newRestaurant)
        1
      } else {
        0
      }
    }

    override def deleteRestaurant(toDeleteId: Long): Future[Int] = Future.successful(restaurantsMap.remove(toDeleteId).map(_ => 1).getOrElse(0))
  }

  "get all restaurants endpoint" should {
    "return all restaurants in Repo" in {
      Get() ~> getRestaurants ~> check {
        status shouldEqual OK
        responseAs[Iterable[Restaurant]] should contain allElementsOf restaurants
      }
    }
  }

  "get a restaurant endpoint" should {
    "get a specific restaurant by id from the repo" in {
      Get("/1") ~> getRestaurant ~> check {
        status shouldEqual OK
        responseAs[Restaurant] shouldEqual restaurant1
      }
      Get("/3") ~> getRestaurant ~> check {
        status shouldEqual OK
        responseAs[Restaurant] shouldEqual restaurant3
      }
    }
    "fail if the id is not present in the repo" in {
      Get("/11") ~> getRestaurant ~> check {
        status shouldEqual NotFound
        responseAs[BaseError] shouldEqual NoRestaurantFound
      }
    }
  }

  "post a restaurant endpoint" should {
    "add a specific restaurant in the repo" in {
      Post("/", restaurant1.copy(id = None)) ~> addRestaurant ~> check {
        status shouldEqual OK
        responseAs[RestaurantId] shouldEqual RestaurantId(5)
      }
    }
    "fail if the restaurant has an id" in {
      Post("/", restaurant1) ~> addRestaurant ~> check {
        status shouldEqual BadRequest
        responseAs[Seq[BaseError]] should contain only IdPresentError
      }
    }
    "fail if the restaurant has some data invalid" in {
      Post("/", restaurant1.copy(id = None, name = "")) ~> addRestaurant ~> check {
        status shouldEqual BadRequest
        responseAs[Seq[BaseError]] should contain only CannotBeEmpty("name")
      }
    }
  }

  "delete a restaurant endpoint" should {
    "delete a specific restaurant" in {
      Delete("/5") ~> deleteRestaurant ~> check {
        status shouldEqual OK
        responseAs[RestaurantId] shouldEqual RestaurantId(5)
      }
    }
    "fail if the id is not present in the repo" in {
      Delete("/11") ~> deleteRestaurant ~> check {
        status shouldEqual NotFound
        responseAs[BaseError] shouldEqual NoRestaurantFound
      }
    }
  }

  "put a restaurant endpoint" should {
    "update a specific restaurant" in {
      Put("/", restaurant1.copy(name = "hello")) ~> updateRestaurant ~> check {
        status shouldEqual OK
        responseAs[Restaurant] shouldEqual restaurant1.copy(name = "hello")
      }
    }
    "fail if the restaurant doesn't have an id" in {
      Put("/", restaurant1.copy(id = None)) ~> updateRestaurant ~> check {
        status shouldEqual BadRequest
        responseAs[Seq[BaseError]] should contain only IdPresentError
      }
    }
    "fail if the restaurant has some data invalid" in {
      Put("/", restaurant1.copy(description = "")) ~> updateRestaurant ~> check {
        status shouldEqual BadRequest
        responseAs[Seq[BaseError]] should contain only CannotBeEmpty("description")
      }
    }
  }
}
