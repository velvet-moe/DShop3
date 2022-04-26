package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class SellBuy : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(4)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "sellbuy"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shop name> sellbuy < sellonly | buyonly | clear >")
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val temp: String
        temp = if (args[3].equalsIgnoreCase("SellOnly")) {
            "SellOnly"
        } else if (args[3].equalsIgnoreCase("BuyOnly")) {
            "BuyOnly"
        } else {
            "SellBuy"
        }
        for (s in shopData.get().getKeys(false)) {
            try {
                // i를 직접 사용하지는 않지만 의도적으로 넣은 코드임.
                val i: Int = Integer.parseInt(s)
                if (!shopData.get().contains("$s.value")) continue  //장식용임
            } catch (e: Exception) {
                continue
            }
            if (temp.equalsIgnoreCase("SellBuy")) {
                shopData.get().set("$s.tradeType", null)
            } else {
                shopData.get().set("$s.tradeType", temp)
            }
        }
        shopData.save()
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.CHANGES_APPLIED") + temp)
    }
}