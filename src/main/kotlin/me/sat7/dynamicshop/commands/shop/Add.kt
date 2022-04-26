package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class Add : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(7)
        validArgCount.add(9)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "add"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <median> <stock>")
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> add <item> <value> <min value> <max value> <median> <stock>")
        player.sendMessage(" - " + t(player, "HELP.SHOP_ADD_ITEM"))
        player.sendMessage(" - " + t(player, "HELP.PRICE"))
        player.sendMessage(" - " + t(player, "HELP.INF_STATIC"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val mat: Material
        val buyValue: Double
        var valueMin = 0.01
        var valueMax = -1.0
        val median: Int
        val stock: Int
        try {
            if (args.size == 7) {
                mat = Material.getMaterial(args[3].toUpperCase())
                buyValue = Double.parseDouble(args[4])
                median = Integer.parseInt(args[5])
                stock = Integer.parseInt(args[6])
            } else {
                mat = Material.getMaterial(args[3].toUpperCase())
                buyValue = Double.parseDouble(args[4])
                valueMin = Double.parseDouble(args[5])
                valueMax = Double.parseDouble(args[6])
                median = Integer.parseInt(args[7])
                stock = Integer.parseInt(args[8])

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
            if (buyValue < 0.01 || median == 0 || stock == 0) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.VALUE_ZERO"))
                return
            }
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            return
        }

        // 금지품목
        if (Material.getMaterial(args[3]) === Material.AIR) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.ITEM_FORBIDDEN"))
            return
        }

        // 상점에서 같은 아이탬 찾기
        val itemStack: ItemStack
        try {
            itemStack = ItemStack(mat)
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_ITEM_NAME"))
            return
        }
        var idx: Int = ShopUtil.findItemFromShop(shopName, itemStack)
        // 상점에 새 아이탬 추가
        if (idx == -1) {
            idx = ShopUtil.findEmptyShopSlot(shopName, 1, true)
            if (idx == -1) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.NO_EMPTY_SLOT"))
            } else if (ShopUtil.addItemToShop(shopName, idx, itemStack, buyValue, buyValue, valueMin, valueMax, median, stock)) // 아이탬 추가
            {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_ADDED"))
                ItemsUtil.sendItemInfo(sender, shopName, idx, "HELP.ITEM_INFO")
            }
        } else {
            ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock)
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"))
            ItemsUtil.sendItemInfo(sender, shopName, idx, "HELP.ITEM_INFO")
        }
    }
}