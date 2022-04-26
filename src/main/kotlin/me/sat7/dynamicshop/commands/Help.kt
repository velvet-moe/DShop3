package me.sat7.dynamicshop.commands

import org.bukkit.Material

object Help {
    // 명령어 도움말 표시
    fun showHelp(helpcode: String, player: Player, args: Array<String>) {
        if (!DynamicShop.ccUser.get().getBoolean(player.getUniqueId() + ".cmdHelp")) return
        val uuid: UUID = player.getUniqueId()
        if (DynamicShop.userTempData.get(uuid).equals(helpcode)) return
        DynamicShop.userTempData.put(uuid, helpcode)
        if (helpcode.equals("main")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "main"))
            player.sendMessage(" - shop: " + t(player, "HELP.SHOP"))
            player.sendMessage(" - qsell: " + t(player, "HELP.QSELL"))
            player.sendMessage(" - cmdHelp: " + t(player, "HELP.CMD"))
            if (player.hasPermission(P_ADMIN_CREATE_SHOP)) player.sendMessage("§e - createshop: " + t(player, "HELP.CREATE_SHOP"))
            if (player.hasPermission(P_ADMIN_DELETE_SHOP)) player.sendMessage("§e - deleteshop: " + t(player, "HELP.DELETE_SHOP"))
            if (player.hasPermission(P_ADMIN_RELOAD)) player.sendMessage("§e - reload: " + t(player, "HELP.RELOAD"))
            player.sendMessage("")
        } else if (helpcode.equals("shop")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "shop"))
            player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop [<shopname>]")
            if (player.hasPermission(P_ADMIN_SHOP_EDIT) || player.hasPermission(P_ADMIN_EDIT_ALL)) {
                player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> <enable | addhand | add | edit | editall | setToRecAll | sellbuy | permission | maxpage | flag | position | shophours | fluctuation | stockStabilizing | account | log>")
            }
            if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                player.sendMessage("§e - enable: " + t(player, "HELP.SHOP_ENABLE"))
                player.sendMessage("§e - addhand: " + t(player, "HELP.SHOP_ADD_HAND"))
                player.sendMessage("§e - add: " + t(player, "HELP.SHOP_ADD_ITEM"))
                player.sendMessage("§e - edit: " + t(player, "HELP.SHOP_EDIT"))
            }
            if (player.hasPermission(P_ADMIN_EDIT_ALL)) player.sendMessage("§e - editall: " + t(player, "HELP.EDIT_ALL"))
            if (player.hasPermission(P_ADMIN_SHOP_EDIT)) player.sendMessage("§e - setToRecAll: " + t(player, "HELP.SET_TO_REC_ALL"))
            player.sendMessage("")
        } else if (helpcode.equals("open_shop")) {
            CMDManager.openShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("enable")) {
            CMDManager.enable!!.SendHelpMessage(player)
        } else if (helpcode.equals("add_hand") && player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            CMDManager.addHand!!.SendHelpMessage(player)
            val tempItem: ItemStack = player.getInventory().getItemInMainHand()
            if (tempItem.getType() !== Material.AIR) {
                val idx: Int = ShopUtil.findItemFromShop(args[1], tempItem)
                if (idx != -1) {
                    player.sendMessage("")
                    ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST")
                }
            } else {
                player.sendMessage(" - " + t(player, "ERR.HAND_EMPTY2"))
            }
            player.sendMessage("")
        } else if (helpcode.startsWith("add") && player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            if (helpcode.length() > "add".length()) {
                try {
                    val tempItem = ItemStack(Material.getMaterial(args[3]))
                    val idx: Int = ShopUtil.findItemFromShop(args[1], tempItem)
                    if (idx != -1) {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_ALREADY_EXIST")
                        player.sendMessage("")
                    }
                } catch (ignored: Exception) {
                }
            } else {
                CMDManager.add!!.SendHelpMessage(player)
            }
        } else if (helpcode.contains("edit") && !helpcode.equals("edit_all") && player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            if (helpcode.length() > "edit".length()) {
                try {
                    val tempItem = ItemStack(Material.getMaterial(args[3].substring(args[3].indexOf("/") + 1)))
                    val idx: Int = ShopUtil.findItemFromShop(args[1], tempItem)
                    if (idx != -1) {
                        ItemsUtil.sendItemInfo(player, args[1], idx, "HELP.ITEM_INFO")
                        player.sendMessage(" - " + t(player, "HELP.REMOVE_ITEM"))
                        player.sendMessage("")
                    }
                } catch (ignored: Exception) {
                }
            } else {
                CMDManager.edit!!.SendHelpMessage(player)
            }
        } else if (helpcode.equals("edit_all")) {
            CMDManager.editAll!!.SendHelpMessage(player)
        } else if (helpcode.equals("set_to_rec_all")) {
            CMDManager.setToRecAll!!.SendHelpMessage(player)
        } else if (helpcode.equals("cmd_help")) {
            CMDManager.commandHelp!!.SendHelpMessage(player)
        } else if (helpcode.equals("create_shop")) {
            CMDManager.createShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("delete_shop")) {
            CMDManager.deleteShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("merge_shop")) {
            CMDManager.mergeShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("rename_shop")) {
            CMDManager.renameShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("permission")) {
            CMDManager.permission!!.SendHelpMessage(player)
        } else if (helpcode.equals("max_page")) {
            CMDManager.maxPage!!.SendHelpMessage(player)
        } else if (helpcode.equals("flag")) {
            CMDManager.flag!!.SendHelpMessage(player)
        } else if (helpcode.equals("position")) {
            CMDManager.position!!.SendHelpMessage(player)
        } else if (helpcode.equals("shophours")) {
            CMDManager.shopHours!!.SendHelpMessage(player)
        } else if (helpcode.equals("fluctuation")) {
            CMDManager.fluctuation!!.SendHelpMessage(player)
        } else if (helpcode.equals("stock_stabilizing")) {
            CMDManager.stockStabilizing!!.SendHelpMessage(player)
        } else if (helpcode.equals("account")) {
            CMDManager.account!!.SendHelpMessage(player)
        } else if (helpcode.equals("set_tax")) {
            CMDManager.setTax!!.SendHelpMessage(player)
        } else if (helpcode.equals("set_default_shop")) {
            CMDManager.setDefaultShop!!.SendHelpMessage(player)
        } else if (helpcode.equals("delete_old_user")) {
            CMDManager.deleteUser!!.SendHelpMessage(player)
        } else if (helpcode.equals("sellbuy")) {
            CMDManager.sellBuy!!.SendHelpMessage(player)
        } else if (helpcode.equals("log")) {
            CMDManager.log!!.SendHelpMessage(player)
        }
    }
}