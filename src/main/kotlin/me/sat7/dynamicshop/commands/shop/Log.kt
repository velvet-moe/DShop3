package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Log : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "log"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> log < enable | disable | clear >")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (args[3].equalsIgnoreCase("enable")) {
            shopData.get().set("Options.log", true)
            sender.sendMessage(((DynamicShop.dsPrefix(sender) + shopName).toString() + "/" + t(sender, "LOG.LOG")).toString() + ": " + args[3])
        } else if (args[3].equalsIgnoreCase("disable")) {
            shopData.get().set("Options.log", null)
            sender.sendMessage(((DynamicShop.dsPrefix(sender) + shopName).toString() + "/" + t(sender, "LOG.LOG")).toString() + ": " + args[3])
        } else if (args[3].equalsIgnoreCase("clear")) {
            LogUtil.ccLog.get().set(shopName, null)
            LogUtil.ccLog.save()
            sender.sendMessage((DynamicShop.dsPrefix(sender) + shopName).toString() + "/" + t(sender, "LOG.CLEAR"))
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
            return
        }
        shopData.save()
    }
}