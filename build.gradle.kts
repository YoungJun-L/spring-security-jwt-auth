import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("java-library")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.asciidoctor.jvm.convert")
    id("org.jlleitschuh.gradle.ktlint")
}

java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")
tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
        jvmTarget.set(JvmTarget.fromTarget("${project.property("javaVersion")}"))
    }
}

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudDependenciesVersion")}")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.jsonwebtoken:jjwt:${property("jjwtVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:${property("springMockkVersion")}")
    testImplementation("io.kotest:kotest-runner-junit5:${property("kotestVersion")}")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:${property("kotestExtensionsSpringVersion")}")
    testImplementation("io.kotest:kotest-extensions-now:${property("kotestVersion")}")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
    testImplementation("io.rest-assured:spring-mock-mvc")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
}

tasks.test {
    systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
    useJUnitPlatform()
}

tasks.register<Test>("securityTest") {
    group = "verification"
    description = "Runs tests for security components."
    useJUnitPlatform {
        includeTags("security")
    }
}

tasks.register<Test>("restDocsTest") {
    group = "verification"
    description = "Runs presentation-layer tests."
    useJUnitPlatform {
        includeTags("restDocs")
    }
}

tasks.register<Test>("applicationTest") {
    group = "verification"
    description = "Runs application-level integration tests."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "application")
        excludeTags("security", "restDocs")
    }
}

tasks.register<Test>("domainTest") {
    group = "verification"
    description = "Runs domain-layer tests."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "domain")
        excludeTags("security", "restDocs")
    }
}

tasks.register<Test>("unitTest") {
    group = "verification"
    description = "Runs unit tests."
    useJUnitPlatform {
        excludeTags("security", "restDocs")
        systemProperty("kotest.tags.exclude", "application,domain")
    }
}

tasks.getByName("asciidoctor") {
    dependsOn("securityTest")
    dependsOn("restDocsTest")
}
