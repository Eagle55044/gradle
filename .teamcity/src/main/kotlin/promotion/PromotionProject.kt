package promotion

import common.Branch
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

class PromotionProject(branch: Branch) : Project({
    id("Promotion")
    name = "Promotion"

    buildType(SanityCheck)
    buildType(PublishNightlySnapshot(branch))
    buildType(PublishNightlySnapshotFromQuickFeedback(branch))
    when (branch) {
        Branch.Master -> {
            buildType(PublishBranchSnapshotFromQuickFeedback)
            buildType(StartReleaseCycle)
            buildType(StartReleaseCycleTest)
            buildType(PublishMilestone)
        }
        Branch.Release -> {
            buildType(PublishReleaseCandidate)
            buildType(PublishFinalRelease)
        }
    }

    params {
        password("env.ORG_GRADLE_PROJECT_gradleS3SecretKey", "dummy")
        password("env.ORG_GRADLE_PROJECT_artifactoryUserPassword", "dummy")
        param("env.ORG_GRADLE_PROJECT_gradleS3AccessKey", "AKIAQBZWBNAJCJGCAMFL")
        password("env.DOTCOM_DEV_DOCS_AWS_SECRET_KEY", "dummy")
        param("env.DOTCOM_DEV_DOCS_AWS_ACCESS_KEY", "AKIAX5VJCER2X7DPYFXF")
        password("env.ORG_GRADLE_PROJECT_sdkmanToken", "dummy")
        param("env.JAVA_HOME", "%linux.java11.openjdk.64bit%")
        param("env.ORG_GRADLE_PROJECT_artifactoryUserName", "bot-build-tool")
        password("env.ORG_GRADLE_PROJECT_infrastructureEmailPwd", "dummy")
        param("env.ORG_GRADLE_PROJECT_sdkmanKey", "dummy")
    }

    buildTypesOrder = arrayListOf(
        SanityCheck
    )
})
