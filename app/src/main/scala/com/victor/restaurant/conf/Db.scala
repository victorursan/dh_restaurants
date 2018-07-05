package com.victor.restaurant.conf

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object Db {
  val config = DatabaseConfig.forConfig[JdbcProfile]("slick")
}
