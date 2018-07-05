package com.victor.restaurant.db

import java.sql.DriverManager

import com.typesafe.config.ConfigFactory
import com.whisk.docker.{DockerCommandExecutor, DockerContainer, DockerContainerState, DockerKit, DockerReadyChecker}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Try
import scala.collection.JavaConverters._


trait DockerPostgresService extends DockerKit {

  val internalPort = 44444
  val externalPort = 5432
  val user = "user"
  val password = "safepassword"
  val database = "mydb"
  val dbUrl = s"jdbc:postgresql://localhost:$internalPort/$database?autoReconnect=true&useSSL=fals&user=$user&password=$password"
  val driver = "org.postgresql.Driver"
  val dockerImage = "victorursan/delivery-postgres:v1"

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick",
    ConfigFactory.parseMap(Map(
      "slick.profile" -> "slick.jdbc.PostgresProfile$",
      "slick.db.driver" -> driver,
      "slick.db.url" -> dbUrl,
      "slick.numThreads" -> "100",
      "connectionPool" -> "HikariCP",
      "registerMbeans" -> "true",
      "poolName" -> database
    ).asJava))

  val postgresContainer = DockerContainer(dockerImage)
    .withPorts(externalPort -> Some(internalPort))
    .withEnv(s"POSTGRES_USER=$user", s"POSTGRES_PASSWORD=$password", s"POSTGRES_DB=$database")
    .withReadyChecker(
      new PostgresReadyChecker(dbUrl, user, password, driver).looped(15, 1.second)
    )

  abstract override def dockerContainers: List[DockerContainer] =
    postgresContainer :: super.dockerContainers
}

class PostgresReadyChecker(url: String,
                           user: String,
                           password: String,
                           driver: String) extends DockerReadyChecker {

  override def apply(container: DockerContainerState
                    )(implicit docker: DockerCommandExecutor, ec: ExecutionContext) =
    container.getPorts().map(_ =>
      Try {
        Class.forName(driver)
        Option(DriverManager.getConnection(url, user, password)).map(_.close).isDefined
      }.getOrElse(false)
    )
}
