package org.codeswarm.tnsconfig.error

import org.codeswarm.tnsconfig._

import java.io.File

/** Parent trait for any TNS-related error.
  */
sealed trait TnsError {

  def description: String

}

/** No TNS alias was specified.
  */
case class TnsAliasNotConfigured() extends TnsError {

  override def description = "TNS alias was not configured."

}

/** The tnsnames.ora file does not contain any entry for the requested alias.
  */
case class NoTnsEntryForAlias(alias: String) extends TnsError {

  override def description =
    "No TNS entry found for alias `%s`.".format(alias)

}

/** One or more TNS entries for the alias does exist, but none contains an address.
  */
case class NoAddressForTnsEntry(alias: String) extends TnsError {

  override def description =
    "TNS entry for alias `%s` contains no addresses.".format(alias)

}

/** The content of the TNS names file was retrieved, but could not be parsed.
  */
case class TnsParseFailure(e: Option[Throwable]) extends TnsError {

  override def description = (
    List("Failed to parse TNS names ora file.")
    ++: e.map(_.getStackTraceString)
  ).mkString("\n")

}

case class TnsFileIOError(path: File, e: Option[Throwable]) extends TnsError {

  override def description = (
    List("Failed to read TNS names ora file `%s`.".format(path.getAbsolutePath))
    ++ e.map(_.getStackTraceString)
  ).mkString("\n")

}

case class TnsNamesPathNotConfigured() extends TnsError {

  override def description = "Path of tnsnames.ora file was not configured."

}

case class TnsAdminSystemPropertyMissing() extends TnsError {

  override def description = "System property `%s` is missing.".format(tnsAdminSystemPropertyKey)

}
