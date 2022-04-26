package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class SetToRecAll : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(2)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "SetToRecAll"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> SetToRecAll")
        player.sendMessage(" - " + t(player, "HELP.SET_TO_REC_ALL"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        ShopUtil.SetToRecommendedValueAll(args[1], sender)
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"))
    }
}