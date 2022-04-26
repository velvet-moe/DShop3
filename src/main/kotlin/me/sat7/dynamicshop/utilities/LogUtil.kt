package me.sat7.dynamicshop.utilities

import java.io.File

object LogUtil {
    var ccLog: CustomConfig? = null
    fun setupLogFile() {
        if (DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs")) {
            val sdf = SimpleDateFormat("MM-dd-yyyy-HH-mm-ss")
            val timeStr: String = sdf.format(System.currentTimeMillis())
            ccLog.setup("Log_$timeStr", "Log")
            ccLog.get().options().copyDefaults(true)
            ccLog.save()
        }
    }

    // 거래 로그 기록
    fun addLog(shopName: String, itemName: String, amount: Int, value: Double, curr: String, player: String) {
        if (DynamicShop.plugin.getConfig().getBoolean("Log.SaveLogs")) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            if (data.get().contains("Options.log") && data.get().getBoolean("Options.log")) {
                val sdf = SimpleDateFormat("MM-dd-yyyy-HH-mm-ss")
                val timeStr: String = sdf.format(System.currentTimeMillis())
                var i = 0
                if (ccLog.get().contains(shopName)) i = ccLog.get().getConfigurationSection(shopName).getKeys(false).size()
                ccLog.get().set("$shopName.$i", timeStr + "," + itemName + "," + amount + "," + Math.round(value * 100) / 100.0 + "," + curr + "," + player)
                ccLog.save()
            }
            if (ccLog.get().getKeys(true).size() > 1000) {
                setupLogFile()
            }
        }
    }

    fun cullLogs() {
        val logs: Array<File> = File(DynamicShop.plugin.getDataFolder() + "/Log").listFiles() ?: return
        if (logs.size > 0) {
            var deleted = 0
            for (l in logs) {
                val ageMins = (System.currentTimeMillis() - l.lastModified()) as Int / 60000
                if (ageMins > DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes")) {
                    l.delete()
                    deleted++
                }
            }
            if (deleted > 0) {
                DynamicShop.console.sendMessage(((Constants.DYNAMIC_SHOP_PREFIX +
                        " Found and deleted " + deleted + " log file(s) older than " + DynamicShop.plugin.getConfig().getInt("Log.LogCullAgeMinutes")).toString() +
                        " minutes. Checking again in " + DynamicShop.plugin.getConfig().getInt("Log.LogCullTimeMinutes")).toString() + " minutes.")
            }
        }
    }
}