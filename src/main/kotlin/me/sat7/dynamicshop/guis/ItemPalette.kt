package me.sat7.dynamicshop.guis

import java.util.ArrayList

class ItemPalette : InGameUI() {
    private val CLOSE = 45
    private val PAGE = 49
    private val ADD_ALL = 51
    private val SEARCH = 53
    private var paletteList: ArrayList<ItemStack> = ArrayList()
    private var player: Player? = null
    private var shopName = ""
    private var shopSlotIndex = 0
    private var search = ""
    private var maxPage = 0
    private var currentPage = 0

    init {
        uiType = UI_TYPE.ItemPalette
    }

    fun getGui(player: Player?, shopName: String, targetSlot: Int, page: Int, search: String): Inventory {
        this.player = player
        this.shopName = shopName
        shopSlotIndex = targetSlot
        this.search = search
        inventory = Bukkit.createInventory(player, 54, t(player, "PALETTE_TITLE") + "§7 | §8" + shopName)
        paletteList.clear()
        paletteList = CreatePaletteList()
        maxPage = paletteList.size() / 45 + 1
        currentPage = Clamp(page, 1, maxPage)

        // Items
        ShowItems()

        // Close Button
        CreateCloseButton(player, CLOSE)

        // Page Button
        val pageString: String = t(player, "PALETTE.PAGE_TITLE")
                .replace("{curPage}", Integer.toString(page))
                .replace("{maxPage}", Integer.toString(maxPage))
        CreateButton(PAGE, InGameUI.GetPageButtonIconMat(), pageString, t(player, "PALETTE.PAGE_LORE"), page)

        // Add all Button
        if (!paletteList.isEmpty()) CreateButton(ADD_ALL, Material.YELLOW_STAINED_GLASS_PANE, t(player, "PALETTE.ADD_ALL"), "")

        // Search Button
        var filterString = if (search.isEmpty()) "" else t(player, "PALETTE.FILTER_APPLIED") + search
        filterString += """
               
               ${t(player, "PALETTE.FILTER_LORE")}
               """.trimIndent()
        CreateButton(SEARCH, Material.COMPASS, t(player, "PALETTE.SEARCH"), filterString)
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        player = e.getWhoClicked() as Player
        if (e.getSlot() === CLOSE) CloseUI() else if (e.getSlot() === PAGE) MovePage(e.isLeftClick(), e.isRightClick()) else if (e.getSlot() === ADD_ALL) AddAll() else if (e.getSlot() === SEARCH) OnClickSearch(e.isLeftClick(), e.isRightClick()) else if (e.getSlot() <= 45) OnClickItem(e.isLeftClick(), e.isRightClick(), e.isShiftClick(), e.getCurrentItem())
    }

    @Override
    override fun OnClickLowerInventory(e: InventoryClickEvent) {
        player = e.getWhoClicked() as Player
        OnClickUserItem(e.isLeftClick(), e.isRightClick(), e.getCurrentItem())
    }

    private fun CreatePaletteList(): ArrayList<ItemStack> {
        var paletteList: ArrayList<ItemStack> = ArrayList()
        if (search.length() > 0) {
            val allMat: Array<Material> = Material.values()
            for (m in allMat) {
                val target: String = m.name().toUpperCase()
                val temp: Array<String> = search.split(" ")
                if (temp.size == 1) {
                    if (target.contains(search.toUpperCase())) {
                        paletteList.add(ItemStack(m))
                    } else if (target.contains(search.toUpperCase().replace(" ", "_"))) {
                        paletteList.add(ItemStack(m))
                    }
                } else {
                    val targetTemp: Array<String> = target.split("_")
                    if (targetTemp.size > 1 && temp.size > 1 && targetTemp.size == temp.size) {
                        var match = true
                        for (i in targetTemp.indices) {
                            if (!targetTemp[i].startsWith(temp[i].toUpperCase())) {
                                match = false
                                break
                            }
                        }
                        if (match) paletteList.add(ItemStack(m))
                    }
                }
            }
        } else {
            if (sortedList.isEmpty()) SortAllItems()
            paletteList = sortedList
        }
        paletteList.removeIf { itemStack -> ShopUtil.findItemFromShop(shopName, ItemStack(itemStack.getType())) !== -1 }
        return paletteList
    }

