dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt:${property("jjwtVersion")}")

    testImplementation("org.springframework.security:spring-security-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
}

tasks.test {
    systemProperties = System.getProperties().asIterable().associate { it.key.toString() to it.value }
    useJUnitPlatform {
        excludeTags("securityContext", "restDocs")
    }
}

tasks.register<Test>("securityTest") {
    group = "verification"
    description = "Runs tests for security components."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "security")
        excludeTags("restDocs")
    }
}

tasks.getByName<Test>("restDocsTest") {
    group = "verification"
    description = "Runs presentation-layer tests."
    useJUnitPlatform {
        includeTags("securityContext", "restDocs")
    }
}

tasks.getByName<Test>("applicationTest") {
    group = "verification"
    description = "Runs application-level integration tests."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "application")
        excludeTags("securityContext", "restDocs")
    }
}

tasks.getByName<Test>("domainTest") {
    group = "verification"
    description = "Runs domain-layer tests."
    useJUnitPlatform {
        systemProperty("kotest.tags.include", "domain")
        excludeTags("securityContext", "restDocs")
    }
}

tasks.getByName("asciidoctor") {
    dependsOn("restDocsTest")
}
