package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class AddHand : DSCMD() {
    init {
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(6)
        validArgCount.add(8)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "addhand"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> addhand <value> <median> <stock>")
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> addhand <value> <min value> <max value> <median> <stock>")
        player.sendMessage(" - " + t(player, "HELP.SHOP_ADD_HAND"))
        player.sendMessage(" - " + t(player, "HELP.PRICE"))
        player.sendMessage(" - " + t(player, "HELP.INF_STATIC"))
    }

    @Override
    fun RunCMD(args: Array<String?>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val player: Player = sender as Player
        val shopName: String = Shop.GetShopName(args)
        val buyValue: Double
        var valueMin = 0.01
        var valueMax = -1.0
        val median: Int
        val stock: Int
        try {
            if (args.size == 6) {
                buyValue = Double.parseDouble(args[3])
                median = Integer.parseInt(args[4])
                stock = Integer.parseInt(args[5])
            } else {
                buyValue = Double.parseDouble(args[3])
                valueMin = Double.parseDouble(args[4])
                valueMax = Double.parseDouble(args[5])
                median = Integer.parseInt(args[6])
                stock = Integer.parseInt(args[7])

                // 유효성 검사
                if (valueMax > 0 && valueMin > 0 && valueMin >= valueMax) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.MAX_LOWER_THAN_MIN"))
                    return
                }
                if (valueMax > 0 && buyValue > valueMax) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
                    return
                }
                if (valueMin > 0 && buyValue < valueMin) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
                    return
                }
            }
            if (buyValue < 0.01 || median == 0 || stock == 0) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.VALUE_ZERO"))
                return
            }
        } catch (e: Exception) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.WRONG_DATATYPE"))
            return
        }

        // 손에 뭔가 들고있는지 확인
        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() === Material.AIR) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.HAND_EMPTY"))
            return
        }

        // 금지품목
        if (Material.getMaterial(player.getInventory().getItemInMainHand().getType().toString()) === Material.AIR) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.ITEM_FORBIDDEN"))
            return
        }

        // 상점에서 같은 아이탬 찾기
        var idx: Int = ShopUtil.findItemFromShop(shopName, player.getInventory().getItemInMainHand())
        // 상점에 새 아이탬 추가
        if (idx == -1) {
            idx = ShopUtil.findEmptyShopSlot(shopName, 1, true)
            if (idx == -1) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_EMPTY_SLOT"))
            } else if (ShopUtil.addItemToShop(shopName, idx, player.getInventory().getItemInMainHand(), buyValue, buyValue, valueMin, valueMax, median, stock)) // 아이탬 추가
            {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_ADDED"))
                ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO")
            }
        } else {
            ShopUtil.editShopItem(shopName, idx, buyValue, buyValue, valueMin, valueMax, median, stock)
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_UPDATED"))
            ItemsUtil.sendItemInfo(player, shopName, idx, "HELP.ITEM_INFO")
        }
    }
}