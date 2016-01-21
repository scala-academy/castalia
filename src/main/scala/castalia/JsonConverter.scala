package castalia

import java.io.FileNotFoundException
import java.net.URL

import spray.json._

import scala.util.Try

object JsonConverter {
  def parseJson[T: JsonReader](filename: String): T = {
    val resource: URL = getClass.getResource("/" + filename)
    resource match {
      case url : URL => unmarshalJsonToClass[T](url) getOrElse
        (throw new UnmarshalException(s"Type could not be unmarshelled from $url"))
      case _ => throw new FileNotFoundException(filename)
    }
  }

  protected def unmarshalJsonToClass[T: JsonReader](url: URL): Try[T] =
    Try{
      scala.io.Source.fromFile(url.getPath) // read File
      .mkString // make it a string
      .parseJson // parse the string to Json objects
      .convertTo[T]}
}

case class UnmarshalException(s: String) extends Exception(s)
