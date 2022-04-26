package me.sat7.dynamicshop.guis

import java.util.UUID

class StartPageSettings : InGameUI() {
    private val CLOSE = 0
    private val NAME = 2
    private val LORE = 3
    private val ICON = 4
    private val CMD = 5
    private val SHOP_SHORTCUT = 6
    private val DECO = 7
    private val DELETE = 8
    private var slotIndex = 0

    init {
        uiType = UI_TYPE.StartPageSettings
    }

    fun getGui(player: Player, slotIndex: Int): Inventory {
        this.slotIndex = slotIndex
        DynamicShop.userInteractItem.put(player.getUniqueId(), "startPage/$slotIndex")
        inventory = Bukkit.createInventory(player, 9, t(player, "START_PAGE.EDITOR_TITLE"))
        CreateCloseButton(player, CLOSE) // 닫기 버튼
        CreateButton(NAME, Material.BOOK, t(player, "START_PAGE.EDIT_NAME"), "") // 이름 버튼
        CreateButton(LORE, Material.BOOK, t(player, "START_PAGE.EDIT_LORE"), "") // 설명 버튼

        // 아이콘 버튼
        CreateButton(ICON, Material.getMaterial(StartPage.ccStartPage.get().getString("Buttons.$slotIndex.icon")), t(player, "START_PAGE.EDIT_ICON"), "")
        val cmdString: String = StartPage.ccStartPage.get().getString("Buttons.$slotIndex.action")
        CreateButton(CMD, Material.REDSTONE_TORCH, t(player, "START_PAGE.EDIT_ACTION"), if (cmdString == null || cmdString.isEmpty()) null else "§7/$cmdString") // 액션 버튼
        CreateButton(SHOP_SHORTCUT, Material.EMERALD, t(player, "START_PAGE.SHOP_SHORTCUT"), "") // 상점 바로가기 생성 버튼
        CreateButton(DECO, Material.BLUE_STAINED_GLASS_PANE, t(player, "START_PAGE.CREATE_DECO"), "") // 장식 버튼
        CreateButton(DELETE, Material.BONE, t(player, "START_PAGE.REMOVE"), t(player, "START_PAGE.REMOVE_LORE")) // 삭제 버튼
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        val player: Player = e.getWhoClicked() as Player
        val uuid: UUID = player.getUniqueId()

        // 돌아가기
        if (e.getSlot() === CLOSE) {
            DynaShopAPI.openStartPage(player)
        } else if (e.getSlot() === DELETE) {
            StartPage.ccStartPage.get().set("Buttons.$slotIndex", null)
            StartPage.ccStartPage.save()
            DynaShopAPI.openStartPage(player)
        } else if (e.getSlot() === NAME) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "START_PAGE.ENTER_NAME"))
            ShopUtil.closeInventoryWithDelay(player)
            DynamicShop.userTempData.put(uuid, "waitforInput" + "btnName")
            OnChat.WaitForInput(player)
        } else if (e.getSlot() === LORE) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "START_PAGE.ENTER_LORE"))
            ShopUtil.closeInventoryWithDelay(player)
            DynamicShop.userTempData.put(uuid, "waitforInput" + "btnLore")
            OnChat.WaitForInput(player)
        } else if (e.getSlot() === ICON) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "START_PAGE.ENTER_ICON"))
            ShopUtil.closeInventoryWithDelay(player)
            DynamicShop.userTempData.put(uuid, "waitforInput" + "btnIcon")
            OnChat.WaitForInput(player)
        } else if (e.getSlot() === CMD) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "START_PAGE.ENTER_ACTION"))
            ShopUtil.closeInventoryWithDelay(player)
            DynamicShop.userTempData.put(uuid, "waitforInput" + "btnAction")
            OnChat.WaitForInput(player)
        } else if (e.getSlot() === SHOP_SHORTCUT) {
            DynaShopAPI.openShopListUI(player, 1, slotIndex)
        } else if (e.getSlot() === DECO) {
            DynaShopAPI.openColorPicker(player, slotIndex)
        }
    }
}