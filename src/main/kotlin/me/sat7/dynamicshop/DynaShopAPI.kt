package me.sat7.dynamicshop

import me.sat7.dynamicshop.files.CustomConfig

object DynaShopAPI {
    fun IsShopEnable(shopName: String?): Boolean {
        if (!ShopUtil.shopConfigFiles.containsKey(shopName)) return false
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        return shopData.get().getBoolean("Options.enable", true)
    }

    // 상점 UI생성 후 열기
    fun openShopGui(player: Player, shopName: String?, page: Int) {
        if (!IsShopEnable(shopName)) {
            if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_DISABLED"))
            } else {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"))
                return
            }
        }
        val uiClass = Shop()
        val inventory: Inventory = uiClass.getGui(player, shopName, page)
        if (inventory != null) {
            UIManager.Open(player, inventory, uiClass)
        }
    }

    // 상점 설정 화면
    fun openShopSettingGui(player: Player?, shopName: String?) {
        val uiClass = ShopSettings()
        val inventory: Inventory = uiClass.getGui(player, shopName)
        UIManager.Open(player, inventory, uiClass)
    }

    // 거래화면 생성 및 열기
    fun openItemTradeGui(player: Player, shopName: String?, tradeIdx: String?) {
        if (!IsShopEnable(shopName)) {
            if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                //player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_DISABLED"));
            } else {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"))
                return
            }
        }
        val uiClass = ItemTrade()
        val inventory: Inventory = uiClass.getGui(player, shopName, tradeIdx)
        UIManager.Open(player, inventory, uiClass)
    }

    // 아이탬 파렛트 생성 및 열기
    fun openItemPalette(player: Player?, shopName: String?, targetSlot: Int, page: Int, search: String?) {
        val uiClass = ItemPalette()
        val inventory: Inventory = uiClass.getGui(player, shopName, targetSlot, page, search)
        UIManager.Open(player, inventory, uiClass)
    }

    // 아이탬 셋팅창
    fun openItemSettingGui(player: Player?, shopName: String?, shopSlotIndex: Int, tab: Int, itemStack: ItemStack?, buyValue: Double, sellValue: Double, minPrice: Double, maxPrice: Double, median: Int, stock: Int, maxStock: Int) {
        val dsItem = DSItem(itemStack, buyValue, sellValue, minPrice, maxPrice, median, stock, maxStock)
        openItemSettingGui(player, shopName, shopSlotIndex, tab, dsItem)
    }

    fun openItemSettingGui(player: Player?, shopName: String?, shopSlotIndex: Int, tab: Int, dsItem: DSItem?) {
        val uiClass = ItemSettings()
        val inventory: Inventory = uiClass.getGui(player, shopName, shopSlotIndex, tab, dsItem)
        UIManager.Open(player, inventory, uiClass)
    }

    // 스타트 페이지
    fun openStartPage(player: Player?) {
        val uiClass = StartPage()
        val inventory: Inventory = uiClass.getGui(player)
        UIManager.Open(player, inventory, uiClass)
    }

    // 상점 목록창
    fun openShopListUI(player: Player?, page: Int, slotIndex: Int) {
        val uiClass = ShopList()
        val inventory: Inventory = uiClass.getGui(player, page, slotIndex)
        UIManager.Open(player, inventory, uiClass)
    }

    // 컬러 픽커
    fun openColorPicker(player: Player?, slotIndex: Int) {
        val uiClass = ColorPicker()
        val inventory: Inventory = uiClass.getGui(player, slotIndex)
        UIManager.Open(player, inventory, uiClass)
    }

    // 퀵셀 창
    fun openQuickSellGUI(player: Player?) {
        val uiClass = QuickSell()
        val inventory: Inventory = uiClass.getGui(player)
        UIManager.Open(player, inventory, uiClass)
    }

    // 유저 데이터를 다시 만들고 만들어졌는지 확인함.
    fun recreateUserData(player: Player): Boolean {
        if (DynamicShop.ccUser.get().contains(player.getUniqueId().toString())) {
            return true
        }
        DynamicShop.userTempData.put(player.getUniqueId(), "")
        DynamicShop.userInteractItem.put(player.getUniqueId(), "")
        DynamicShop.ccUser.get().set(player.getUniqueId() + ".cmdHelp", true)
        DynamicShop.ccUser.get().set(player.getUniqueId() + ".lastJoin", System.currentTimeMillis())
        DynamicShop.ccUser.save()
        return DynamicShop.ccUser.get().contains(player.getUniqueId().toString())
    }

    // 스타트페이지 셋팅창
    fun openStartPageSettingGui(player: Player?, slotIndex: Int) {
        val uiClass = StartPageSettings()
        val inventory: Inventory = uiClass.getGui(player, slotIndex)
        UIManager.Open(player, inventory, uiClass)
    }

    /**
     * Get the tax rate for a shop or the global tax rate if none is set.
     * Will return the global tax if shopName is null.
     *
     * @param shopName The shop name to check tax for or null
     * @return The tax rate
     * @throws IllegalArgumentException When the shop does not exist and is not null
     */
    fun getTaxRate(shopName: String?): Int {
        return if (shopName != null) {
            if (validateShopName(shopName)) {
                Calc.getTaxRate(shopName)
            } else {
                throw IllegalArgumentException("Shop: $shopName does not exist")
            }
        } else {
            ConfigUtil.getCurrentTax()
        }
    }

    /**
     * Get the list of shops
     *
     * @return ArrayList of String containing the list of names of loaded shops
     */
    val shops: ArrayList<String>
        get() = ArrayList(ShopUtil.shopConfigFiles.keySet())

    /**
     * Get the items in a shop
     *
     * @param shopName The name of the shop to get the items from
     * @return ArrayList of ItemStack containing the items for sale in the shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    fun getShopItems(@NonNull shopName: String): ArrayList<ItemStack> {
        return if (validateShopName(shopName)) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            val list: ArrayList<ItemStack> = ArrayList()
            for (s in data.get().getKeys(false)) {
                try {
                    val i: Int = Integer.parseInt(s)
                } catch (e: Exception) {
                    continue
                }
                if (!data.get().contains("$s.value")) {
                    continue  // 장식용임
                }
                var m: Material
                val itemName: String = data.get().getString("$s.mat") // 메테리얼
                try {
                    val mat: Material = Material.getMaterial(itemName)
                    list.add(ItemStack(mat))
                } catch (ignored: Exception) {
                }
            }
            list
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Get the buy price of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the price of
     * @return The buy price of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    fun getBuyPrice(@NonNull shopName: String, @NonNull itemStack: ItemStack?): Double {
        return if (validateShopName(shopName)) {
            val idx: Int = ShopUtil.findItemFromShop(shopName, itemStack)
            if (idx != -1) {
                Calc.getCurrentPrice(shopName, String.valueOf(idx), true)
            } else {
                idx.toDouble()
            }
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Get the sell price of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the price of
     * @return The sell price of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    fun getSellPrice(@NonNull shopName: String, @NonNull itemStack: ItemStack?): Double {
        return if (validateShopName(shopName)) {
            val idx: Int = ShopUtil.findItemFromShop(shopName, itemStack)
            if (idx != -1) {
                Calc.getCurrentPrice(shopName, String.valueOf(idx), false)
            } else {
                idx.toDouble()
            }
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Get the current stock of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the stock of
     * @return The stock of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    fun getStock(@NonNull shopName: String, @NonNull itemStack: ItemStack?): Int {
        return if (validateShopName(shopName)) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            val idx: Int = ShopUtil.findItemFromShop(shopName, itemStack)
            if (idx != -1) {
                data.get().getInt("$idx.stock")
            } else {
                idx
            }
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Get the median stock of an item
     *
     * @param shopName  The shop that has the item
     * @param itemStack The item to check the median stock of
     * @return The median stock of the item, -1 if the shop does not contain the item
     * @throws IllegalArgumentException When the shop does not exist
     */
    @Throws(IllegalArgumentException::class)
    fun getMedian(@NonNull shopName: String, @NonNull itemStack: ItemStack?): Int {
        return if (validateShopName(shopName)) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            val idx: Int = ShopUtil.findItemFromShop(shopName, itemStack)
            if (idx != -1) {
                data.get().getInt("$idx.median")
            } else {
                idx
            }
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Get whether a shop is for Vault money or Jobs points
     *
     * @param shopName The shop to check the type of
     * @return True if it is a Job Point shop, False if it is a Vault economy money shop
     * @throws IllegalArgumentException When the shop does not exist
     */
    @Throws(IllegalArgumentException::class)
    fun isJobsPointShop(@NonNull shopName: String): Boolean {
        return if (validateShopName(shopName)) {
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            data.get().contains("Options.flag.jobpoint")
        } else {
            throw IllegalArgumentException("Shop: $shopName does not exist")
        }
    }

    /**
     * Check if a shop exists
     *
     * @param shopName The shop name to check for
     * @return True if it exists
     */
    fun validateShopName(@NonNull shopName: String?): Boolean {
        return shops.contains(shopName)
    }

    /**
     * Find the best shop to sell.
     * Depending on the player's permission and the state of the store, there may not be an appropriate target.
     *
     * @param player seller
     * @return [0]shopName. return "" if null. [1]tradeIdx. return -1 if null.
     */
    fun FindTheBestShopToSell(player: Player, itemStack: ItemStack?, openTradeView: Boolean): Array<String> {
        val ret: Array<String> = ShopUtil.FindTheBestShopToSell(player, itemStack)
        if (openTradeView) {
            openItemTradeGui(player, ret[0], ret[1])
        }
        return ret
    }

    /**
     * Quick Sell
     *
     * @param player seller. This can be null. If null, permission and time are not checked.
     * @param itemStack Item to sell)
     * @return price sum.
     */
    fun QuickSell(player: Player?, itemStack: ItemStack?): Double {
        val ret: Array<String> = ShopUtil.FindTheBestShopToSell(player, itemStack)
        return if (!validateShopName(ret[0])) 0 else Sell.quickSellItem(player, itemStack, ret[0], Integer.parseInt(ret[1]), true, -1)
    }

    /**
     * Search for empty slots in a specific shop.
     *
     * @param shopName shop name
     * @return Returns the index of an empty slot. (first slot only). Returns -1 if there is no empty slot.
     */
    fun FindEmptySlot(shopName: String?): Int {
        return ShopUtil.findEmptyShopSlot(shopName, 0, false)
    }
}