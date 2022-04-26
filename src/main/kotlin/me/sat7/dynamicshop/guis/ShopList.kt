package me.sat7.dynamicshop.guis

import me.sat7.dynamicshop.DynaShopAPI

class ShopList : InGameUI() {
    private val CLOSE = 45
    private val PAGE = 49
    private var page = 0
    private var maxPage = 0
    private var slotIndex = 0

    init {
        uiType = InGameUI.UI_TYPE.StartPage_ShopList
    }

    fun getGui(player: Player?, page: Int, slotIndex: Int): Inventory {
        inventory = Bukkit.createInventory(player, 54, t(player, "START_PAGE.SHOP_LIST_TITLE"))
        maxPage = ShopUtil.shopConfigFiles.size() / 45 + 1
        this.page = MathUtil.Clamp(page, 1, maxPage)
        this.slotIndex = slotIndex
        CreateShopButtons()
        CreateCloseButton(player, CLOSE)
        CreateButton(PAGE, GetPageButtonIconMat(),
                t(player, "START_PAGE.SHOP_LIST.PAGE_TITLE").replace("{curPage}", String.valueOf(this.page)).replace("{maxPage}", String.valueOf(maxPage)),
                t(player, "START_PAGE.SHOP_LIST.PAGE_LORE"))
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        val player: Player = e.getWhoClicked() as Player
        if (e.getSlot() === CLOSE) {
            DynaShopAPI.openStartPageSettingGui(player, slotIndex)
        } else if (e.getSlot() === PAGE) {
            if (e.isLeftClick()) {
                page--
                if (page < 1) page = maxPage
            } else if (e.isRightClick()) {
                page++
                if (page > maxPage) page = 1
            }
            DynaShopAPI.openShopListUI(player, page, slotIndex)
        } else if (e.getCurrentItem() != null && e.getCurrentItem().getType() === Material.CHEST) {
            val shopName: String = e.getCurrentItem().getItemMeta().getDisplayName()
            StartPage.ccStartPage.get().set("Buttons.$slotIndex.displayName", "§3$shopName")
            StartPage.ccStartPage.get().set("Buttons.$slotIndex.lore", t(player, "START_PAGE.DEFAULT_SHOP_LORE"))
            StartPage.ccStartPage.get().set("Buttons.$slotIndex.action", "ds shop $shopName")
            StartPage.ccStartPage.save()
            DynaShopAPI.openStartPage(player)
        }
    }

    private fun CreateShopButtons() {
        var idx = 0
        var slotIdx = 0
        for (shopName in ShopUtil.shopConfigFiles.keySet()) {
            if (idx > page * 45) break
            if (idx >= (page - 1) * 45) {
                CreateButton(slotIdx, Material.CHEST, shopName, "")
                slotIdx++
            }
            idx++
        }
    }
}