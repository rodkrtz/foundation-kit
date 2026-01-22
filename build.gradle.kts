plugins {
    kotlin("jvm") version "2.2.21"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.rodkrtz"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

// Dokka configuration for API documentation
tasks.dokkaHtml.configure {
    outputDirectory.set(layout.buildDirectory.dir("dokka"))
    
    dokkaSourceSets {
        configureEach {
            includeNonPublic.set(false)
            skipEmptyPackages.set(true)
            reportUndocumented.set(false)
            
            perPackageOption {
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }
        }
    }
}

// Maven publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.rodkrtz"
            artifactId = "foundation-kit"
            version = "1.1.0"
            
            from(components["java"])
            
            pom {
                name.set("Foundation Kit")
                description.set("A comprehensive architectural toolkit for Kotlin projects with CQRS, DDD, and Event-Driven patterns")
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
                        name.set("Rodrigo Kreutzfeld")
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
