import org.apache.tools.ant.taskdefs.condition.Os

import de.undercouch.gradle.tasks.download.Download

import java.time.Instant
import java.util.Base64

plugins {
    id("sap.commerce.build") version("3.7.1")
    id("sap.commerce.build.ccv2") version("3.7.1")
    id("de.undercouch.download") version("4.1.2")
}

val DEPENDENCY_FOLDER = "../dependencies"
repositories {
    flatDir { dirs(DEPENDENCY_FOLDER) }
    mavenCentral()
}

if (project.hasProperty("SAPCX_ARTEFACT_BASEURL") && project.hasProperty("SAPCX_ARTEFACT_USER") && project.hasProperty("SAPCX_ARTEFACT_PASSWORD")) {
    val BASEURL = project.property("SAPCX_ARTEFACT_BASEURL") as String
    val USER = project.property("SAPCX_ARTEFACT_USER") as String
    val PASSWORD = project.property("SAPCX_ARTEFACT_PASSWORD") as String
    val AUTHORIZATION = Base64.getEncoder().encodeToString((USER + ":" + PASSWORD).toByteArray())

    val COMMERCE_VERSION = CCV2.manifest.commerceSuiteVersion
    tasks.register<Download>("downloadPlatform") {
        src(BASEURL + "/hybris-commerce-suite/${COMMERCE_VERSION}.zip")
        dest(file("${DEPENDENCY_FOLDER}/hybris-commerce-suite-${COMMERCE_VERSION}.zip"))
        header("Authorization", "Basic ${AUTHORIZATION}")
        overwrite(false)
        tempAndMove(true)
        onlyIfModified(true)
        useETag(true)
    }

    tasks.named("bootstrapPlatform") {
        dependsOn("downloadPlatform")
    }

    //check if Integration Extension Pack is configured and download it too
    if (CCV2.manifest.extensionPacks.any{"hybris-commerce-integrations".equals(it.name)}) {
        val INTEXTPACK_VERSION = CCV2.manifest.extensionPacks.first{"hybris-commerce-integrations".equals(it.name)}.version        
        tasks.register<Download>("downloadIntExtPack") {
            src(BASEURL + "/hybris-commerce-integrations/${INTEXTPACK_VERSION}.zip")
            dest(file("${DEPENDENCY_FOLDER}/hybris-commerce-integrations-${INTEXTPACK_VERSION}.zip"))
            header("Authorization", "Basic ${AUTHORIZATION}")
            overwrite(false)
            tempAndMove(true)
            onlyIfModified(true)
            useETag(true)
        }

        tasks.named("bootstrapPlatform") {
            dependsOn("downloadIntExtPack")
        }
    }
}

tasks.register<WriteProperties>("generateLocalProperties") {
    comment = "FILE WAS GENERATED AT " + Instant.now()
    outputFile = project.file("hybris/config/local.properties")
    property("hybris.optional.config.dir", project.file("hybris/config/local-config").absolutePath)
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

tasks.register<Exec>("symlinkLocalSSOConfiguration") {
    val link = "95-local.properties"
    val path = file("hybris/config/cloud/local-sso.properties").relativeTo(localConfig)

    if (project.file("../certificates/local.cxdev.me.p12").exists() &&
        project.file("../certificates/cxdev_eu_auth0_com-metadata.xml").exists()) {

        if (Os.isFamily(Os.FAMILY_UNIX)) {
            commandLine("sh", "-c", "ln -sfn ${path} ${link}")
        } else {
            // https://blogs.windows.com/windowsdeveloper/2016/12/02/symlinks-windows-10/
            val windowsPath = path.toString().replace("[/]".toRegex(), "\\")
            commandLine("cmd", "/c", """mklink /d "${link}" "${windowsPath}" """)
        }
    } else {
        if (Os.isFamily(Os.FAMILY_UNIX)) {
            commandLine("sh", "-c", "unlink ${link}")
        } else {
            commandLine("cmd", "/c", """rmdir "${link}" """)
        }
    }
    workingDir(localConfig)
    dependsOn(symlinkConfigTask)
}

tasks.register<WriteProperties>("generateLocalDeveloperProperties") {
    dependsOn("symlinkLocalSSOConfiguration")
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