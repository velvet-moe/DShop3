package me.sat7.dynamicshop.guis

import java.util.ArrayList

class Shop : InGameUI() {
    private val CLOSE = 45
    private val PAGE = 49
    private val SHOP_INFO = 53
    private var player: Player? = null
    private var shopName: String? = null
    private var page = 0
    private var maxPage = 0
    var shopData: FileConfiguration? = null
    private var selectedSlot = -1

    init {
        uiType = UI_TYPE.Shop
    }

    fun getGui(player: Player, shopName: String, page: Int): Inventory? {
        shopData = ShopUtil.shopConfigFiles.get(shopName).get()

        // jobreborn 플러그인 있는지 확인.
        if (!JobsHook.jobsRebornActive && shopData.contains("Options.flag.jobpoint")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.JOBS_REBORN_NOT_FOUND"))
            return null
        }
        this.player = player
        this.shopName = shopName
        this.page = page
        selectedSlot = -1
        maxPage = GetShopMaxPage(shopName)
        this.page = Clamp(page, 1, maxPage)
        DynamicShop.userInteractItem.put(player.getUniqueId(), shopName + "/" + this.page)
        var uiName = if (shopData.getBoolean("Options.enable", true)) "" else t(player, "SHOP.DISABLED")
        uiName += "§3" + shopData.getString("Options.title", shopName)
        inventory = Bukkit.createInventory(player, 54, uiName)
        CreateCloseButton(player, CLOSE)
        CreateButton(PAGE, InGameUI.GetPageButtonIconMat(), CreatePageButtonName(), CreatePageButtonLore(), this.page)
        CreateButton(SHOP_INFO, InGameUI.GetShopInfoButtonIconMat(), "§3$shopName", CreateShopInfoText())
        ShowItems()
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        player = e.getWhoClicked() as Player
        if (!CheckShopIsEnable()) return
        if (e.getSlot() === CLOSE) CloseUI() else if (e.getSlot() === PAGE) OnClickPageButton(e.isLeftClick(), e.isRightClick(), e.isShiftClick()) else if (e.getSlot() === SHOP_INFO && e.isRightClick()) OnClickShopSettingsButton() else if (e.getSlot() <= 45) {
            val idx: Int = e.getSlot() + 45 * (page - 1)
            OnClickItemSlot(idx, e)
        }
    }

    @Override
    override fun OnClickLowerInventory(e: InventoryClickEvent) {
        if (!CheckShopIsEnable()) return
        if (!DynamicShop.plugin.getConfig().getBoolean("UI.EnableInventoryClickSearch.Shop")) return
        player = e.getWhoClicked() as Player
        val idx: Int = ShopUtil.findItemFromShop(shopName, e.getCurrentItem())
        if (idx != -1) {
            page = idx / 45 + 1
            RefreshUI()
        }
    }

