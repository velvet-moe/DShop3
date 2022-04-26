package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.DynaShopAPI

class OpenShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_OPEN_SHOP
        validArgCount.add(2)
        validArgCount.add(3)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "openshop"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds openshop [shopname] <playername>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        var player: Player? = null
        if (sender is Player) player = sender as Player
        val shopName: String?
        shopName = if (ShopUtil.shopConfigFiles.containsKey(args[1])) {
            args[1]
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.SHOP_NOT_FOUND"))
            return
        }
        var target: Player? = null
        if (args.size == 2) {
            target = player
        }
        if (args.size > 2) {
            target = Bukkit.getPlayer(args[2])
        }
        if (target != null) {
            DynaShopAPI.openShopGui(target, shopName, 1)
        }
    }
}