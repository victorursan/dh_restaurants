package com.victor.restaurant.utils

object Errors {
  case class BaseError(message: String, code: String)

  def CannotBeEmpty(who: String) = BaseError(s"The '$who' canno't be empty.", s"invalid_empty_$who")
  val IdPresentError = BaseError("When adding an Item, you cannot set the id.", "id_present_error")
  val NoIdPresentError = BaseError("When updating an Item, you must set the id.", "id_missing_error")
  val NoRestaurantFound = BaseError("No restaurant found for the given id.", "no_restaurant_found")

}
