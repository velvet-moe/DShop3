package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Edit : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(5)
        validArgCount.add(7)
        validArgCount.add(9)
        validArgCount.add(10)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "edit"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <median> <stock>")
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> edit <item> <value> <min value> <max value> <median> <stock> [<max stock>]")
        player.sendMessage(" - " + t(player, "HELP.SHOP_EDIT"))
        player.sendMessage(" - " + t(player, "HELP.PRICE"))
        player.sendMessage(" - " + t(player, "HELP.INF_STATIC"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val idx: Int
        val buyValue: Double
        var valueMin = 0.01
        var valueMax = -1.0
        val median: Int
        val stock: Int
        var maxStock = -1
        try {
            val temp: Array<String> = args[3].split("/")
            idx = Integer.parseInt(temp[0])
            if (!shopData.get().contains(temp[0])) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_ITEM_NAME"))
                return
            }
            buyValue = Double.parseDouble(args[4])

            // 삭제
            if (buyValue <= 0) {
                ShopUtil.removeItemFromShop(shopName, idx)
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_DELETED"))
                return
            } else {
                if (args.size == 7) {
                    median = Integer.parseInt(args[5])
                    stock = Integer.parseInt(args[6])
                } else {
                    valueMin = Integer.parseInt(args[5])
                    valueMax = Integer.parseInt(args[6])
                    median = Integer.parseInt(args[7])
                    stock = Integer.parseInt(args[8])
                    if (args.size == 10) maxStock = Integer.parseInt(args[9])
                    if (maxStock < 1) maxStock = -1

                    // 유효성 검사
                    if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax) {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.MAX_LOWER_THAN_MIN"))
                        return
                    }
                    if (valueMax > 0 && buyValue > valueMax) {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
                        return
                    }
                    if (valueMin > 0 && buyValue < valueMin) {
                        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
                        return
                    }
                }
            }
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            return
        }

        // 수정
        ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock, maxStock)
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"))
        ItemsUtil.sendItemInfo(sender, shopName, idx, "HELP.ITEM_INFO")
    }
}