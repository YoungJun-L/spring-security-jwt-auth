import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("org.asciidoctor.jvm.convert") apply false
    id("org.jlleitschuh.gradle.ktlint") apply false
}

java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

allprojects {
    group = "${property("projectGroup")}"
    version = "${property("applicationVersion")}"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.asciidoctor.jvm.convert")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudDependenciesVersion")}")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = true
    }

    java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")
    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
            jvmTarget.set(JvmTarget.fromTarget("${project.property("javaVersion")}"))
        }
    }

    tasks.test {
        systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
        useJUnitPlatform {
            excludeTags("restDocs")
        }
    }

    tasks.register<Test>("restDocsTest") {
        group = "verification"
        description = "Runs presentation-layer tests."
        useJUnitPlatform {
            includeTags("restDocs")
            excludeTags("core")
        }
    }

    tasks.register<Test>("applicationTest") {
        group = "verification"
        description = "Runs application-level integration tests."
        useJUnitPlatform {
            systemProperty("kotest.tags.include", "application")
            excludeTags("restDocs")
        }
    }

    tasks.register<Test>("domainTest") {
        group = "verification"
        description = "Runs domain-layer tests."
        useJUnitPlatform {
            systemProperty("kotest.tags.include", "domain")
            excludeTags("restDocs")
        }
    }

    tasks.getByName("asciidoctor") {
        dependsOn("restDocsTest")
    }
}
