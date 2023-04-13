plugins {
    java
    kotlin("multiplatform")
    kotlin("kapt")

    // Flyway is a database migration tool that lets us keep our schemas in the source tree.
    id("org.flywaydb.flyway") version "9.8.1"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
            }
        }
    }
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

val classicPostgresDriver = "org.postgresql:postgresql:42.2.27"
val flywayMigration by configurations.creating

// pljava isn't shipped in Maven Central for some reason, so we bundle it into the repo here.
val libs = dependencies.create(fileTree("libs") { include("*.jar") })
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    flywayMigration(classicPostgresDriver)

    implementation(libs)
    configurations["kapt"](libs)
}

val jdbcURL: String by project

flyway {
    url = jdbcURL
    driver = "org.postgresql.Driver"
    user = System.getProperty("user.name")
    password = ""
    encoding = "UTF-8"
    configurations = arrayOf("flywayMigration")
    locations = arrayOf("filesystem:${projectDir}/src/jvmMain/sql")
    validateMigrationNaming = false
    placeholders = mapOf(
        "backend_jar_path" to if ("//localhost" in jdbcURL) "file:${buildDir}/libs/backend-uber.jar" else "file:/var/lib/postgresql/backend-uber.jar"
    )
}

// So we can ship a single JAR to the server.
tasks.register<Jar>("uberJar") {
    archiveClassifier.set("uber")

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar") && !it.name.startsWith("pljava-api-")
        }.map { zipTree(it) }
    })
    manifest {
        attributes(mapOf(
            "Name" to "pljava.ddr",
            "SQLJDeploymentDescriptor" to "true"
        ))
    }
}
