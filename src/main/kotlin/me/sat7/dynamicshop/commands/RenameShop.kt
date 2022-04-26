package me.sat7.dynamicshop.commands

import org.bukkit.command.CommandSender

class RenameShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_RENAME_SHOP
        validArgCount.add(3)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "renameshop"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds renameshop <old name> <new name>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        if (ShopUtil.shopConfigFiles.containsKey(args[1])) {
            val newName: String = args[2].replace("/", "")
            ShopUtil.renameShop(args[1], newName)
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + newName)
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"))
        }
    }
}