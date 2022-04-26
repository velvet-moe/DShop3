package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Position : DSCMD() {
    init {
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "position"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> position <pos1 | pos2 | clear>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val player: Player = sender as Player
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (args[3].equalsIgnoreCase("pos1")) {
            shopData.get().set("Options.world", player.getWorld().getName())
            shopData.get().set("Options.pos1", (player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY()).toString() + "_" + player.getLocation().getBlockZ())
            shopData.save()
            player.sendMessage(DynamicShop.dsPrefix(player) + "p1")
        } else if (args[3].equalsIgnoreCase("pos2")) {
            shopData.get().set("Options.world", player.getWorld().getName())
            shopData.get().set("Options.pos2", (player.getLocation().getBlockX() + "_" + player.getLocation().getBlockY()).toString() + "_" + player.getLocation().getBlockZ())
            shopData.save()
            player.sendMessage(DynamicShop.dsPrefix(player) + "p2")
        } else if (args[3].equalsIgnoreCase("clear")) {
            shopData.get().set("Options.world", null)
            shopData.get().set("Options.pos1", null)
            shopData.get().set("Options.pos2", null)
            shopData.save()
            player.sendMessage(DynamicShop.dsPrefix(player) + "clear")
        } else {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_USAGE"))
        }
    }
}