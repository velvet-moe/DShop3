package me.sat7.dynamicshop.commands

import org.bukkit.command.CommandSender

class SetDefaultShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SET_DEFAULT_SHOP
        validArgCount.add(2)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "setdefaultshop"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds setdefaultshop <shop name>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        if (ShopUtil.shopConfigFiles.containsKey(args[1])) {
            DynamicShop.plugin.getConfig().set("Command.DefaultShopName", args[1])
            DynamicShop.plugin.saveConfig()
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[1])
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"))
        }
    }
}