package me.sat7.dynamicshop.guis

import me.sat7.dynamicshop.DynamicShop

class UIManager : Listener {
    @SuppressWarnings("EmptyMethod")
    @EventHandler
    fun OnOpen(e: InventoryOpenEvent?) {
    }

    @EventHandler
    fun OnClose(e: InventoryCloseEvent) {
        // 기존에 인벤토리가 열려있는 상태에서 다른것을 열면 close가 먼저 불림.
        val player: Player = e.getPlayer() as Player
        currentUI.remove(player)
    }

    companion object {
        private val currentUI: HashMap<Player, InGameUI> = HashMap()
        fun OnPlayerQuit(p: Player?) {
            currentUI.remove(p)
        }

        fun Open(player: Player, inventory: Inventory?, inGameUI: InGameUI?) {
            object : BukkitRunnable() {
                @Override
                fun run() {
                    player.openInventory(inventory) // 가장 먼저 불려야함. (버킷에서 새 인벤이 열릴때 기존의 것이 닫힘처리됨)
                    currentUI.put(player, inGameUI)
                }
            }.runTask(DynamicShop.plugin)
        }

        fun IsPlayerUsingPluginGUI(player: Player?): Boolean {
            return if (player == null) false else currentUI.get(player) != null
        }

        fun GetPlayerCurrentUIType(player: Player?): InGameUI.UI_TYPE? {
            if (player == null) return null
            return if (currentUI.get(player) == null) null else currentUI.get(player).uiType
        }

        fun OnClickUpperInventory(e: InventoryClickEvent) {
            val player: Player = e.getWhoClicked() as Player
            val inGameUI: InGameUI = currentUI.get(player) ?: return
            SoundUtil.playerSoundEffect(player, "click")
            inGameUI.OnClickUpperInventory(e)
        }

        fun OnClickLowerInventory(e: InventoryClickEvent) {
            val player: Player = e.getWhoClicked() as Player
            val inGameUI: InGameUI = currentUI.get(player) ?: return
            SoundUtil.playerSoundEffect(player, "click")
            inGameUI.OnClickLowerInventory(e)
        }

        fun RefreshUI() {
            for (entry in currentUI.entrySet()) {
                val p: Player = entry.getKey()
                val ui: InGameUI = entry.getValue()
                if (p == null || ui == null) continue
                if (ui.uiType === InGameUI.UI_TYPE.ItemTrade
                        || ui.uiType === InGameUI.UI_TYPE.Shop) {
                    ui.RefreshUI()
                }
            }
        }
    }
}