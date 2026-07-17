plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("maven-publish")

}


repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(26)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand(props)
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
