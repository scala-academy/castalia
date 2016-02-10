sonatypeProfileName := "com.github.scala-academy"

// To sync with Maven central, you need to supply the following information:
pomExtra in Global := {
  <url>https://github.com/scala-academy/castalia</url>
    <licenses>
      <license>
        <name>MIT</name>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/scala-academy/castalia.git</connection>
      <developerConnection>scm:git:git@github.com/scala-academy/castalia.git</developerConnection>
      <url>https://github.com/scala-academy/castalia</url>
    </scm>
    <developers>
    </developers>
}