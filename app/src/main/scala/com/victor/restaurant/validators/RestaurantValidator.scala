package com.victor.restaurant.validators

import com.victor.restaurant.db.entities.Restaurant
import com.victor.restaurant.utils.Errors._

import scala.collection.immutable._


object RestaurantValidator {

  def validateRestaurantNoId(restaurant: Restaurant): Either[Seq[BaseError], Restaurant] =
    validateRestaurant(id => if (id.isDefined) Seq(IdPresentError) else Nil)(restaurant)

  def validateRestaurantWithId(restaurant: Restaurant): Either[Seq[BaseError], Restaurant] =
    validateRestaurant(id => if (id.isEmpty) Seq(IdPresentError) else Nil)(restaurant)

  private def validateRestaurant(validateId: Option[Long] => Seq[BaseError])(restaurant: Restaurant) = {
    val possibleErrors = validateId(restaurant.id) ++ getPossibleErrors(restaurant)
    toEither(possibleErrors, restaurant)
  }

  private def toEither(possibleErrors: Seq[BaseError], restaurant: Restaurant): Either[Seq[BaseError], Restaurant] =
    if (possibleErrors.isEmpty) {
      Right(restaurant)
    } else {
      Left(possibleErrors)
    }

  private def getPossibleErrors(restaurant: Restaurant) =
    Seq(validateName(restaurant.name), validateAddress(restaurant.address), validateDescription(restaurant.description),
      validatePhoneNumber(restaurant.phoneNumber), validateCuisinesOffered(restaurant.cuisinesOffered)).flatten

  private def validateName(name: String): Seq[BaseError] = validateNonEmpty("name", name)

  private def validateAddress(address: String): Seq[BaseError] = validateNonEmpty("address", address)

  private def validateDescription(description: String): Seq[BaseError] = validateNonEmpty("description", description)

  private def validatePhoneNumber(phoneNumber: String): Seq[BaseError] = validateNonEmpty("phoneNumber", phoneNumber)

  private def validateCuisinesOffered(cuisinesOffered: String): Seq[BaseError] = validateNonEmpty("cuisinesOffered", cuisinesOffered)

  private def validateNonEmpty(property: String, value: String): Seq[BaseError] =
    if (value.trim.isEmpty) Seq(CannotBeEmpty(property)) else Nil


}
