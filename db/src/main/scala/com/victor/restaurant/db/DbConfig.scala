package com.victor.restaurant.db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DbConfig {
  val dbConfig: DatabaseConfig[JdbcProfile]
}
