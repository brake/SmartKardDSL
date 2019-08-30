import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

val kotlintestVersion = "3.3.3"
val slf4jVersion = "1.7.26"
val artifactVersion = "0.1.0"
val sonatypeRepositoryUrl: String by project
val sonatypeUsername: String by project
val sonatypePassword: String by project

plugins {
    java
    `java-library`
    kotlin("jvm") version "1.3.31"
    id("org.jetbrains.dokka") version "0.9.18"
    `maven-publish`
    signing
}

group = "com.github.brake.smart_card"
version = artifactVersion

repositories {
    mavenCentral()
    jcenter()  // dokka
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintestVersion")
    testImplementation("io.kotlintest:kotlintest-runner-console:$kotlintestVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString() //"1.8"
}

tasks {
    jar {
        manifest {
            attributes("Automatic-Module-Name" to "${project.group}.${project.name}")
        }
    }

    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        doFirst {
            file(outputDirectory).deleteRecursively()
        }
    }

    val dokkaJavadoc by creating(DokkaTask::class) {
        dependsOn.add(javadoc)
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
        jdkVersion = 8

        doFirst {
            file(outputDirectory).deleteRecursively()
        }
    }

    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by registering(Jar::class) {
        dependsOn.add(dokkaJavadoc)
        dependsOn.add(dokka)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Smart Card interacting DSL for Kotlin")
                url.set("https://github.com/brake/$artifactId")
                inceptionYear.set("2019")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("brake")
                        name.set("Constantin Roganov")
                        email.set("rccbox@gmail.com")
                        url.set("https://github.com/brake")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:brake/$artifactId")
                    developerConnection.set(connection)
                    url.set(pom.url)
                }
            }
        }
    }
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            val releasesRepoUrl = uri("$buildDir/repos/releases")
            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }

        mavenCentral {
            url = uri(sonatypeRepositoryUrl)
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

