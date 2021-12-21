plugins {
    id("com.diffplug.spotless") version("6.0.4")
}

repositories {
    mavenCentral()
}

spotless {
    val importOrderConfigFile = project.file("conventions/eclipse.importorder")
    val javaFormatterConfigFile = project.file("conventions/eclipse-formatter-settings.xml")

    java {
        target("hybris/bin/custom/sapcxtools/**/*.java")
        targetExclude("hybris/bin/custom/sapcxtools/**/gensrc/**")
        importOrderFile(importOrderConfigFile)
        eclipse().configFile(javaFormatterConfigFile)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
