package me.sat7.dynamicshop.guis

import me.sat7.dynamicshop.DynaShopAPI

class QuickSell : InGameUI() {
    init {
        uiType = UI_TYPE.QuickSell
    }

    fun getGui(player: Player?): Inventory {
        inventory = Bukkit.createInventory(player, quickSellGui.get().getInt("UiSlotCount"), t(player, "QUICK_SELL_TITLE"))
        val confSec: ConfigurationSection = quickSellGui.get().getConfigurationSection("Buttons")
        for (s in confSec.getKeys(false)) {
            try {
                val i: Int = Integer.parseInt(s)
                if (i > inventory.getSize()) break
                var mat: Material = Material.getMaterial(confSec.getString(s))
                if (mat == null) mat = Material.GREEN_STAINED_GLASS_PANE
                CreateButton(i, mat, t(player, "QUICK_SELL.GUIDE_TITLE"), t(player, "QUICK_SELL.GUIDE_LORE"))
            } catch (ignore: Exception) {
            }
        }
        return inventory
    }

    @Override
    override fun OnClickLowerInventory(e: InventoryClickEvent) {
        val player: Player = e.getWhoClicked() as Player
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() === Material.AIR) {
            return
        }
        val targetShopInfo: Array<String> = ShopUtil.FindTheBestShopToSell(player, e.getCurrentItem())
        val topShopName = targetShopInfo[0]
        val tradeIdx: Int = Integer.parseInt(targetShopInfo[1])
        if (topShopName.length() > 0) {
            if (e.isLeftClick()) {
                // 찾은 상점에 판매
                Sell.quickSellItem(player, e.getCurrentItem(), topShopName, tradeIdx, e.isShiftClick(), e.getSlot())
            } else if (e.isRightClick()) {
                player.closeInventory()
                DynaShopAPI.openShopGui(player, topShopName, 1)
            }
        } else {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.QSELL_NA") + topShopName)
        }
    }

    companion object {
        var quickSellGui: CustomConfig? = null
        fun SetupQuickSellGUIFile() {
            quickSellGui.setup("QuickSell", null)
            quickSellGui.get().addDefault("UiSlotCount", 9)
            if (quickSellGui.get().getKeys(false).size() === 0) {
                for (i in 0..8) quickSellGui.get().set("Buttons.$i", "GREEN_STAINED_GLASS_PANE")
            }
            quickSellGui.get().options().copyDefaults(true)
            quickSellGui.save()
        }
    }
}