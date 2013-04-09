tns-config
==========

Scala library for reading `tnsnames.ora`.

Simplest use case:

```scala
// Get the host, port, and SID of an address associated with our tns alias
val address: Option[TnsAddress] = TnsConfig("our_tns_alias").address
```
