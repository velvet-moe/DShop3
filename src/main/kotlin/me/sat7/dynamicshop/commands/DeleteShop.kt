package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.files.CustomConfig

class DeleteShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_DELETE_SHOP
        validArgCount.add(2)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "§c§ldeleteshop§f§r"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds deleteshop <shopname>")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        if (ShopUtil.shopConfigFiles.containsKey(args[1])) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(args[1])
            data.delete()
            ShopUtil.shopConfigFiles.remove(args[1])
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.SHOP_DELETED"))
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_NOT_FOUND"))
        }
    }
}