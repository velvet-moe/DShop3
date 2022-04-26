package me.sat7.dynamicshop.commands

import org.bukkit.command.CommandSender

class CommandHelp : DSCMD() {
    init {
        permission = ""
        validArgCount.add(2)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "cmdHelp"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds cmdHelp <on | off>")
        player.sendMessage(" - " + t(player, "HELP.CMD"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val player: Player = sender as Player
        val uuid: UUID = player.getUniqueId()
        if (args[1].equalsIgnoreCase("on")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + "켜짐")
            DynamicShop.userTempData.put(uuid, "")
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp", true)
            DynamicShop.ccUser.save()
        } else if (args[1].equalsIgnoreCase("off")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + "꺼짐")
            DynamicShop.userTempData.put(uuid, "")
            DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp", false)
            DynamicShop.ccUser.save()
        } else {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_USAGE"))
        }
    }
}