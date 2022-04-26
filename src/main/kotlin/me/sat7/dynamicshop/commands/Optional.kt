package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.DynamicShop

class Optional : CommandExecutor {
    @Override
    fun onCommand(sender: CommandSender, command: Command?, label: String?, args: Array<String>): Boolean {
        if (!DynamicShop.plugin.getConfig().getBoolean("Command.UseShopCommand")) return true
        if (sender is Player) {
            val player: Player = sender as Player
            if (!player.hasPermission(Constants.P_USE)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"))
                return true
            }
            if (player.getGameMode() === GameMode.CREATIVE && !player.hasPermission(Constants.P_ADMIN_CREATIVE)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.CREATIVE"))
                return true
            }
            if (args.size == 0) {
                Bukkit.dispatchCommand(sender, "DynamicShop shop")
            } else {
                Bukkit.dispatchCommand(sender, "DynamicShop shop " + args[0])
            }
        }
        return true
    }
}