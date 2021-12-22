plugins {
    id("com.diffplug.spotless") version("6.0.4")
    id("sap.commerce.build") version("3.6.0")
    id("sap.commerce.build.ccv2") version("3.6.0")
    id("de.undercouch.download") version("4.1.2")
}

import mpern.sap.commerce.build.tasks.HybrisAntTask
import org.apache.tools.ant.taskdefs.condition.Os

import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.Verify

val DEPENDENCY_FOLDER = "dependencies"
repositories {
    flatDir { dirs(DEPENDENCY_FOLDER) }
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

if (project.hasProperty("sUser") && project.hasProperty("sUserPass")) {
    val SUSER = project.property("sUser") as String
    val SUSERPASS = project.property("sUserPass") as String

    val COMMERCE_VERSION = CCV2.manifest.commerceSuiteVersion
    val commerceSuiteDownloadUrl = project.property("com.sap.softwaredownloads.commerceSuite.${COMMERCE_VERSION}.downloadUrl")
    val commerceSuiteChecksum = project.property("com.sap.softwaredownloads.commerceSuite.${COMMERCE_VERSION}.checksum")

    tasks.register<Download>("downloadPlatform") {
        src(commerceSuiteDownloadUrl)
        dest(file("${DEPENDENCY_FOLDER}/hybris-commerce-suite-${COMMERCE_VERSION}.zip"))
        username(SUSER)
        password(SUSERPASS)
        overwrite(false)
        tempAndMove(true)
        onlyIfModified(true)
        useETag(true)
    }

    tasks.register<Verify>("downloadAndVerifyPlatform") {
        dependsOn("downloadPlatform") 
        src(file("dependencies/hybris-commerce-suite-${COMMERCE_VERSION}.zip"))
        algorithm("SHA-256")
        checksum(commerceSuiteChecksum.toString())
    }

    tasks.named("bootstrapPlatform") {
        dependsOn("downloadAndVerifyPlatform")
    }

    //check if Integration Extension Pack is configured and download it too
    if (CCV2.manifest.useCloudExtensionPack) {
        val INTEXTPACK_VERSION = CCV2.manifest.extensionPacks.first{"hybris-commerce-integrations".equals(it.name)}.version
        val commerceIntegrationsDownloadUrl = project.property("com.sap.softwaredownloads.commerceIntegrations.${COMMERCE_VERSION}.downloadUrl")
        val commerceIntegrationsChecksum = project.property("com.sap.softwaredownloads.commerceIntegrations.${COMMERCE_VERSION}.checksum")
        
        tasks.register<Download>("downloadIntExtPack") {
            src(commerceIntegrationsDownloadUrl)
            dest(file("${DEPENDENCY_FOLDER}/hybris-commerce-integrations-${INTEXTPACK_VERSION}.zip"))
            username(SUSER)
            password(SUSERPASS)
            overwrite(false)
            tempAndMove(true)
            onlyIfModified(true)
            useETag(true)
        }

        tasks.register<Verify>("downloadAndVerifyIntExtPack") {
            dependsOn("downloadIntExtPack")
            src(file("${DEPENDENCY_FOLDER}/hybris-commerce-integrations-${INTEXTPACK_VERSION}.zip"))
            algorithm("SHA-256")
            checksum(commerceIntegrationsChecksum.toString())
        }

        tasks.named("bootstrapPlatform") {
            dependsOn("downloadAndVerifyIntExtPack")
        }
    }
}

tasks.register<WriteProperties>("generateLocalProperties") {
    comment = "GENEREATED AT " + java.time.Instant.now()
    outputFile = project.file("hybris/config/local.properties")
    property("hybris.optional.config.dir", "\${HYBRIS_CONFIG_DIR}/local-config")
    doLast {
        mkdir(project.file("hybris/config/local-config/"))
    }
}

val symlinkConfigTask = tasks.register("symlinkConfig")
val localConfig = file("hybris/config/local-config")
mapOf(
    "10-local.properties" to file("hybris/config/cloud/common.properties"),
    "20-local.properties" to file("hybris/config/cloud/persona/development.properties"),
    "50-local.properties" to file("hybris/config/cloud/local-dev.properties")
).forEach{
    val symlinkTask = tasks.register<Exec>("symlink${it.key}") {
        val path = it.value.relativeTo(localConfig)
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            commandLine("sh", "-c", "ln -sfn ${path} ${it.key}")
        } else {
            // https://blogs.windows.com/windowsdeveloper/2016/12/02/symlinks-windows-10/
            val windowsPath = path.toString().replace("[/]".toRegex(), "\\")
            commandLine("cmd", "/c", """mklink /d "${it.key}" "${windowsPath}" """)
        }
        workingDir(localConfig)
        dependsOn("generateLocalProperties")
    }
    symlinkConfigTask.configure {
        dependsOn(symlinkTask)
    }
}

tasks.register<WriteProperties>("generateLocalDeveloperProperties") {
    dependsOn(symlinkConfigTask)
    comment = "my.properties - add your own local development configuration parameters here"
    outputFile = project.file("hybris/config/local-config/99-local.properties")
    onlyIf {
        !project.file("hybris/config/local-config/99-local.properties").exists()
    }
}

tasks.named("installManifestAddons") {
    mustRunAfter("generateLocalProperties")
}

tasks.register("setupLocalDevelopment") {
    group = "SAP Commerce"
    description = "Setup local development"
    dependsOn("bootstrapPlatform", "generateLocalDeveloperProperties", "installManifestAddons")
}