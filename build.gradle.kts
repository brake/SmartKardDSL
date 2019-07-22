import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlintestVersion: String by project
val slf4jVersion: String by project
val artifactVersion: String by project

plugins {
    java
    kotlin("jvm") version "1.3.31"
}

group = "com.github.brake.smart_card"
version = artifactVersion

repositories {
    mavenCentral()
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