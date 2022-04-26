package me.sat7.dynamicshop.events

import me.sat7.dynamicshop.DynamicShop

class OnChat : Listener {
    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val p: Player = e.getPlayer()
        val uuid: UUID = p.getUniqueId()
        if (!DynamicShop.userTempData.containsKey(uuid)) return
        val userData: String = DynamicShop.userTempData.get(uuid)
        if (userData.equals("waitforPalette")) {
            e.setCancelled(true)
            val userInteractData: Array<String> = DynamicShop.userInteractItem.get(p.getUniqueId()).split("/")
            DynamicShop.userTempData.put(uuid, "")
            DynaShopAPI.openItemPalette(p, userInteractData[0], Integer.parseInt(userInteractData[1]), 1, e.getMessage())
            cancelRunnable(p)
        } else if (userData.contains("waitforInput")) {
            e.setCancelled(true)
            val s: String = userData.replace("waitforInput", "")
            val temp: Array<String> = DynamicShop.userInteractItem.get(uuid).split("/")
            when (s) {
                "btnName" -> StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".displayName", "§3" + e.getMessage())
                "btnLore" -> StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".lore", "§f" + e.getMessage())
                "btnIcon" -> try {
                    val tempMat: Material = Material.getMaterial(ChatColor.stripColor(e.getMessage()).toUpperCase())
                    StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".icon", tempMat.name())
                } catch (exception: Exception) {
                    p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.WRONG_ITEM_NAME"))
                }
                "btnAction" -> StartPage.ccStartPage.get().set("Buttons." + temp[1] + ".action", ChatColor.stripColor(e.getMessage()))
            }
            StartPage.ccStartPage.save()
            DynamicShop.userTempData.put(uuid, "")
            DynaShopAPI.openStartPage(p)
            cancelRunnable(p)
        } else if (userData.contains("waitforPageDelete")) {
            e.setCancelled(true)
            if (e.getMessage().equals("delete")) {
                val temp: Array<String> = DynamicShop.userInteractItem.get(uuid).split("/")
                ShopUtil.deleteShopPage(temp[0], Integer.parseInt(temp[1]))
                DynaShopAPI.openShopGui(p, temp[0], 1)
            } else {
                p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "MESSAGE.INPUT_CANCELED"))
            }
            DynamicShop.userTempData.put(uuid, "")
            cancelRunnable(p)
        }
    }

    companion object {
        private val runnableMap: Map<UUID, Integer> = HashMap()
        fun WaitForInput(player: Player) {
            if (runnableMap.containsKey(player.getUniqueId())) {
                cancelRunnable(player)
            }
            val taskID: BukkitTask = Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, {
                val uuid: UUID = player.getUniqueId()
                val userData: String = DynamicShop.userTempData.get(uuid)
                if (userData.equals("waitforPalette")) {
                    DynamicShop.userTempData.put(uuid, "")
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SEARCH_CANCELED"))
                } else if (userData.contains("waitforInput")) {
                    DynamicShop.userTempData.put(uuid, "")
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INPUT_CANCELED"))
                } else if (userData.equals("waitforPageDelete")) {
                    DynamicShop.userTempData.put(uuid, "")
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INPUT_CANCELED"))
                }
            }, 400)
            runnableMap.put(player.getUniqueId(), taskID.getTaskId())
        }

        private fun cancelRunnable(player: Player) {
            if (runnableMap.containsKey(player.getUniqueId())) {
                Bukkit.getScheduler().cancelTask(runnableMap[player.getUniqueId()])
            }
        }
    }
}