package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.DynaShopAPI

class Root : CommandExecutor {
    @Override
    fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String>): Boolean {
        val senderIsPlayer = sender is Player
        if (senderIsPlayer) {
            val player: Player = sender as Player
            if (!player.hasPermission(Constants.P_USE)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"))
                return true
            }
            if (player.getGameMode() === GameMode.CREATIVE && !player.hasPermission(Constants.P_ADMIN_CREATIVE)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.CREATIVE"))
                return true
            }

            // user.yml 에 player가 없으면 재생성 시도. 실패시 리턴.
            if (!DynaShopAPI.recreateUserData(player)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_USER_ID"))
                return true
            }

            // 스타트페이지
            if (args.size == 0) {
                DynaShopAPI.openStartPage(player)
                return true
            } else if (args[0].equalsIgnoreCase("close")) {
                player.closeInventory()
                return true
            } else if (args[0].equalsIgnoreCase("shop")) {
                Shop.shopCommand(args, player)
                return true
            } else if (args[0].equalsIgnoreCase("qsell")) {
                if (sender.hasPermission(Constants.P_USE_QSELL)) {
                    DynaShopAPI.openQuickSellGUI(player)
                    return true
                }
            }
        } else {
            if (args.size > 0) {
                if (args[0].equalsIgnoreCase("shop")) {
                    Shop.shopCommand(args, sender)
                    return true
                } else if (args[0].equalsIgnoreCase("qsell")) {
                    sender.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console")
                    return true
                }
            }
        }
        if (args.size > 0) CMDManager.RunCMD(args[0].toLowerCase(), args, sender) else sender.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console")
        return true
    }
}