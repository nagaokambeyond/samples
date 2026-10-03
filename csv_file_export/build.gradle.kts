import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.domaframework.doma.compile") version "4.0.3"
    id("org.domaframework.doma.codegen") version "3.2.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("com.opencsv:opencsv:5.12.0")
    implementation("org.seasar.doma.boot:doma-spring-boot-starter:3.0.0")
    implementation("org.seasar.doma:doma-core:3.14.0")
    annotationProcessor("org.seasar.doma:doma-processor:3.14.0")
    runtimeOnly("com.h2database:h2:2.3.232")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    domaCodeGen("com.h2database:h2:2.3.232")
}

tasks.withType<JavaCompile>().configureEach { options.release.set(21) }
tasks.withType<Test>().configureEach { useJUnitPlatform() }

// Uses an in-memory H2 database initialized by the Flyway V1 migration.
// Generated sources are deliberately versioned; do not edit them by hand.
domaCodeGen {
    register("h2") {
        val schema = file("src/main/resources/db/migration/V1__initial_schema.sql").absolutePath
        url.set("jdbc:h2:mem:csv_export_codegen;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM '$schema'")
        user.set("sa")
        password.set("")
        entity {
            packageName.set("com.example.csvexport.generated.entity")
            useAccessor.set(false)
            useListener.set(false)
        }
    }
}
