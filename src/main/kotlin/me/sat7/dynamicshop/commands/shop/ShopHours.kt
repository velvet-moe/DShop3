package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class ShopHours : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(5)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "shophours"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> shophours <open> <close>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        try {
            val start: Int = Clamp(Integer.parseInt(args[3]), 1, 24)
            val end: Int = Clamp(Integer.parseInt(args[4]), 1, 24)
            if (start == end) {
                shopData.get().set("Options.shophours", null)
                shopData.save()
                sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED")).toString() + "Open 24 hours")
            } else {
                shopData.get().set("Options.shophours", "$start~$end")
                shopData.save()
                sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + start).toString() + "~" + end)
            }
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
        }
    }
}