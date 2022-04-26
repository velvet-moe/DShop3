package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Permission : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(3)
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "permission"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> permission [<true | false | custom >]")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (args.size == 3) {
            var s: String? = shopData.get().getConfigurationSection("Options").getString("permission")
            if (s == null || s.length() === 0) s = t(sender, "NULL(OPEN)")
            sender.sendMessage(DynamicShop.dsPrefix(sender) + s)
        } else if (args.size >= 4) {
            if (args[3].equalsIgnoreCase("true")) {
                shopData.get().set("Options.permission", "dshop.user.shop." + args[1])
                sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED")).toString() + "dshop.user.shop." + args[1])
            } else if (args[3].equalsIgnoreCase("false")) {
                shopData.get().set("Options.permission", "")
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + t(sender, "NULL(OPEN)"))
            } else {
                shopData.get().set("Options.permission", args[3])
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + args[3])
            }
            shopData.save()
        }
    }
}