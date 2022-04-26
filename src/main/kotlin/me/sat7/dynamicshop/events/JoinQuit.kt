package me.sat7.dynamicshop.events

import me.sat7.dynamicshop.DynamicShop

class JoinQuit : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player: Player = e.getPlayer()
        DynamicShop.userTempData.put(player.getUniqueId(), "")
        DynamicShop.userInteractItem.put(player.getUniqueId(), "")
        DynamicShop.ccUser.get().set(player.getUniqueId() + ".lastJoin", System.currentTimeMillis())
        DynamicShop.ccUser.get().addDefault(player.getUniqueId() + ".cmdHelp", true)
        DynamicShop.ccUser.save()
        val isSnapshot: Boolean = DynamicShop.yourVersion.contains("snapshot")
        if (DynamicShop.updateAvailable || isSnapshot) {
            if (e.getPlayer().hasPermission(P_ADMIN_SHOP_EDIT) || e.getPlayer().hasPermission(P_ADMIN_RELOAD)) {
                val text = TextComponent("")
                text.addExtra(DynamicShop.CreateLink("DShop3", false, ChatColor.DARK_AQUA, UpdateChecker.getResourceUrl()))
                text.addExtra(" ")
                text.addExtra(DynamicShop.CreateLink("Download", false, ChatColor.WHITE, UpdateChecker.getResourceUrl()))
                text.addExtra(" ")
                text.addExtra(DynamicShop.CreateLink("Premium", false, ChatColor.WHITE, "https://spigotmc.org/resources/100058"))
                text.addExtra(" ")
                text.addExtra(DynamicShop.CreateLink("Donate", false, ChatColor.WHITE, "https://www.paypal.com/paypalme/7sat"))
                e.getPlayer().sendMessage("")
                e.getPlayer().spigot().sendMessage(text)
                if (isSnapshot) {
                    e.getPlayer().sendMessage("§cYou are currently using a snapshot build.")
                } else {
                    e.getPlayer().sendMessage("New Update available")
                }
                e.getPlayer().sendMessage("§7Latest version: §f" + DynamicShop.lastVersion)
                e.getPlayer().sendMessage("§7Your version: §f" + DynamicShop.yourVersion)
                e.getPlayer().sendMessage("")
            }
        }
    }

    @EventHandler
    fun OnPlayerQuit(e: PlayerQuitEvent) {
        UIManager.OnPlayerQuit(e.getPlayer())
        DynamicShop.userTempData.remove(e.getPlayer().getUniqueId())
        DynamicShop.userInteractItem.remove(e.getPlayer().getUniqueId())
    }
}