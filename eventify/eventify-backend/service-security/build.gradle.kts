plugins {
    id("java-library")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.1.0")
    }
}

dependencies {
    api("jakarta.servlet:jakarta.servlet-api")

    compileOnly("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    annotationProcessor("org.projectlombok:lombok")
}