plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("kapt") version "2.4.20"
    application
    id("org.domaframework.doma.compile") version "4.0.3"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seasar.doma:doma-kotlin:3.14.0")
    kapt("org.seasar.doma:doma-processor:3.14.0")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
    implementation("net.jthink:jaudiotagger:3.0.1")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("com.example.mp3info.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
