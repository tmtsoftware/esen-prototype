import com.typesafe.sbt.SbtNativePackager.Universal

lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  `esen-prototype-assembly`,
  `esen-prototype-hcd`,
  `esen-prototype-deploy`
)

lazy val `esen-prototype` = project
  .in(file("."))
  .aggregate(aggregatedProjects: _*)

lazy val `esen-prototype-assembly` = project
  .settings(
    libraryDependencies ++= Dependencies.EsenprototypeAssembly
  )

lazy val `esen-prototype-hcd` = project
  .settings(
    libraryDependencies ++= Dependencies.EsenprototypeHcd
  )

lazy val `esen-prototype-deploy` = project
  .dependsOn(
    `esen-prototype-assembly`,
    `esen-prototype-hcd`
  )
  .enablePlugins(JavaAppPackaging, CswBuildInfo)
  .settings(
    libraryDependencies ++= Dependencies.EsenprototypeDeploy,
    // This is the placeholder for setting JVM options via sbt native packager.
    // You can add more JVM options below.
//    javaOptions in Universal ++= Seq(
//      // -J params will be added as jvm parameters
//      "-J-Xmx8GB",
//      "J-XX:+UseG1GC", // G1GC is default in jdk9 and above
//      "J-XX:MaxGCPauseMillis=30" // Sets a target for the maximum GC pause time. This is a soft goal, and the JVM will make its best effort to achieve it
//    )
  )
