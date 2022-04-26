package me.sat7.dynamicshop.commands

import java.util.UUID

class DeleteUser : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_DELETE_OLD_USER
        validArgCount.add(2)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "§c§ldeleteOldUser§f§r"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds deleteOldUser <days>")
        player.sendMessage(" - " + t(player, "HELP.DELETE_OLD_USER"))
        player.sendMessage(" - " + t(player, "MESSAGE.IRREVERSIBLE"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val day: Long
        try {
            day = Long.parseLong(args[1])
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            return
        }
        if (day <= 0) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.VALUE_ZERO"))
            return
        }
        var count = 0
        for (s in DynamicShop.ccUser.get().getKeys(false)) {
            try {
                val lastJoinLong: Long = DynamicShop.ccUser.get().getLong("$s.lastJoin")
                val dayPassed: Long = (System.currentTimeMillis() - lastJoinLong) / 86400000L

                // 마지막으로 접속한지 입력한 일보다 더 지남.
                if (dayPassed > day) {
                    sender.sendMessage((DynamicShop.dsPrefix(sender) + Bukkit.getOfflinePlayer(UUID.fromString(s)).getName()).toString() + " Deleted")
                    DynamicShop.ccUser.get().set(s, null)
                    count += 1
                }
            } catch (e: Exception) {
                sender.sendMessage((DynamicShop.dsPrefix(sender) + e).toString() + "/" + s)
            }
            DynamicShop.ccUser.save()
        }
        sender.sendMessage((DynamicShop.dsPrefix(sender) + count).toString() + " Items Removed")
    }
}