plugins {
    id("com.diffplug.spotless") version("6.8.0")
    id("org.sonarqube") version("3.3")
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

sonarqube {
    properties {
        property("sonar.log.level", "DEBUG")
        property("sonar.scanner.dumpToFile", "sonar.dump")

        property("sonar.projectKey", "sapcxtools_workspace")
        property("sonar.projectName", "SAP CX Tools Workspace")
        property("sonar.projectVersion", "2.4.0")
        property("sonar.projectBaseDir", project.file("core-customize/hybris/bin/custom/sapcxtools").getAbsolutePath())
        property("sonar.scm.provider", "git")
        property("sonar.organization", "sapcxtools")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", "35e4fdcb5c3cfd5f549df895b0906d2389f9ed61")
        //property("sonar.java.jdkHome", "")

        property("sonar.junit.reportPaths", project.file("core-customize/hybris/log/junit/test-results/unit/index.html").getAbsolutePath() + "," + project.file("core-customize/hybris/log/junit/test-results/integration/index.html").getAbsolutePath())
        property("sonar.coverage.jacoco.xmlReportPaths", project.file("core-customize/hybris/log/junit/jacoco.xml").getAbsolutePath())
        property("sonar.coverage.exclusions", "**/testsrc/**,**/backoffice/src/**")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", ".")
        property("sonar.tests", ".")
        property("sonar.test.inclusions", "**/testsrc/**/*")
        property("sonar.exclusions", "**/testsrc/**,**/gensrc/**,**/constants/*Constants.java,**/jalo/**")

        property("sonar.java.source", "11")
        property("sonar.java.binaries", "sapcommercetoolkit/classes,sapcxbackoffice/classes,sapcxbackoffice/backoffice/classes,sapcxreporting/classes")
        property("sonar.java.test.binaries", "sapcommercetoolkit/classes,sapcxbackoffice/classes,sapcxbackoffice/backoffice/classes,sapcxreporting/classes")
        property("sonar.java.libraries", "**/lib/*.jar")
    }
}