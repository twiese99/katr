plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("net.minecrell.licenser") version "0.4.1"
}

allprojects {

    group = "de.twhx.katr"
    version = "0.1.0"

    if (System.getenv("GITHUB_ACTIONS")?.toBoolean() == true) {
        val ref = System.getenv("GITHUB_REF") ?: ""
        val sha = System.getenv("GITHUB_SHA").subSequence(0, 8)
        version = if (ref.startsWith("refs/tags/release/")) {
            ref.removePrefix("refs/tags/release/")
        } else {
            "$version-${sha}"
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

}

subprojects {

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "net.minecrell.licenser")

    defaultTasks("licenseFormat", "build")

    java {
        withJavadocJar()
    }

    dependencies {
        // Kotlin Implementations
        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))

        // Kotlin Test Implementations
        testImplementation("junit", "junit", "4.12")

        // Java Implementations
        implementation("javax.xml.bind", "jaxb-api", "2.3.1")
        implementation("javax.activation", "activation", "1.1.1")
        implementation("org.glassfish.jaxb", "jaxb-runtime", "2.3.1")
        implementation("org.reflections", "reflections", "0.9.12")

        // Java Test Implementations
        testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.3.1")
        testImplementation("org.junit.jupiter", "junit-jupiter-engine", "5.3.1")
    }

    kapt {
        useBuildCache = false
    }

    license {
        header = project.file("HEADER.txt")

        ext["year"] = 2020
        ext["project_name"] = rootProject.name

        include("**/*.java", "**/*.kt")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom {
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/twhx99/${rootProject.name}/LICENSE")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/twhx99/${rootProject.name}")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        javadoc {
            if (JavaVersion.current().isJava9Compatible) {
                (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            }
        }
        test {
            useJUnitPlatform()
            maxHeapSize = "1G"
        }
        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }

}