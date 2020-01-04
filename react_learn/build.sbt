enablePlugins(ScalaJSPlugin)
enablePlugins(ScalaJSBundlerPlugin)

name := "Scala.js Tutorial"
scalaVersion := "2.12.8" // or any other Scala version >= 2.10.2

// core = essentials only. No bells or whistles.
libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.4.2"

npmDependencies in Compile ++= Seq(
    "react" -> "16.7.0",
    "react-dom" -> "16.7.0"
)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true
