package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Enable : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "enable"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> <true|false>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (args[3].equalsIgnoreCase("true")) {
            shopData.get().set("Options.enable", true)
            sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + t(sender, "SHOP_SETTING.STATE")).toString() + ":" + args[3])
        } else if (args[3].equalsIgnoreCase("false")) {
            shopData.get().set("Options.enable", false)
            sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + t(sender, "SHOP_SETTING.STATE")).toString() + ":" + args[3])
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
            return
        }
        shopData.save()
    }
}