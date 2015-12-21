package castalia

import java.io.FileNotFoundException
import java.net.URL

import spray.json._

import scala.util.Try

/**
  * Created by jens on 13-12-15.
  */
object JsonConverter {
  def parseJson[T: JsonReader](filename: String): T = {
    val resource: Option[URL] = Option(getClass.getResource("/" + filename))
    resource match {
      case Some(url) => unmarshalJsonToClass[T](url) getOrElse
        (throw new UnmarshalException(s"Type could not be unmarshalled from $url"))
      case None => throw new FileNotFoundException(filename)
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
