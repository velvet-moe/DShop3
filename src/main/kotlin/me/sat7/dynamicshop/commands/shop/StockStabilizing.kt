package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class StockStabilizing : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
        validArgCount.add(5)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "stockStabilizing"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> stockStabilizing <interval> <strength>")
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> stockStabilizing off")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (args.size == 4) {
            if (args[3].equals("off")) {
                shopData.get().set("Options.stockStabilizing", null)
                shopData.save()
                sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED")).toString() + "stockStabilizing Off")
            } else {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
            }
        } else if (args.size >= 5) {
            val interval: Int
            try {
                interval = Clamp(Integer.parseInt(args[3]), 1, 999)
            } catch (e: Exception) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
                return
            }
            try {
                val strength: Double = Double.parseDouble(args[4])
                shopData.get().set("Options.stockStabilizing.interval", interval)
                shopData.get().set("Options.stockStabilizing.strength", strength)
                shopData.save()
                sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED")).toString() + "Interval " + interval + ", strength " + strength)
            } catch (e: Exception) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            }
        }
    }
}