package org.codeswarm.tnsconfig

/** The string identifier of an entry in a tnsnames.ora file.
  */
sealed case class TnsAlias(name: String)
