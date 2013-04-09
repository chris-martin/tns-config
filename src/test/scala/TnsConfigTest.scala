package org.codeswarm.tnsconfig

import org.scalatest.FreeSpec
import java.io.{FileNotFoundException, File}

import error._

class TnsConfigTest extends FreeSpec {

  "good tns config" - {

    val tnsFileContent = "TheApplication =\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1521))\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1522))\n" +
      "  (CONNECT_DATA =(SID = banana)(SERVER = dedicated)))"

    def readFile(file: File): String =
      if (file == new File("testnames.ora")) tnsFileContent
      else throw new UnsupportedOperationException

    val tnsConfig = TnsConfig(
      path = Some(new File("testnames.ora")),
      alias = Some("TheApplication"),
      readFile = readFile
    )

    val address = tnsConfig.address.get

    assert(address == TnsAddress(host = "app-server", port = 1521, sid = "banana"))

  }

  "IO exception and no tns alias" - {

    def readFile(file: File): String = throw new FileNotFoundException()

    val tnsConfig = TnsConfig(
      path = Some(new File("nothing.ora")),
      readFile = readFile
    )

    val errors = tnsConfig.errors

    assert(errors.size == 2)
    assert(errors.exists(e => e.isInstanceOf[TnsFileIOError]))
    assert(errors.exists(e => e.isInstanceOf[TnsAliasNotConfigured]))
  }

  "Alias with no tns entry" - {

    val tnsFileContent = "TheApplication =\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1521))\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1522))\n" +
      "  (CONNECT_DATA =(SID = banana)(SERVER = dedicated)))"

    def readFile(file: File): String =
      if (file == new File("testnames.ora")) tnsFileContent
      else throw new UnsupportedOperationException

    val tnsConfig = TnsConfig(
      path = Some(new File("testnames.ora")),
      alias = Some("Apples"),
      readFile = readFile
    )

    val errors = tnsConfig.errors

    assert(errors.size == 1)
    assert(errors.exists(e => e.isInstanceOf[NoTnsEntryForAlias]))

  }

  "TNS entry with no address" - {

    val tnsFileContent = "TheApplication =\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 15f21))\n" +
      "  (ADDRESS = (PROTOCOL = TCP)(HOST = app-server)(PORT = 1q522))\n" +
      "  (CONNECT_DATA =(SID = banana)(SERVER = dedicated)))"

    def readFile(file: File): String =
      if (file == new File("testnames.ora")) tnsFileContent
      else throw new UnsupportedOperationException

    val tnsConfig = TnsConfig(
      path = Some(new File("testnames.ora")),
      alias = Some("TheApplication"),
      readFile = readFile
    )

    val errors = tnsConfig.errors

    assert(errors.size == 1)
    assert(errors.exists(e => e.isInstanceOf[NoAddressForTnsEntry]))

  }

}
