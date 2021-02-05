package common

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext

enum class Branch(val buildTypeId: String) {
    // For backward compatibility
    Master("Check3"),
    Release("Release_Check3");

    companion object {
        fun current(): Branch {
            val branch = DslContext.getParameter("Branch")
            return when {
                branch.toLowerCase().contains("master") -> Master
                branch.toLowerCase().contains("release") -> Release
                // TeamCity running DSL with "<placeholder-1>"
                // so you can't throw exception here
                else -> Master
            }
        }
    }
}
