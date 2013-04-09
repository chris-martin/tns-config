package org.codeswarm.tnsconfig

/** One TCP database connection address pulled from a tnsnames.ora file.
 */
sealed case class TnsAddress(host: String, port: Int, sid: String)

object TnsAddress {

  /** Construct an address from a string map using keys "host", "port", and "sid".
    * Return None if any map entries are missing or if port cannot be parsed as an integer.
    */
  def fromStringMap(x: String => Option[String]): Option[TnsAddress] =
    try {
      Some(new TnsAddress(
        host = x("host").get,
        port = x("port").get.toInt,
        sid  = x("sid").get
      ))
    } catch {
      case nsee: NoSuchElementException => None
      case nfe: NumberFormatException => None
    }

}
