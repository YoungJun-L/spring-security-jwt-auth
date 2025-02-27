dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api")
    compileOnly("org.springframework:spring-jdbc")

    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.restdocs:spring-restdocs-mockmvc")
    api("org.springframework.restdocs:spring-restdocs-restassured")
    api("io.rest-assured:spring-mock-mvc")
    api("com.ninja-squad:springmockk:${property("springMockkVersion")}")
    api("io.kotest:kotest-runner-junit5:${property("kotestVersion")}")
    api("io.kotest.extensions:kotest-extensions-spring:${property("kotestExtensionsSpringVersion")}")
}
