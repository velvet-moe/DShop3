package me.sat7.dynamicshop.jobshook

import com.gamingmesh.jobs.container.JobsPlayer

object JobsHook {
    var jobsRebornActive = false

    // JobsReborn의 points 수정
    fun addJobsPoint(p: Player, amount: Double): Boolean {
        if (!jobsRebornActive) {
            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.JOBS_REBORN_NOT_FOUND"))
            return false
        }
        val pp: PlayerPoints = getJobsPlayerPoints(p)
        // 차감
        return if (amount < 0.0) {
            if (pp.havePoints(amount * -1)) {
                pp.takePoints(amount * -1)
                true
            } else {
                p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "MESSAGE.NOT_ENOUGH_POINT")
                        .replace("{bal}", n(getCurJobPoints(p))))
                false
            }
        } else {
            pp.addPoints(amount)
            true
        }
    }

    // JobsReborn. 플레이어의 잔액 확인
    fun getCurJobPoints(p: Player?): Double {
        val jobsPlayer: JobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p)
        return jobsPlayer.getPointsData().getCurrentPoints()
    }

    fun getJobsPlayerPoints(p: Player?): PlayerPoints {
        val jobsPlayer: JobsPlayer = Jobs.getPlayerManager().getJobsPlayer(p)
        return jobsPlayer.getPointsData()
    }
}