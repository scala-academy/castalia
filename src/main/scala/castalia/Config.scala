package castalia

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()

  private val httpConfig = config.getConfig("http")
  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")

  val managementHttpInterface = config.getString("castalia.management.interface")
  val managementHttpPort = config.getInt("castalia.management.port")
}
