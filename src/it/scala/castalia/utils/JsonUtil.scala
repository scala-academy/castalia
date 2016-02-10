package castalia.utils

object JsonUtil {

  val indentRegex = "\\s*\\n\\s*".r

  def stripIndent(json: String): String = {
    indentRegex.replaceAllIn(json, "")
  }
}
