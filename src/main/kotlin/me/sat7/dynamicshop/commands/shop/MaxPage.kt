package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class MaxPage : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "maxpage"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> maxpage <number>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val newValue: Int
        newValue = try {
            Integer.parseInt(args[3])
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            return
        }
        if (newValue <= 0) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.VALUE_ZERO"))
        } else {
            shopData.get().set("Options.page", newValue)
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[3])
            shopData.save()
        }
    }
}