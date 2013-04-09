package org.codeswarm

import java.io.File
import scala.collection.JavaConversions._
import org.codeswarm.fallible.{Failure, Success, Fallible}
import org.codeswarm.orafile.{OrafileVal, OrafileDict, Orafile}

/** This package utilizes the parsing capability of `org.codeswarm.orafile` to provide an
  * API specific to the TNS names file "tnsnames.ora".
  *
  * The project was designed to support a simple use case: Given a particular TNS alias,
  * retrieve a single [[org.codeswarm.tnsconfig.TnsAddress TnsAddress]].
  *
  * {{{
  * val address: Option[TnsAddress] = TnsConfig("your_database_alias").address
  * }}}
  *
  * See `apply` methods on the [[org.codeswarm.tnsconfig.TnsConfig$ TnsConfig object]]
  * for more options, and the [[org.codeswarm.tnsconfig.TnsConfig TnsConfig class]]
  * for more detail on all of the output information it contains.
  */
package object tnsconfig {

  import error._

  val tnsNamesFilename = "tnsnames.ora"

  val tnsAdminSystemPropertyKey = "oracle.net.tns_admin"

  def systemPropertyTnsNamesPath(): Fallible[File, TnsAdminSystemPropertyMissing] =
    tnsAdminSystemProperty() map { path => new File(new File(path), tnsNamesFilename) }

  def tnsAdminSystemProperty(): Fallible[String, TnsAdminSystemPropertyMissing] =
    Fallible(Option(System getProperty tnsAdminSystemPropertyKey), { new TnsAdminSystemPropertyMissing() })

  /** Parse the content of a tnsnames.ora file.
    */
  def parseTnsNames(tnsNames: String): Fallible[OrafileDict, TnsParseFailure] =
    try { Success(Orafile.parse(tnsNames)) }
    catch { case e: org.antlr.runtime.RecognitionException => Failure(new TnsParseFailure(Some(e))) }

  /** Wraps the `OrafileDict` from a tnsnames.ora file.
    */
  implicit class TnsNamesOrafileDict(dict: OrafileDict) {

    /** Non-empty list of Ora values corresponding to `alias`.
      */
    def get_nonEmpty(alias: String): Fallible[List[OrafileVal], NoTnsEntryForAlias] =
      dict.get(alias).toList match {
        case xs if xs.nonEmpty => Success(xs)
        case _ => Failure(new NoTnsEntryForAlias(alias))
      }

    /** Non-empty list of addresses corresponding to `alias`.
      */
    def getAddresses_nonEmpty(alias: String): Fallible[List[TnsAddress], TnsError] =
      get_nonEmpty(alias) flatMap (_
        flatMap { x => x.findParamAttrs("address", List("host", "port", "sid")) }
        flatMap { x => TnsAddress.fromStringMap(x.toMap.get(_)) }
        match {
          case xs if xs.nonEmpty => Success(xs)
          case _ => Failure(new NoAddressForTnsEntry(alias))
        }
      )
  }

}
