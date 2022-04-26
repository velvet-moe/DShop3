package me.sat7.dynamicshop.guis

import me.sat7.dynamicshop.DynamicShop

class InGameUI {
    enum class UI_TYPE {
        ItemPalette, ItemSettings, ItemTrade, QuickSell, Shop, ShopSettings, StartPage, StartPageSettings, StartPage_ShopList, StartPage_ColorList
    }

    var uiType: UI_TYPE? = null
    fun OnClickUpperInventory(e: InventoryClickEvent?) {}
    fun OnClickLowerInventory(e: InventoryClickEvent?) {}
    fun RefreshUI() {}
    protected var inventory: Inventory? = null
    @SuppressWarnings(["UnusedReturnValue", "SameParameterValue"])
    protected fun CreateButton(slotIndex: Int, icon: Material?, name: String?, lore: String?): ItemStack {
        var lore = lore
        if (lore != null && lore.isEmpty()) lore = null
        return if (lore == null) {
            CreateButton(slotIndex, icon, name, 1)
        } else if (lore.contains("\n")) {
            CreateButton(slotIndex, icon, name, ArrayList(Arrays.asList(lore.split("\n"))), 1)
        } else {
            CreateButton(slotIndex, icon, name, ArrayList(Collections.singletonList(lore)), 1)
        }
    }

    @SuppressWarnings(["UnusedReturnValue", "SameParameterValue"])
    protected fun CreateButton(slotIndex: Int, icon: Material?, name: String?, lore: ArrayList<String?>?): ItemStack {
        return CreateButton(slotIndex, icon, name, lore, 1)
    }

    @SuppressWarnings(["UnusedReturnValue", "SameParameterValue"])
    protected fun CreateButton(slotIndex: Int, icon: Material?, name: String?, lore: String?, amount: Int): ItemStack {
        var lore = lore
        if (lore != null && lore.isEmpty()) lore = null
        return if (lore == null) {
            CreateButton(slotIndex, icon, name, amount)
        } else if (lore.contains("\n")) {
            CreateButton(slotIndex, icon, name, ArrayList(Arrays.asList(lore.split("\n"))), amount)
        } else {
            CreateButton(slotIndex, icon, name, ArrayList(Collections.singletonList(lore)), amount)
        }
    }

    protected fun CreateButton(slotIndex: Int, icon: Material?, name: String?, amount: Int): ItemStack {
        val itemStack: ItemStack = ItemsUtil.createItemStack(icon, null, name, null, amount)
        inventory.setItem(slotIndex, itemStack)
        return itemStack
    }

    @SuppressWarnings(["UnusedReturnValue", "SameParameterValue"])
    protected fun CreateButton(slotIndex: Int, icon: Material?, name: String?, lore: ArrayList<String?>?, amount: Int): ItemStack {
        val itemStack: ItemStack = ItemsUtil.createItemStack(icon, null, name, lore, amount)
        inventory.setItem(slotIndex, itemStack)
        return itemStack
    }

    @SuppressWarnings("SameParameterValue")
    protected fun CreateCloseButton(player: Player?, slotIndex: Int) {
        CreateButton(slotIndex, GetCloseButtonIconMat(), t(player, "CLOSE"), t(player, "CLOSE_LORE"))
    }

    companion object {
        fun GetCloseButtonIconMat(): Material? {
            val iconName: String = DynamicShop.plugin.getConfig().getString("UI.CloseButtonIcon")
            var mat: Material = Material.getMaterial(iconName)
            if (mat == null) {
                mat = Material.BARRIER
                DynamicShop.plugin.getConfig().set("UI.CloseButtonIcon", "BARRIER")
                DynamicShop.plugin.saveConfig()
            }
            return mat
        }

        fun GetPageButtonIconMat(): Material? {
            val iconName: String = DynamicShop.plugin.getConfig().getString("UI.PageButtonIcon")
            var mat: Material = Material.getMaterial(iconName)
            if (mat == null) {
                mat = Material.PAPER
                DynamicShop.plugin.getConfig().set("UI.PageButtonIcon", "PAPER")
                DynamicShop.plugin.saveConfig()
            }
            return mat
        }

        fun GetShopInfoButtonIconMat(): Material? {
            val iconName: String = DynamicShop.plugin.getConfig().getString("UI.ShopInfoButtonIcon")
            var mat: Material = Material.getMaterial(iconName)
            if (mat == null) {
                mat = Material.GOLD_BLOCK
                DynamicShop.plugin.getConfig().set("UI.ShopInfoButtonIcon", "GOLD_BLOCK")
                DynamicShop.plugin.saveConfig()
            }
            return mat
        }
    }
}