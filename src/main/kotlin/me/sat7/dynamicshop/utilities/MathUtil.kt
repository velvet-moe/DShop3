package me.sat7.dynamicshop.utilities

import me.sat7.dynamicshop.guis.UIManager
import me.sat7.dynamicshop.jobshook.JobsHook
import kotlin.Throws
import me.sat7.dynamicshop.UpdateChecker

object MathUtil {
    fun RoundDown(value: Double): Int {
        var intNum = value.toInt()
        if (intNum.toDouble() != value) return intNum
        if (value < 10) return intNum
        var temp = 10
        for (i in 0..6) {
            if (intNum % temp != 0 && intNum > temp) {
                intNum = intNum / temp * temp
                break
            }
            temp *= 10
        }
        if (intNum < 1) intNum = 1
        return intNum
    }

    fun Clamp(value: Int, min: Int, max: Int): Int {
        if (value < min) return min else if (value > max) return max
        return value
    }

    fun Clamp(value: Double, min: Double, max: Double): Double {
        if (value < min) return min else if (value > max) return max
        return value
    }

    fun Clamp(value: Long, min: Long, max: Long): Long {
        if (value < min) return min else if (value > max) return max
        return value
    }

    fun SafeAdd(a: Int, b: Int): Int {
        val temp = a + b
        if (b > 0) {
            if (a > Integer.MAX_VALUE - b - 1) return Integer.MAX_VALUE - 1
        } else {
            if (a < Integer.MIN_VALUE - b) return Integer.MIN_VALUE
        }
        return temp
    }

    fun TickToMilliSeconds(tick: Long): Long {
        return tick * 50
    }

    fun MilliSecondsToTick(ms: Long): Long {
        return ms / 50
    }
}