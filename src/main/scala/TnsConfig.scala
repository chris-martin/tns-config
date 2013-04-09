package org.codeswarm.tnsconfig

import java.io.{IOException, File}
import org.codeswarm.fallible.{Failure, Success, Fallible}
import org.codeswarm.orafile.OrafileDict

import error._

/** @param path Filesystem path of the `.ora` file that was parsed.
  * @param dict Entire result of parsing the tns names in `.ora` format.
  * @param alias Alias name that was requested.
  * @param address One address corresponding to the requested alias.
  * @param errors All problems encountered, such as misconfiguration and parsing errors.
  * Note that a nonempty errors list does not necessarily indicate failure.
  */
case class TnsConfig(
  path: Option[File],
  dict: OrafileDict,
  alias: Option[String],
  address: Option[TnsAddress],
  errors: List[TnsError]
)

object TnsConfig {

  def apply(alias: String): TnsConfig = apply(alias = Some(alias))

  /** @param path Filesystem location of a TNS names `.ora` file to parse. If `None` is given, then a
    * `TnsNamesPathNotConfigured` error is generated, and we attempt to proceed using the `System` default
    * instead.
    * @param alias alias Name of the TNS alias for which an address is desired. If `None` is given, then a
    * `TnsAliasNotConfigured` error is generated, and the result will not contain an address.
    * @param readFile Used to read a file's contents. The default value for this argument is generally
    * sufficient, but it can be overridden for testing.
    */
  def apply( path: Option[File] = None, alias: Option[String] = None,
             readFile: File => String = DefaultFileReader.apply ): TnsConfig = {

    var errors = List[TnsError]()

    def unwrap[A](x: Fallible[A, TnsError]): Option[A] = {
      errors ++= x.getErrors
      x.toOption
    }

    val actualPath = unwrap(
      Fallible(path, { TnsNamesPathNotConfigured() }) || systemPropertyTnsNamesPath()
    )

    val dictOption: Option[OrafileDict] = actualPath flatMap { p =>
      unwrap(
        (try {
          Success(readFile(p))
        } catch {
          case e: IOException => Failure(TnsFileIOError(p, Some(e)))
        }) flatMap (parseTnsNames(_))
      )
    }

    unwrap(Fallible(alias, { TnsAliasNotConfigured() }))

    val address: Option[TnsAddress] = dictOption flatMap { dict =>
      alias flatMap { a =>
        unwrap(dict.getAddresses_nonEmpty(a) map { _.head })
      }
    }

    TnsConfig(
      path = actualPath,
      dict = dictOption getOrElse { new OrafileDict() },
      alias = alias,
      address = address,
      errors = errors
    )

  }

  object DefaultFileReader {
    def apply(file: File): String = io.Source.fromFile(file).mkString
  }

}
