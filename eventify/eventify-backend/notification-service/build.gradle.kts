plugins {
    id("org.springframework.boot")
}

val mapstructVersion = "1.6.3"
val lombokMapstructBindingVersion = "0.2.0"
val springDocVersion = "3.0.3"

dependencies {
    implementation(project(":common"))
    implementation(project(":service-security"))
    implementation(project(":kafka-contracts"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.kafka:spring-kafka")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok-mapstruct-binding:$lombokMapstructBindingVersion")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
}