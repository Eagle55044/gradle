package projects

import common.Branch
import common.failedTestArtifactDestination
import configurations.StagePasses
import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import model.CIBuildModel
import model.FunctionalTestBucketProvider
import model.Stage
import model.StatisticsBasedPerformanceTestBucketProvider
import promotion.PromotionProject
import util.UtilPerformanceProject
import util.UtilProject
import java.io.File

class RootProject(
    model: CIBuildModel,
    functionalTestBucketProvider: FunctionalTestBucketProvider
) : Project({
    // GradleBuildTool_Master
    uuid = model.projectId
    // GradleBuildTool_Master
    id = AbsoluteId(model.projectId)
    // Master
    name = model.branch.name
    // GradleBuildTool
    parentId = AbsoluteId(model.rootProjectId)
    val performanceTestBucketProvider = StatisticsBasedPerformanceTestBucketProvider(model, File("performance-test-durations.json"), File("performance-tests-ci.json"))

    params {
        param("credentialsStorageType", "credentialsJSON")
        param("teamcity.ui.settings.readOnly", "true")
        param("env.GRADLE_ENTERPRISE_ACCESS_KEY", "%ge.gradle.org.access.key%")
    }

    var prevStage: Stage? = null
    model.stages.forEach { stage ->
        val stageProject = StageProject(model, functionalTestBucketProvider, performanceTestBucketProvider, stage, uuid)
        val stagePasses = StagePasses(model, stage, prevStage, stageProject)
        buildType(stagePasses)
        subProject(stageProject)
        prevStage = stage
    }

    subProject(PromotionProject(model.branch))
    if (model.branch == Branch.Master) {
        subProject(UtilProject)
        subProject(UtilPerformanceProject)
    }

    buildTypesOrder = buildTypes
    subProjectsOrder = subProjects

    cleanup {
        baseRule {
            history(days = 14)
        }
        baseRule {
            artifacts(
                days = 14, artifactPatterns = """
                +:**/*
                +:$failedTestArtifactDestination/**/*"
            """.trimIndent()
            )
        }
    }
})
