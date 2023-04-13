plugins {
    java
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    // The Jooq Gradle plugin generates the code under src/generated/jooq, which acts as a type safe interface to the database.
    id("nu.studer.jooq") version "8.1"
    // Conveyor handles desktop deployment.
    id("dev.hydraulic.conveyor") version "1.4"
}

version = "1.7"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                // Compose UI toolkit
                implementation(compose.desktop.currentOs)
                val voyagerVersion = "1.0.0-rc04"
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")

                implementation(project(":common"))

                // PostgreSQL driver, connection pooling and the jOOQ SQL binding/wrapping layer.
                //
                // We use the NG driver for its better async notifications support.
                // But we use the classic driver for the flyway migration, because the NG driver doesn't seem to work with it.
                implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:0.8.9")
                val jooqVer = "3.17.5"
                implementation("org.jooq:jooq:$jooqVer")
                implementation("org.jooq:jooq-kotlin:$jooqVer")
                implementation("org.jooq:jooq-kotlin-coroutines:$jooqVer")
                implementation("com.zaxxer:HikariCP:4.0.3")
            }
        }
        val jvmTest by getting
    }
}

// region Compose and Conveyor related build config
dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

compose.desktop {
    application {
        mainClass = "dev.hydraulic.bugzino.frontend.app.AppKt"
    }
}

// Work around a Compose/Gradle bug.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

// Forcibly align Kotlin versions. This will go away in a future Conveyor Gradle plugin version.
dependencies {
    val v = "1.8.10"
    for (m in setOf("linuxAmd64", "macAmd64", "macAarch64", "windowsAmd64")) {
        m("org.jetbrains.kotlin:kotlin-stdlib:$v")
        m("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$v")
        m("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$v")
    }
}
// endregion

// region Database related build config

val classicPostgresDriver = "org.postgresql:postgresql:42.2.27"
dependencies {
    jooqGenerator(classicPostgresDriver)
}

val jdbcURL: String by project

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

jooq {
    version.set("3.17.0")
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = jdbcURL
                    user = System.getProperty("user.name")
                    password = ""
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                        inputSchema = "public"
                    }
                    generate.apply {
                    }
                    target.apply {
                        packageName = "dev.hydraulic.bugzino.frontend.db"
                        directory = "src/generated/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

// configure jOOQ task such that it only executes when something has changed that potentially affects the generated JOOQ sources
// - the jOOQ configuration has changed (Jdbc, Generator, Strategy, etc.)
// - the classpath used to execute the jOOQ generation tool has changed (jOOQ library, database driver, strategy classes, etc.)
// - the schema files from which the schema is generated and which is used by jOOQ to generate the sources have changed (scripts added, modified, etc.)
tasks.named<nu.studer.gradle.jooq.JooqGenerate>("generateJooq").configure {
    // Ensure database schema has been prepared by Flyway before generating the jOOQ sources
    dependsOn(project(":backend").tasks.named("flywayMigrate"))

    // Declare Flyway migration scripts as inputs on the jOOQ task.
    inputs.files(fileTree(project(":backend").projectDir.resolve("src/main/sql")))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)

    allInputsDeclared.set(true)
}
// endregion
