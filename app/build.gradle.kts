plugins {
	application
	jacoco
	checkstyle
	id("io.freefair.lombok") version "8.6"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.5"
	id("io.sentry.jvm.gradle") version "6.6.0"
	id("org.sonarqube") version "7.2.2.6593"
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.mapstruct:mapstruct:1.6.0.Beta2")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.0.Beta2")

	implementation("org.instancio:instancio-junit:4.8.1")
	implementation("net.javacrumbs.json-unit:json-unit-assertj:3.3.0")
	implementation("net.datafaker:datafaker:2.2.2")

	runtimeOnly("com.h2database:h2:2.2.224")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:5.11.0-M2"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.11.0-M2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
	toolVersion = "0.8.12"
}

sonar {
	properties {
		property("sonar.projectKey", "KirillSosnyuk_java-project-99")
		property("sonar.organization", "kirillsosnyuk")
		property(
			"sonar.coverage.jacoco.xmlReportPaths",
			layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
				.get()
				.asFile
				.absolutePath
		)
		property(
			"sonar.junit.reportPaths",
			layout.buildDirectory.dir("test-results/test")
				.get()
				.asFile
				.absolutePath
		)
	}
}

sentry {
	includeSourceContext = false

	org = "hexlet-project"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
	reports {
		junitXml.required.set(true)
		junitXml.outputLocation.set(layout.buildDirectory.dir("test-results/test"))
		html.required.set(true)
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
		html.required.set(true)
	}
}