    private fun ShowItems() {
        for (i in 0..44) {
            try {
                val idx = i + (currentPage - 1) * 45
                if (idx >= paletteList.size()) break
                val btn: ItemStack = paletteList.get(idx)
                val btnMeta: ItemMeta = btn.getItemMeta()
                var lastName: String = btn.getType().name()
                val subStrIdx: Int = lastName.lastIndexOf('_')
                if (subStrIdx != -1) lastName = lastName.substring(subStrIdx)
                if (btnMeta != null) {
                    val lore: Array<String> = t(player, "PALETTE.LORE").replace("{item}", lastName.replace("_", "")).split("\n")
                    btnMeta.setLore(ArrayList(Arrays.asList(lore)))
                    btn.setItemMeta(btnMeta)
                }
                inventory.setItem(i, btn)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun SortAllItems() {
        val allItems: ArrayList<ItemStack> = ArrayList()
        for (m in Material.values()) {
            if (m.isAir()) continue
            if (m.isItem()) allItems.add(ItemStack(m))
        }
        allItems.sort((label@ Comparator<ItemStack> { o1, o2 ->
            if (o1.getType().getMaxDurability() > 0 && o2.getType().getMaxDurability() > 0) return@label 0 else if (o1.getType().getMaxDurability() > 0) return@label -1 else if (o2.getType().getMaxDurability() > 0) return@label 1
            val isEdible: Int = Boolean.compare(o2.getType().isEdible(), o1.getType().isEdible())
            if (isEdible != 0) return@label isEdible
            val isSolid: Int = Boolean.compare(o2.getType().isSolid(), o1.getType().isSolid())
            if (isSolid != 0) return@label isSolid
            val isRecord: Int = Boolean.compare(o2.getType().isRecord(), o1.getType().isRecord())
            if (isRecord != 0) return@label isRecord
            0
        } as Comparator<ItemStack?>).thenComparing { stack: ItemStack -> GetArmorType(stack) }.thenComparing { stack: ItemStack -> GetSortName(stack) })
        sortedList = allItems
    }

    private fun GetItemLastName(iStack: ItemStack): String {
        var itemName: String = iStack.getType().name()
        val idx: Int = itemName.lastIndexOf('_')
        if (idx != -1) itemName = itemName.substring(idx)
        return itemName.replace("_", "")
    }

    private fun CloseUI() {
        DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1)
    }

    private fun MovePage(isLeft: Boolean, isRight: Boolean) {
        var targetPage = currentPage
        if (isLeft) {
            targetPage -= 1
            if (targetPage < 1) targetPage = maxPage
        } else if (isRight) {
            targetPage += 1
            if (targetPage > maxPage) targetPage = 1
        }
        if (targetPage == currentPage) return
        DynaShopAPI.openItemPalette(player, shopName, shopSlotIndex, targetPage, search)
    }

    private fun AddAll() {
        if (paletteList.isEmpty()) return
        var targetSlotIdx: Int
        for (i in 0..44) {
            if (inventory.getItem(i) != null) {
                val material: Material = inventory.getItem(i).getType()
                if (material === Material.AIR) continue
                val itemStack = ItemStack(material) // UI요소를 그대로 쓰는 대신 새로 생성.
                val existSlot: Int = ShopUtil.findItemFromShop(shopName, itemStack)
                if (-1 != existSlot) // 이미 상점에 등록되어 있는 아이템 무시
                    continue
                targetSlotIdx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true)
                ShopUtil.addItemToShop(shopName, targetSlotIdx, itemStack, 1, 1, 0.01, -1, 10000, 10000)
            }
        }
        DynaShopAPI.openShopGui(player, shopName, 1)
    }

    private fun OnClickSearch(isLeft: Boolean, isRight: Boolean) {
        if (isLeft) {
            player.closeInventory()
            DynamicShop.userTempData.put(player.getUniqueId(), "waitforPalette")
            OnChat.WaitForInput(player)
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SEARCH_ITEM"))
        } else if (isRight) {
            if (!search.isEmpty()) DynaShopAPI.openItemPalette(player, shopName, shopSlotIndex, currentPage, "")
        }
    }

    private fun OnClickItem(isLeft: Boolean, isRight: Boolean, isShift: Boolean, item: ItemStack?) {
        if (item == null || item.getType() === Material.AIR) return

        // 인자로 들어오는 item은 UI요소임
        val itemStack = ItemStack(item.getType())
        if (isLeft) {
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, 0, itemStack, 10, 10, 0.01, -1, 10000, 10000, -1)
        } else if (isRight) {
            val targetSlotIdx: Int = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true)
            DynamicShop.userInteractItem.put(player.getUniqueId(), shopName + "/" + targetSlotIdx + 1)
            ShopUtil.addItemToShop(shopName, targetSlotIdx, itemStack, -1, -1, -1, -1, -1, -1)
            DynaShopAPI.openShopGui(player, shopName, targetSlotIdx / 45 + 1)
        }
    }

    private fun OnClickUserItem(isLeft: Boolean, isRight: Boolean, item: ItemStack?) {
        if (item == null || item.getType() === Material.AIR) return
        if (isLeft) {
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, 0, item, 10, 10, 0.01, -1, 10000, 10000, -1)
        } else if (isRight) {
            ShopUtil.addItemToShop(shopName, shopSlotIndex, item, -1, -1, -1, -1, -1, -1)
            DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1)
        }
    }

    companion object {
        private var sortedList: ArrayList<ItemStack> = ArrayList()
        private fun GetSortName(stack: ItemStack): String {
            var ret: String = stack.getType().name()
            val idx: Int = ret.lastIndexOf('_')
            //int idx = ret.indexOf('_');
            if (idx != -1) ret = ret.substring(idx)
            return ret
        }

        private fun GetArmorType(stack: ItemStack): Int {
            val name: String = stack.getType().name()
            if (name.contains("HELMET")) return 0
            if (name.contains("CHESTPLATE")) return 1
            if (name.contains("LEGGINGS")) return 2
            if (name.contains("BOOTS")) return 3
            return if (name.contains("TURTLE_SHELL")) 4 else 5
        }
    }
}