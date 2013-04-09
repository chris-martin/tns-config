tns-config
==========

Scala library for reading `tnsnames.ora`.

[Scaladoc API documentation](http://codeswarm.github.io/tns-config/api/1.0/#org.codeswarm.tnsconfig.package)

Simplest example use
--------------------

```scala
// Get the host, port, and SID of an address associated with our tns alias
val address: Option[TnsAddress] = TnsConfig("our_tns_alias").address
```

Download
--------

tns-config is available from Maven Central.

```xml
<dependency>
  <groupId>org.codeswarm</groupId>
  <artifactId>tns-config_2.10</artifactId>
  <version>1.0</version>
</dependency>
```

