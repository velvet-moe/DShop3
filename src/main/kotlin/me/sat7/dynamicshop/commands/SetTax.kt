package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.utilities.ConfigUtil

class SetTax : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SET_TAX
        validArgCount.add(2)
        validArgCount.add(4)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "settax"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds settax <value>")
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "settax temp"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds settax temp <tax_value> <minutes_until_reset>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        if (args.size == 2) {
            try {
                val newValue: Int = Clamp(Integer.parseInt(args[1]), 1, 99)
                DynamicShop.plugin.getConfig().set("Shop.SalesTax", newValue)
                DynamicShop.plugin.saveConfig()
                ConfigUtil.setCurrentTax(newValue)
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + newValue)
            } catch (e: Exception) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            }
        } else if (args.size == 4 && args[1].equals("temp")) {
            try {
                val newValue: Int = Clamp(Integer.parseInt(args[2]), 1, 99)
                var tempTaxDurationMinutes: Int = Integer.parseInt(args[3])
                if (tempTaxDurationMinutes <= 1) tempTaxDurationMinutes = 1
                ConfigUtil.setCurrentTax(newValue)
                class ResetTaxTask : BukkitRunnable() {
                    @Override
                    fun run() {
                        ConfigUtil.resetTax()
                    }
                }
                if (resetTaxTask != null) {
                    resetTaxTask.cancel()
                    resetTaxTask = null
                }
                resetTaxTask = ResetTaxTask()
                resetTaxTask.runTaskLater(DynamicShop.plugin, 20L * 60L * tempTaxDurationMinutes)
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + newValue)
            } catch (e: Exception) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            }
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
        }
    }

    companion object {
        private var resetTaxTask: BukkitRunnable? = null
    }
}