    private fun ShowItems() {
        var idx = -1
        for (s in shopData.getKeys(false)) {
            try {
                // 현재 페이지에 해당하는 것들만 출력
                idx = Integer.parseInt(s)
                (idx -= page - 1) * 45
                if (!(idx < 45 && idx >= 0)) continue

                // 아이탬 생성
                val itemName: String = shopData.getString("$s.mat") // 메테리얼
                val itemStack = ItemStack(Material.getMaterial(itemName), 1) // 아이탬 생성
                itemStack.setItemMeta(shopData.get("$s.itemStack") as ItemMeta) // 저장된 메타 적용

                // 커스텀 메타 설정
                val meta: ItemMeta = itemStack.getItemMeta()
                var lore = ""

                // 상품
                if (shopData.contains("$s.value")) {
                    lore = l("SHOP.ITEM_INFO")
                    val stock: Int = shopData.getInt("$s.stock")
                    val maxStock: Int = shopData.getInt("$s.maxStock", -1)
                    var stockStr: String?
                    var maxStockStr: String? = ""
                    if (stock <= 0) {
                        stockStr = t(player, "SHOP.INF_STOCK")
                    } else if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack")) {
                        stockStr = t(player, "SHOP.STACKS").replace("{num}", n(stock / 64))
                    } else {
                        stockStr = n(stock)
                    }
                    if (maxStock != -1) {
                        if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack")) {
                            maxStockStr = t(player, "SHOP.STACKS").replace("{num}", n(maxStock / 64))
                        } else {
                            maxStockStr = n(maxStock)
                        }
                    }
                    val buyPrice: Double = Calc.getCurrentPrice(shopName, s, true)
                    val sellPrice: Double = Calc.getCurrentPrice(shopName, s, false)
                    var buyPrice2: Double = shopData.getDouble("$s.value")
                    if (shopData.contains("Options.flag.integeronly")) {
                        buyPrice2 = Math.ceil(buyPrice2)
                    }
                    val priceSave1 = buyPrice / buyPrice2 - 1
                    val priceSave2 = 1 - buyPrice / buyPrice2
                    var valueChanged_Buy: String
                    var valueChanged_Sell: String
                    if (buyPrice - buyPrice2 > 0.005) {
                        valueChanged_Buy = (t(player, "ARROW.UP_2") + n(priceSave1 * 100)).toString() + "%"
                        valueChanged_Sell = (t(player, "ARROW.UP") + n(priceSave1 * 100)).toString() + "%"
                    } else if (buyPrice - buyPrice2 < -0.005) {
                        valueChanged_Buy = (t(player, "ARROW.DOWN_2") + n(priceSave2 * 100)).toString() + "%"
                        valueChanged_Sell = (t(player, "ARROW.DOWN") + n(priceSave2 * 100)).toString() + "%"
                    } else {
                        valueChanged_Buy = ""
                        valueChanged_Sell = ""
                    }
                    var tradeType = "default"
                    if (shopData.contains("$s.tradeType")) tradeType = shopData.getString("$s.tradeType")
                    val showValueChange: Boolean = shopData.contains("Options.flag.showvaluechange")
                    var buyText = ""
                    var sellText = ""
                    if (!tradeType.equalsIgnoreCase("SellOnly")) {
                        buyText = t(player, "SHOP.BUY_PRICE").replace("{num}", n(buyPrice))
                        buyText += if (showValueChange) " $valueChanged_Buy" else ""
                    }
                    if (!tradeType.equalsIgnoreCase("BuyOnly")) {
                        sellText = t(player, "SHOP.SELL_PRICE").replace("{num}", n(sellPrice))
                        sellText += if (showValueChange) " $valueChanged_Sell" else ""
                    }
                    var pricingTypeText = ""
                    if (shopData.getInt("$s.stock") <= 0 || shopData.getInt("$s.median") <= 0) {
                        if (!shopData.contains("Options.flag.hidepricingtype")) {
                            pricingTypeText = t(player, "SHOP.STATIC_PRICE")
                        }
                    }
                    var stockText = ""
                    if (!shopData.contains("Options.flag.hidestock")) {
                        stockText = if (maxStock != -1 && shopData.contains("Options.flag.showmaxstock")) t(player, "SHOP.STOCK_2").replace("{stock}", stockStr).replace("{max_stock}", maxStockStr) else t(player, "SHOP.STOCK").replace("{num}", stockStr)
                    }
                    var tradeLoreText = ""
                    if (t(player, "SHOP.TRADE_LORE").length() > 0) tradeLoreText = t(player, "SHOP.TRADE_LORE")
                    var itemMetaLoreText = ""
                    if (meta != null && meta.hasLore()) {
                        for (tempLore in meta.getLore()) {
                            itemMetaLoreText += tempLore + "\n"
                        }
                        itemMetaLoreText = itemMetaLoreText.substring(0, itemMetaLoreText.length() - 2)
                    }
                    lore = lore.replace("{\\nBuy}", if (buyText.isEmpty()) "" else """
     
     $buyText
     """.trimIndent())
                    lore = lore.replace("{\\nSell}", if (sellText.isEmpty()) "" else """
     
     $sellText
     """.trimIndent())
                    lore = lore.replace("{\\nStock}", if (stockText.isEmpty()) "" else """
     
     $stockText
     """.trimIndent())
                    lore = lore.replace("{\\nPricingType}", if (pricingTypeText.isEmpty()) "" else """
     
     $pricingTypeText
     """.trimIndent())
                    lore = lore.replace("{\\nTradeLore}", if (tradeLoreText.isEmpty()) "" else """
     
     $tradeLoreText
     """.trimIndent())
                    lore = lore.replace("{\\nItemMetaLore}", if (itemMetaLoreText.isEmpty()) "" else """
     
     $itemMetaLoreText
     """.trimIndent())
                    lore = lore.replace("{Buy}", buyText)
                    lore = lore.replace("{Sell}", sellText)
                    lore = lore.replace("{Stock}", stockText)
                    lore = lore.replace("{PricingType}", pricingTypeText)
                    lore = lore.replace("{TradeLore}", tradeLoreText)
                    lore = lore.replace("{ItemMetaLore}", itemMetaLoreText)
                    val temp: String = lore.replace(" ", "")
                    if (ChatColor.stripColor(temp).startsWith("\n")) lore = lore.replaceFirst("\n", "")
                    if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                        lore += """
                            
                            ${t(player, "SHOP.ITEM_MOVE_LORE")}
                            """.trimIndent()
                        lore += """
                            
                            ${t(player, "SHOP.ITEM_EDIT_LORE")}
                            """.trimIndent()
                    }
                } else {
                    if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                        lore += t(player, "SHOP.ITEM_COPY_LORE")
                        lore += """
                            
                            ${t(player, "SHOP.DECO_DELETE_LORE")}
                            """.trimIndent()
                    }
                    meta.setDisplayName(" ")
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                }
                meta.setLore(ArrayList(Arrays.asList(lore.split("\n"))))
                itemStack.setItemMeta(meta)
                inventory.setItem(idx, itemStack)
            } catch (e: Exception) {
                if (!s.equalsIgnoreCase("Options") && player.hasPermission(P_ADMIN_SHOP_EDIT) && idx != -1) {
                    CreateButton(idx, Material.BARRIER, t(player, "SHOP.INCOMPLETE_DATA"), t(null, "SHOP.INCOMPLETE_DATA_Lore") + idx)
                }
            }
        }
    }

    private fun CreatePageButtonName(): String {
        var pageString: String = t(player, "SHOP.PAGE_TITLE")
        pageString = pageString.replace("{curPage}", page.toString() + "")
        pageString = pageString.replace("{maxPage}", maxPage.toString() + "")
        return pageString
    }

    private fun CreatePageButtonLore(): String {
        var pageLore: String = t(player, "SHOP.PAGE_LORE_V2")
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            pageLore += """
                
                ${t(player, "SHOP.PAGE_EDIT_LORE")}
                """.trimIndent()
        }
        return pageLore
    }

    private fun CreateShopInfoText(): String {
        var shopLore: String = l("SHOP.INFO")
        val finalLoreText = StringBuilder()
        if (shopData.contains("Options.lore")) {
            val loreTxt: String = shopData.getString("Options.lore")
            if (loreTxt != null && loreTxt.length() > 0) {
                val loreArray: Array<String> = loreTxt.split(Pattern.quote("\\n"))
                for (s in loreArray) {
                    finalLoreText.append("§f").append(s).append("\n")
                }
            }
        }

        // 권한
        var finalPermText = ""
        val perm: String = shopData.getString("Options.permission")
        if (!(perm.length() === 0)) {
            finalPermText += t(player, "SHOP.PERMISSION") + "\n"
            finalPermText += t(player, "SHOP.PERMISSION_ITEM").replace("{permission}", perm) + "\n"
        }

        // 세금
        var finalTaxText = ""
        finalTaxText += t(player, "TAX.SALES_TAX") + ":" + "\n"
        finalTaxText += (t(player, "SHOP.SHOP_INFO_DASH") + Calc.getTaxRate(shopName)).toString() + "%" + "\n"

        // 상점 잔액
        var finalShopBalanceText = ""
        if (!shopData.contains("Options.flag.hideshopbalance")) {
            finalShopBalanceText += t(player, "SHOP.SHOP_BAL") + "\n"
            if (ShopUtil.getShopBalance(shopName) >= 0) {
                var temp: String = n(ShopUtil.getShopBalance(shopName))
                if (shopData.contains("Options.flag.jobpoint")) temp += "Points"
                finalShopBalanceText += (t(player, "SHOP.SHOP_INFO_DASH") + temp).toString() + "\n"
            } else {
                finalShopBalanceText += (t(player, "SHOP.SHOP_INFO_DASH") + ChatColor.stripColor(t(player, "SHOP.SHOP_BAL_INF"))).toString() + "\n"
            }
        }

        // 영업시간
        var finalShopHourText = ""
        if (shopData.contains("Options.shophours")) {
            val temp: Array<String> = shopData.getString("Options.shophours").split("~")
            val open: Int = Integer.parseInt(temp[0])
            val close: Int = Integer.parseInt(temp[1])
            finalShopHourText += t(player, "TIME.SHOPHOURS") + "\n"
            finalShopHourText += (t(player, "SHOP.SHOP_INFO_DASH") + t(player, "TIME.OPEN")).toString() + ": " + open + "\n"
            finalShopHourText += (t(player, "SHOP.SHOP_INFO_DASH") + t(player, "TIME.CLOSE")).toString() + ": " + close + "\n"
        }

        // 상점 좌표
        var finalShopPosText = ""
        if (shopData.contains("Options.pos1") && shopData.contains("Options.pos2")) {
            finalShopPosText += t(player, "SHOP.SHOP_LOCATION_B") + "\n"
            finalShopPosText += (t(player, "SHOP.SHOP_INFO_DASH") + shopData.getString("Options.world")).toString() + "\n"
            finalShopPosText += (t(player, "SHOP.SHOP_INFO_DASH") + shopData.getString("Options.pos1")).toString() + "\n"
            finalShopPosText += (t(player, "SHOP.SHOP_INFO_DASH") + shopData.getString("Options.pos2")).toString() + "\n"
        }
        shopLore = shopLore.replace("{\\nShopLore}", if (finalLoreText.toString().isEmpty()) "" else """
     
     $finalLoreText
     """.trimIndent())
        shopLore = shopLore.replace("{\\nPermission}", if (finalPermText.isEmpty()) "" else """
     
     $finalPermText
     """.trimIndent())
        shopLore = shopLore.replace("{\\nTax}", """
     
     $finalTaxText
     """.trimIndent())
        shopLore = shopLore.replace("{\\nShopBalance}", if (finalShopBalanceText.isEmpty()) "" else """
     
     $finalShopBalanceText
     """.trimIndent())
        shopLore = shopLore.replace("{\\nShopHour}", if (finalShopHourText.isEmpty()) "" else """
     
     $finalShopHourText
     """.trimIndent())
        shopLore = shopLore.replace("{\\nShopPosition}", if (finalShopPosText.isEmpty()) "" else """
     
     $finalShopPosText
     """.trimIndent())
        shopLore = shopLore.replace("{ShopLore}", finalLoreText)
        shopLore = shopLore.replace("{Permission}", finalPermText)
        shopLore = shopLore.replace("{Tax}", finalTaxText)
        shopLore = shopLore.replace("{ShopBalance}", finalShopBalanceText)
        shopLore = shopLore.replace("{ShopHour}", finalShopHourText)
        shopLore = shopLore.replace("{ShopPosition}", finalShopPosText)
        val temp: String = shopLore.replace(" ", "")
        if (ChatColor.stripColor(temp).startsWith("\n")) shopLore = shopLore.replaceFirst("\n", "")

        // 어드민이면----------
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) shopLore += "\n"

        // 플래그
        var finalFlagText = ""
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            if (shopData.contains("Options.flag") && shopData.getConfigurationSection("Options.flag").getKeys(false).size() > 0) {
                finalFlagText = t(player, "SHOP.FLAGS") + "\n"
                for (s in shopData.getConfigurationSection("Options.flag").getKeys(false)) {
                    finalFlagText += t(player, "SHOP.FLAGS_ITEM").replace("{flag}", s) + "\n"
                }
                finalFlagText += "\n"
            }
        }
        shopLore += finalFlagText
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            shopLore += t(player, "SHOP_SETTING.SHOP_SETTINGS_LORE")
        }
        return shopLore
    }

    private fun CloseUI() {
        // 표지판으로 접근한 경우에는 그냥 창을 닫음
        if (DynamicShop.userTempData.get(player.getUniqueId()).equalsIgnoreCase("sign")) {
            DynamicShop.userTempData.put(player.getUniqueId(), "")
            player.closeInventory()
        } else {
            if (DynamicShop.plugin.getConfig().getBoolean("UI.OpenStartPageWhenClickCloseButton")) {
                DynaShopAPI.openStartPage(player)
            } else {
                ShopUtil.closeInventoryWithDelay(player)
            }
        }
    }

    private fun OnClickPageButton(isLeftClick: Boolean, isRightClick: Boolean, isShiftClick: Boolean) {
        var targetPage = page
        if (isLeftClick) {
            if (!isShiftClick) {
                targetPage -= 1
                if (targetPage < 1) targetPage = GetShopMaxPage(shopName)
            } else if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                ShopUtil.insetShopPage(shopName, page)
            }
        } else if (isRightClick) {
            if (!isShiftClick) {
                targetPage += 1
                if (targetPage > GetShopMaxPage(shopName)) targetPage = 1
            } else if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                if (shopData.getInt("Options.page") > 1) {
                    ShopUtil.closeInventoryWithDelay(player)
                    DynamicShop.userInteractItem.put(player.getUniqueId(), shopName.toString() + "/" + page)
                    DynamicShop.userTempData.put(player.getUniqueId(), "waitforPageDelete")
                    OnChat.WaitForInput(player)
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.DELETE_CONFIRM"))
                } else {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CANT_DELETE_LAST_PAGE"))
                }
                return
            }
        }
        page = targetPage
        RefreshUI()
    }

    private fun OnClickShopSettingsButton() {
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            DynaShopAPI.openShopSettingGui(player, shopName)
        }
    }

    private fun OnClickItemSlot(idx: Int, e: InventoryClickEvent) {
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() !== Material.AIR) {
            if (e.getCurrentItem().getItemMeta() != null &&
                    e.getCurrentItem().getItemMeta().getDisplayName().equals(t(null, "SHOP.INCOMPLETE_DATA"))) {
                return
            }

            // 거래화면 열기
            if (e.isLeftClick() && shopData.contains("$idx.value")) {
                SoundUtil.playerSoundEffect(player, "tradeview")
                DynaShopAPI.openItemTradeGui(player, shopName, String.valueOf(idx))
            } else if (e.isRightClick() && player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                if (e.isShiftClick()) {
                    if (shopData.contains("$idx.value")) {
                        val buyValue: Double = shopData.getDouble("$idx.value")
                        var sellValue = buyValue
                        if (shopData.contains("$idx.value2")) {
                            sellValue = shopData.getDouble("$idx.value2")
                        }
                        var valueMin: Double = shopData.getDouble("$idx.valueMin")
                        if (valueMin <= 0.01) valueMin = 0.01
                        var valueMax: Double = shopData.getDouble("$idx.valueMax")
                        if (valueMax <= 0) valueMax = -1.0
                        val median: Int = shopData.getInt("$idx.median")
                        val stock: Int = shopData.getInt("$idx.stock")
                        val maxStock: Int = shopData.getInt("$idx.maxStock", -1)
                        val iStack = ItemStack(e.getCurrentItem().getType())
                        iStack.setItemMeta(shopData.get("$idx.itemStack") as ItemMeta)
                        DynaShopAPI.openItemSettingGui(player, shopName, idx, 0, iStack, buyValue, sellValue, valueMin, valueMax, median, stock, maxStock)
                    } else {
                        ShopUtil.removeItemFromShop(shopName, idx)
                        selectedSlot = -1
                        RefreshUI()
                    }
                } else if (selectedSlot == -1) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "SHOP.ITEM_MOVE_SELECTED"))
                    selectedSlot = idx
                }
            }
        } else if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            // 아이탬 이동. 또는 장식 복사
            if (e.isRightClick() && selectedSlot != -1) {
                shopData.set(String.valueOf(idx), shopData.get(String.valueOf(selectedSlot)))
                if (shopData.contains("$selectedSlot.value")) {
                    shopData.set(String.valueOf(selectedSlot), null)
                }
                ShopUtil.shopConfigFiles.get(shopName).save()
                selectedSlot = -1
                RefreshUI()
            } else {
                DynaShopAPI.openItemPalette(player, shopName, idx, 1, "")
            }
        }
    }

    @Override
    override fun RefreshUI() {
        if (!CheckShopIsEnable()) return
        for (i in 0..44) inventory.setItem(i, null)
        val pageButton: ItemStack = inventory.getItem(PAGE)
        val pageButtonMeta: ItemMeta = pageButton.getItemMeta()
        maxPage = GetShopMaxPage(shopName)
        pageButtonMeta.setDisplayName(CreatePageButtonName())
        pageButton.setItemMeta(pageButtonMeta)
        pageButton.setAmount(page)
        val infoButton: ItemStack = inventory.getItem(SHOP_INFO)
        val infoMeta: ItemMeta = infoButton.getItemMeta()
        infoMeta.setLore(ArrayList(Arrays.asList(CreateShopInfoText().split("\n"))))
        infoButton.setItemMeta(infoMeta)
        ShowItems()
    }

    fun CheckShopIsEnable(): Boolean {
        if (!ShopUtil.shopConfigFiles.containsKey(shopName)) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.INVALID_TRANSACTION"))
            player.closeInventory()
            return false
        }
        val ret = DynaShopAPI.IsShopEnable(shopName) || player.hasPermission(P_ADMIN_SHOP_EDIT)
        if (!ret) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"))
            player.closeInventory()
        }
        return ret
    }
}