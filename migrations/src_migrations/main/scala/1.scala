import com.liyaos.forklift.slick.DBIOMigration
import com.victor.restaurant.db.queries.RestaurantsQueries
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object M1 extends RestaurantsQueries {
  override val profile = DatabaseConfig.forConfig[JdbcProfile]("slick").profile
  import profile.api._
  Migrations.migrations = Migrations.migrations :+ DBIOMigration(1)(
    DBIO.seq(restaurants.schema.create))
}
