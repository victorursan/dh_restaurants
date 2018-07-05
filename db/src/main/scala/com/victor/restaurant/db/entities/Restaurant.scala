package com.victor.restaurant.db.entities

case class Restaurant(id: Option[Long] = None,
                      name: String,
                      description: String,
                      phoneNumber: String,
                      cuisinesOffered: String,
                      address: String)
