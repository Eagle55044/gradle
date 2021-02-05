/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package promotion

import common.Branch
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

class PublishNightlySnapshot(branch: Branch) : PublishGradleDistribution(
    branch = branch.name.decapitalize(),
    task = branch.promoteNightlyTaskName(),
    triggerName = "ReadyforNightly"
) {
    init {
        this.uuid = when (branch) {
            Branch.Master -> "01432c63-861f-4d08-ae0a-7d127f63096e"
            Branch.Release -> "1f5ca7f8-b0f5-41f9-9ba7-6d518b2822f0"
            else -> throw IllegalArgumentException("Unsupported branch: $branch")
        }
        id("Promotion_${branch.name}Nightly")
        name = "Nightly Snapshot"
        description = "Promotes the latest successful changes on '${branch.name.toLowerCase()}' from Ready for Nightly as a new nightly snapshot"

        triggers {
            schedule {
                schedulingPolicy = daily {
                    this.hour = branch.triggeredHour()
                }
                triggerBuild = always()
                withPendingChangesOnly = false
            }
        }
    }
}

// Avoid two jobs running at the same time and causing troubles
private fun Branch.triggeredHour() = when (this) {
    Branch.Master -> 0
    Branch.Release -> 1
    else -> throw IllegalArgumentException("Unsupported branch: $this")
}
