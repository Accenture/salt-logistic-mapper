//test code coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

resolvers += Resolver.bintrayIvyRepo("com.eed3si9n", "sbt-plugins")
//addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")