name := "tns-config"

version := "1.1-SNAPSHOT"

scalaVersion := "2.10.0"

organization := "org.codeswarm"

organizationHomepage := Some(url("https://github.com/codeswarm"))

homepage := Some(url("https://github.com/codeswarm/tns-config"))

licenses := Seq(
  "The Apache Software License, Version 2.0" ->
  url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  Some(
  if (v.trim.endsWith("SNAPSHOT"))
    "snapshots" at nexus + "content/repositories/snapshots"
  else
    "releases" at nexus + "service/local/staging/deploy/maven2"
  )
}

pomExtra := {
  val org = "codeswarm"
  val repo = "tns-config"
  <scm>
    <url>https://github.com/{org}/{repo}</url>
    <connection>scm:git:git://github.com/{org}/{repo}.git</connection>
    <developerConnection>scm:git:ssh://git@github.com/{org}/{repo}.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>chris-martin</id>
      <name>Chris Martin</name>
      <url>https://github.com/chris-martin</url>
    </developer>
  </developers>
}

libraryDependencies ++= Seq(
  "org.codeswarm" % "orafile" % "1.0",
  "org.codeswarm" %% "fallible" % "1.1",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
)
