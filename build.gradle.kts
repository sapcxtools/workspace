plugins {
    id("com.diffplug.spotless") version("6.8.0")
}

repositories {
    mavenCentral()
}

spotless {
    val importOrderConfigFile = project.file("core-customize/conventions/eclipse.importorder")
    val javaFormatterConfigFile = project.file("core-customize/conventions/eclipse-formatter-settings.xml")

    java {
        target("core-customize/hybris/bin/custom/sapcxtools/**/*.java")
        targetExclude("core-customize/hybris/bin/custom/sapcxtools/**/gensrc/**")
        importOrderFile(importOrderConfigFile)
        eclipse().configFile(javaFormatterConfigFile)
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}