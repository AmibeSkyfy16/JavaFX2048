
plugins {
    id("application")
    id ("org.openjfx.javafxplugin") version "0.0.12"
}

group = "ch.skyfy"
version = "1.0-SNAPSHOT"

repositories{
    mavenCentral()
    flatDir {
        dirs = setOf(file("libs"))
    }
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jetbrains:annotations:23.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

application{
    mainClass.set("ch.skyfy.game.Main")
}

javafx{
    version = "18"
}

tasks{

    named<Test>("test"){
        useJUnitPlatform()
    }

    named<JavaExec>("run"){
        jvmArgs = listOf("--add-exports=javafx.controls/com.sun.javafx.scene.control.skin=ALL-UNNAMED")
    }

    named<JavaCompile>("compileJava"){
        options.encoding = "UTF-8"
        options.compilerArgs = listOf("--add-exports=javafx.controls/com.sun.javafx.scene.control.skin=ALL-UNNAMED")
    }

    create<Jar>("buildApplication"){

        archiveFileName.set("${rootProject.name}.jar")

        manifest{
            this.attributes["Main-Class"] = "ch.skyfy.game.Main"
        }

        dependsOn(configurations.runtimeClasspath)

        configurations["runtimeClasspath"].forEach { file: File ->
            from(zipTree(file.absoluteFile))
        }

        from(project.sourceSets.getByName("main").output)
    }

}
