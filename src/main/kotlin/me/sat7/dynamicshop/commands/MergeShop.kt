package me.sat7.dynamicshop.commands

import org.bukkit.command.CommandSender

class MergeShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_MERGE_SHOP
        validArgCount.add(3)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "mergeshop"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds mergeshop <shop1> <shop2>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        if (args[1].equals(args[2])) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
            return
        }
        if (ShopUtil.shopConfigFiles.containsKey(args[1]) && ShopUtil.shopConfigFiles.containsKey(args[2])) {
            ShopUtil.mergeShop(args[1], args[2])
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[1])
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"))
        }
    }
}