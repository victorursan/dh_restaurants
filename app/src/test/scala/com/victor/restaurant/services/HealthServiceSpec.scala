package com.victor.restaurant.services

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{WordSpec, Matchers}

import scala.concurrent.Future

class HealthServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {

  import akka.http.scaladsl.model.StatusCodes.OK

  val goodHealth = HealthStatus(100, true, "abcd")
  val badHealth = HealthStatus(200, false, "dcba")

  trait GoodStatus extends HealthService {

    override def healthStatus: Future[HealthStatus] = Future.successful(goodHealth)
  }

  trait BadStatus extends HealthService {

    override def healthStatus: Future[HealthStatus] = Future.successful(badHealth)
  }

  "get health endpoint" should {
    "be good in some cases" in new GoodStatus {
      Get("/healthcheck") ~> routes ~> check {
        status shouldEqual OK
        responseAs[HealthStatus] should be(goodHealth)
      }
    }
    "bad in other cases" in new BadStatus {
      Get("/healthcheck") ~> routes ~> check {
        status shouldEqual OK
        responseAs[HealthStatus] should be(badHealth)
      }
    }
  }


}
