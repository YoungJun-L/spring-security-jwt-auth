allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    testImplementation(project(":tests"))
}

tasks.test {
    systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
    useJUnitPlatform {
        excludeTags("restDocs")
    }
}

tasks.getByName<Test>("restDocsTest") {
    group = "verification"
    description = "Runs presentation-layer tests."
    useJUnitPlatform {
        includeTags("restDocs")
    }
}

tasks.getByName<Test>("applicationTest") {
    group = "verification"
    description = "Runs application-level integration tests."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "application")
        excludeTags("restDocs")
    }
}

tasks.getByName<Test>("domainTest") {
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
