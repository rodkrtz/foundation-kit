import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
    `java-library`
}

// ============================================
// Project Information
// ============================================
group = "com.rodkrtz"
version = "1.2.0"

// ============================================
// Java/Kotlin Configuration
// ============================================
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(21)

    explicitApi()

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)

        apiVersion.set(KotlinVersion.KOTLIN_2_0)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)

        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-opt-in=kotlin.RequiresOptIn"
        )

        allWarningsAsErrors.set(false)
        javaParameters.set(true)
    }
}

// ============================================
// Repositories
// ============================================
repositories {
    mavenCentral()
}

// ============================================
// Dependencies
// ============================================
dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

val githubUsernameProvider = providers
    .environmentVariable("GITHUB_USERNAME")
    .orElse(providers.gradleProperty("gpr.user"))

val githubTokenProvider = providers
    .environmentVariable("GITHUB_TOKEN")
    .orElse(providers.gradleProperty("gpr.token"))

// ============================================
// Publishing Configuration
// ============================================
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rodkrtz/foundation-kit")

            credentials {
                username = githubUsernameProvider.orNull
                password = githubTokenProvider.orNull
            }
        }
    }

    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])

            groupId = "com.rodkrtz"
            artifactId = "foundation-kit"
            version = project.version.toString()

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("Foundation Kit")
                description.set("A comprehensive architectural toolkit for Kotlin projects following DDD principles")
                url.set("https://github.com/rodkrtz/foundation-kit")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("rodkrtz")
                        name.set("Rodrigo")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/rodkrtz/foundation-kit.git")
                    developerConnection.set("scm:git:ssh://github.com/rodkrtz/foundation-kit.git")
                    url.set("https://github.com/rodkrtz/foundation-kit")
                }
            }
        }
    }
}

// ============================================
// Testing Configuration
// ============================================
tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = false
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    ignoreFailures = false
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

// ============================================
// Clean Configuration
// ============================================
tasks.clean {
    delete("out")
    delete(".kotlin")
}

// ============================================
// Custom Tasks
// ============================================
tasks.register("verifyCredentials") {
    group = "verification"
    description = "Verify that GitHub credentials are configured"

    doLast {
        val username = githubUsernameProvider.orNull
        val token = githubTokenProvider.orNull

        if (username.isNullOrBlank() || token.isNullOrBlank()) {
            throw GradleException(
                """
                GitHub credentials not found!
                
                Please configure credentials in one of these ways:

                1. ~/.gradle/gradle.properties:
                   gpr.user=your-username
                   gpr.token=your-token
                
                2. Environment variables:
                   export GITHUB_USERNAME=your-username
                   export GITHUB_TOKEN=your-token
            """.trimIndent()
            )
        }
    }
}

tasks.named("publish") {
    dependsOn("verifyCredentials")
}

tasks.register("projectInfo") {
    group = "help"
    description = "Display project information"

    doLast {
        println(
            """
            ================================================
            Project: ${project.name}
            Group: ${project.group}
            Version: ${project.version}
            Kotlin Version: ${kotlin.coreLibrariesVersion}
            Java Version: ${java.sourceCompatibility}
            ================================================
        """.trimIndent()
        )
    }
}
