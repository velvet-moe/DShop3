package me.sat7.dynamicshop.guis

import java.util.ArrayList

class ItemSettings : InGameUI() {
    private val SAMPLE_ITEM = 0
    private val DONE = 8
    private val CLOSE = 27
    private val RECOMMEND = 31
    private val REMOVE = 35
    private val BUY_VALUE = 1
    private val SELL_VALUE = 2
    private val MIN_VALUE = 3
    private val MAX_VALUE = 4
    private val MEDIAN = 5
    private val STOCK = 6
    private val MAX_STOCK = 7
    private val TAB_START = BUY_VALUE
    private val TAB_END = MAX_STOCK
    private val RESET = 13
    private val ROUND_DOWN = 20
    private val DIVIDE = 21
    private val SHIFT = 22
    private val MULTIPLY = 23
    private val SET_TO_OTHER = 24
    private var player: Player? = null
    private var shopName: String? = null
    private var shopSlotIndex = 0
    private var dsItem: DSItem? = null
    private var currentTab = 0
    private var buyValue = 0.0
    private var sellValue = 0.0
    private var minValue = 0.0
    private var maxValue = 0.0
    private var median = 0
    private var stock = 0
    private var maxStock = 0
    private var oldSbSame = false

    init {
        uiType = UI_TYPE.ItemSettings
    }

    fun getGui(player: Player?, shopName: String, shopSlotIndex: Int, tab: Int, dsItem: DSItem): Inventory {
        this.player = player
        this.shopName = shopName
        this.shopSlotIndex = shopSlotIndex
        this.dsItem = dsItem
        currentTab = Clamp(tab, TAB_START, TAB_END)
        inventory = Bukkit.createInventory(player, 36, t(player, "ITEM_SETTING_TITLE") + "§7 | §8" + shopName)
        var buyValueStr: String = t(null, "ITEM_SETTING.VALUE_BUY") + n(dsItem.getBuyValue())
        var sellValueStr: String = t(null, "ITEM_SETTING.VALUE_SELL") + n(dsItem.getSellValue())
        var priceMinStr: String = t(null, "ITEM_SETTING.PRICE_MIN") + n(dsItem.getMinPrice())
        var priceMaxStr: String = t(null, "ITEM_SETTING.PRICE_MAX") + n(dsItem.getMaxPrice())
        var medianStr: String = t(null, "ITEM_SETTING.MEDIAN") + n(dsItem.getMedian())
        var stockStr: String = t(null, "ITEM_SETTING.STOCK") + n(dsItem.getStock())
        var maxStockStr: String = t(null, "ITEM_SETTING.MAX_STOCK") + n(dsItem.getMaxStock())
        val sellValueLore = if (dsItem.getBuyValue() !== dsItem.getSellValue()) "§8(" + t(null, "ITEM_SETTING.TAX_IGNORED") + ")" else ""
        var medianLore: String? = if (dsItem.getMedian() <= 0) """
     ${"§8(" + t(null, "ITEM_SETTING.STATIC_PRICE")})
     
     """.trimIndent() else ""
        medianLore += t(null, "ITEM_SETTING.MEDIAN_HELP")
        val stockLore = if (dsItem.getStock() <= 0) "§8(" + t(null, "ITEM_SETTING.INF_STOCK") + ")" else ""
        val maxPriceLore = if (dsItem.getMaxPrice() <= 0) "§8(" + t(null, "ITEM_SETTING.UNLIMITED") + ")" else ""
        var maxStockLore = if (dsItem.getMaxStock() <= 0) "§8(" + t(null, "ITEM_SETTING.UNLIMITED") + ")" else ""
        maxStockLore += """
               
               ${t(null, "ITEM_SETTING.MAX_STOCK_LORE")}
               """.trimIndent()
        val red: Material = Material.RED_STAINED_GLASS_PANE
        val gray: Material = Material.GRAY_STAINED_GLASS_PANE
        val blue: Material = Material.BLUE_STAINED_GLASS_PANE
        val white: Material = Material.WHITE_STAINED_GLASS_PANE
        val yellow: Material = Material.YELLOW_STAINED_GLASS_PANE
        CreateButton(BUY_VALUE, if (currentTab == BUY_VALUE) red else blue, buyValueStr, "")
        CreateButton(SELL_VALUE, if (currentTab == SELL_VALUE) red else gray, sellValueStr, sellValueLore)
        CreateButton(MIN_VALUE, if (currentTab == MIN_VALUE) red else gray, priceMinStr, "")
        CreateButton(MAX_VALUE, if (currentTab == MAX_VALUE) red else gray, priceMaxStr, maxPriceLore)
        CreateButton(MEDIAN, if (currentTab == MEDIAN) red else blue, medianStr, medianLore)
        CreateButton(STOCK, if (currentTab == STOCK) red else blue, stockStr, stockLore)
        CreateButton(MAX_STOCK, if (currentTab == MAX_STOCK) red else gray, maxStockStr, maxStockLore)
        CreateButton(SHIFT, Material.BLACK_STAINED_GLASS_PANE, "Shift = x5", "")

        // 조절버튼
        if (dsItem.getBuyValue() === dsItem.getSellValue()) sellValueStr = "§8" + ChatColor.stripColor(sellValueStr)
        if (dsItem.getMinPrice() <= 0.01) priceMinStr = "§8" + ChatColor.stripColor(priceMinStr)
        if (dsItem.getMaxPrice() <= 0) priceMaxStr = "§8" + ChatColor.stripColor(priceMaxStr)
        if (dsItem.getMaxStock() <= 0) maxStockStr = "§8" + ChatColor.stripColor(maxStockStr)
        if (currentTab == BUY_VALUE) buyValueStr = "§3>$buyValueStr" else if (currentTab == SELL_VALUE) sellValueStr = "§3>$sellValueStr" else if (currentTab == MIN_VALUE) priceMinStr = "§3>$priceMinStr" else if (currentTab == MAX_VALUE) priceMaxStr = "§3>$priceMaxStr" else if (currentTab == MEDIAN) medianStr = "§3>$medianStr" else if (currentTab == STOCK) stockStr = "§3>$stockStr" else if (currentTab == MAX_STOCK) maxStockStr = "§3>$maxStockStr"
        if (dsItem.getMaxPrice() <= 0) priceMaxStr = priceMaxStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.UNLIMITED")) + ")"
        if (dsItem.getMedian() <= 0) medianStr = medianStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.STATIC_PRICE")) + ")"
        if (dsItem.getStock() <= 0) stockStr = stockStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.INF_STOCK")) + ")"
        if (dsItem.getMaxStock() <= 0) maxStockStr = maxStockStr + "§8(" + ChatColor.stripColor(t(null, "ITEM_SETTING.UNLIMITED")) + ")"
        val editBtnLore: ArrayList<String> = ArrayList()
        editBtnLore.add("§3§m                       ")
        editBtnLore.add(buyValueStr)
        editBtnLore.add(sellValueStr)
        editBtnLore.add(priceMinStr)
        editBtnLore.add(priceMaxStr)
        editBtnLore.add(medianStr)
        editBtnLore.add(stockStr)
        editBtnLore.add(maxStockStr)
        editBtnLore.add("§3§m                       ")
        var buyPrice: Double
        var sellPrice: Double
        if (dsItem.getMedian() <= 0 || dsItem.getStock() <= 0) {
            buyPrice = dsItem.getBuyValue()
            if (dsItem.getBuyValue() !== dsItem.getSellValue()) {
                editBtnLore.add("§7" + ChatColor.stripColor(t(null, "ITEM_SETTING.TAX_IGNORED")))
                sellPrice = dsItem.getSellValue()
            } else {
                var taxStr = "§7" + ChatColor.stripColor(t(null, "TAX.SALES_TAX")) + ": "
                taxStr += Calc.getTaxRate(shopName) + "%"
                editBtnLore.add(taxStr)
                sellPrice = buyPrice - buyPrice / 100.0 * Calc.getTaxRate(shopName)
            }
        } else {
            buyPrice = dsItem.getBuyValue() * dsItem.getMedian() / dsItem.getStock()
            if (buyPrice < 0.01) buyPrice = 0.01
            if (dsItem.getBuyValue() !== dsItem.getSellValue()) // 판매가 별도설정
            {
                editBtnLore.add("§7" + ChatColor.stripColor(t(null, "ITEM_SETTING.TAX_IGNORED")))
                sellPrice = dsItem.getSellValue() * dsItem.getMedian() / dsItem.getStock()
            } else {
                var taxStr = "§7" + ChatColor.stripColor(t(null, "TAX.SALES_TAX")) + ": "
                val config: FileConfiguration = ShopUtil.shopConfigFiles.get(shopName).get()
                if (config.contains("Options.SalesTax")) {
                    taxStr += config.getInt("Options.SalesTax") + "%"
                    sellPrice = buyPrice - buyPrice / 100.0 * config.getInt("Options.SalesTax")
                } else {
                    taxStr += ConfigUtil.getCurrentTax() + "%"
                    sellPrice = buyPrice - buyPrice / 100.0 * ConfigUtil.getCurrentTax()
                }
                sellPrice = Math.round(sellPrice * 100) / 100.0
                editBtnLore.add(taxStr)
            }
        }
        editBtnLore.add(t(null, "ITEM_SETTING.BUY").replace("{num}", n(buyPrice)))
        editBtnLore.add(t(null, "ITEM_SETTING.SELL").replace("{num}", n(sellPrice)))
        CreateButton(RESET, white, "Reset", editBtnLore)
        CreateButton(ROUND_DOWN, white, t(null, "ITEM_SETTING.ROUND_DOWN"), editBtnLore)
        CreateButton(DIVIDE, white, "/2", editBtnLore)
        CreateButton(MULTIPLY, white, "x2", editBtnLore)
        if (currentTab <= MAX_VALUE) {
            CreateButton(9, white, "-100", editBtnLore)
            CreateButton(10, white, "-10", editBtnLore)
            CreateButton(11, white, "-1", editBtnLore)
            CreateButton(12, white, "-0.1", editBtnLore)
            CreateButton(14, white, "+0.1", editBtnLore)
            CreateButton(15, white, "+1", editBtnLore)
            CreateButton(16, white, "+10", editBtnLore)
            CreateButton(17, white, "+100", editBtnLore)
            if (currentTab >= SELL_VALUE) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_VALUE"), editBtnLore)
        } else {
            CreateButton(9, white, "-1000", editBtnLore)
            CreateButton(10, white, "-100", editBtnLore)
            CreateButton(11, white, "-10", editBtnLore)
            CreateButton(12, white, "-1", editBtnLore)
            CreateButton(14, white, "+1", editBtnLore)
            CreateButton(15, white, "+10", editBtnLore)
            CreateButton(16, white, "+100", editBtnLore)
            CreateButton(17, white, "+1000", editBtnLore)
            if (currentTab == MEDIAN) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_STOCK"), editBtnLore) else if (currentTab == STOCK) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_MEDIAN"), editBtnLore) else if (currentTab == MAX_STOCK) CreateButton(SET_TO_OTHER, yellow, t(null, "ITEM_SETTING.SET_TO_STOCK"), editBtnLore)
        }
        inventory.setItem(SAMPLE_ITEM, dsItem.getItemStack()) // 아이탬 견본
        inventory.getItem(SAMPLE_ITEM).setAmount(1)
        val worth = TryGetWorth(dsItem.getItemStack().getType().name())
        val recommendLore: String
        if (worth == 0.0) {
            recommendLore = t(player, "ERR.NO_RECOMMEND_DATA")
        } else {
            val sugMid: Int = ShopUtil.CalcRecommendedMedian(worth, DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer"))
            val worthChanged = if (dsItem.getBuyValue() === worth) " ▶§f " else " ▶§a "
            val worthChanged2 = if (dsItem.getSellValue() === worth) " ▶§f " else " ▶§a "
            //String minChanged = (dsItem.getMinPrice() == 0.01) ? " ▶§f " : " ▶§a ";
            //String maxChanged = (dsItem.getMaxPrice() == -1) ? " ▶§f " : " ▶§a ";
            val medianChanged = if (dsItem.getMedian() === sugMid) " ▶§f " else " ▶§a "
            val stockChanged = if (dsItem.getStock() === sugMid) " ▶§f " else " ▶§a "
            recommendLore = (((((((t(null, "ITEM_SETTING.VALUE_BUY") + "\n"
                    + "§7 " + dsItem.getBuyValue() + worthChanged + worth).toString() + "\n"
                    + t(null, "ITEM_SETTING.VALUE_SELL")).toString() + "\n"
                    + "§7 " + dsItem.getSellValue() + worthChanged2 + worth).toString() + "\n" //+ t(null, "ITEM_SETTING.PRICE_MIN") + "\n"
                    //+ "§7 " + dsItem.getMinPrice() + minChanged + 0.01 + "\n"
                    //+ t(null, "ITEM_SETTING.PRICE_MAX") + "\n"
                    //+ "§7 " + dsItem.getMaxPrice() + maxChanged + -1 + "\n"
                    + t(null, "ITEM_SETTING.MEDIAN")).toString() + "\n"
                    + "§7 " + dsItem.getMedian() + medianChanged + sugMid).toString() + "\n"
                    + t(null, "ITEM_SETTING.STOCK")).toString() + "\n"
                    + "§7 " + dsItem.getStock() + stockChanged + sugMid)
        }
        CreateButton(RECOMMEND, Material.NETHER_STAR, t(player, "ITEM_SETTING.RECOMMEND"), recommendLore) // 추천 버튼
        CreateButton(DONE, Material.STRUCTURE_VOID, t(player, "ITEM_SETTING.DONE"), t(player, "ITEM_SETTING.DONE_LORE")) // 완료 버튼
        CreateButton(CLOSE, Material.BARRIER, t(player, "ITEM_SETTING.CLOSE"), t(player, "ITEM_SETTING.CLOSE_LORE")) // 닫기 버튼
        CreateButton(REMOVE, Material.BONE, t(player, "ITEM_SETTING.REMOVE"), t(player, "ITEM_SETTING.REMOVE_LORE")) // 삭제 버튼
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        player = e.getWhoClicked() as Player
        if (e.getCurrentItem() == null) return
        buyValue = dsItem.getBuyValue()
        sellValue = dsItem.getSellValue()
        minValue = dsItem.getMinPrice()
        if (minValue <= 0) minValue = 0.01
        maxValue = dsItem.getMaxPrice()
        if (maxValue <= 0) maxValue = -1.0
        median = dsItem.getMedian()
        stock = dsItem.getStock()
        maxStock = dsItem.getMaxStock()
        oldSbSame = sellValue == buyValue
        if (e.getSlot() === CLOSE) DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1) else if (e.getSlot() === REMOVE) RemoveItem() else if (e.getSlot() === RECOMMEND) SetToRecommend() else if (e.getSlot() >= TAB_START && e.getSlot() <= TAB_END) ChangeTab(e.getSlot()) else if (e.getSlot() === RESET) Reset() else if (e.getSlot() >= 9 && e.getSlot() < 18) PlusMinus(e.isShiftClick(), e.getCurrentItem()) // RESET 이 13인것에 주의
        else if (e.getSlot() === DIVIDE) Divide(e.isShiftClick()) else if (e.getSlot() === MULTIPLY) Multiply(e.isShiftClick()) else if (e.getSlot() === ROUND_DOWN) RoundDown() else if (e.getSlot() === SET_TO_OTHER) SetEqualToOther() else if (e.getSlot() === DONE) SaveSetting()
    }

    private fun SaveSetting() {
        // 유효성 검사
        if (maxValue > 0 && buyValue > maxValue) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
            return
        }
        if (minValue > 0 && buyValue < minValue) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
            return
        }
        if (maxValue > 0 && sellValue > maxValue) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
            return
        }
        if (minValue > 0 && sellValue < minValue) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.DEFAULT_VALUE_OUT_OF_RANGE"))
            return
        }
        if (maxValue > 0 && minValue > 0 && minValue >= maxValue) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.MAX_LOWER_THAN_MIN"))
            return
        }
        val existSlot: Int = ShopUtil.findItemFromShop(shopName, inventory.getItem(SAMPLE_ITEM))
        if (-1 != existSlot) {
            ShopUtil.editShopItem(shopName, existSlot, buyValue, sellValue, minValue, maxValue, median, stock, maxStock)
            DynaShopAPI.openShopGui(player, shopName, existSlot / 45 + 1)
            SoundUtil.playerSoundEffect(player, "addItem")
        } else {
            var idx = -1
            try {
                idx = ShopUtil.findEmptyShopSlot(shopName, shopSlotIndex, true)
            } catch (ignore: Exception) {
            }
            if (idx != -1) {
                ShopUtil.addItemToShop(shopName, idx, inventory.getItem(SAMPLE_ITEM), buyValue, sellValue, minValue, maxValue, median, stock, maxStock)
                DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1)
                SoundUtil.playerSoundEffect(player, "addItem")
            }
        }
    }

    private fun RemoveItem() {
        ShopUtil.removeItemFromShop(shopName, shopSlotIndex)
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.ITEM_DELETED"))
        DynaShopAPI.openShopGui(player, shopName, shopSlotIndex / 45 + 1)
        SoundUtil.playerSoundEffect(player, "deleteItem")
    }

    private fun SetToRecommend() {
        val worth = TryGetWorth(dsItem.getItemStack().getType().name())
        if (worth == 0.0) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_RECOMMEND_DATA"))
        } else {
            val numberOfPlayer: Int = DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer")
            val sugMid: Int = ShopUtil.CalcRecommendedMedian(worth, numberOfPlayer)
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.RECOMMEND_APPLIED").replace("{playerNum}", numberOfPlayer.toString() + ""))
            DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, inventory.getItem(SAMPLE_ITEM),
                    worth, worth, minValue, maxValue, sugMid, sugMid, maxStock)
        }
    }

    private fun TryGetWorth(itemName: String): Double {
        var itemName = itemName
        var worth: Double = WorthUtil.ccWorth.get().getDouble(itemName)
        if (worth == 0.0) {
            itemName = itemName.replace("-", "")
            itemName = itemName.replace("_", "")
            itemName = itemName.toLowerCase()
            worth = WorthUtil.ccWorth.get().getDouble(itemName)
        }
        return worth
    }

    private fun ChangeTab(tabIndex: Int) {
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, tabIndex, dsItem)
    }

    private fun Reset() {
        if (currentTab == BUY_VALUE) buyValue = 10.0 else if (currentTab == SELL_VALUE) sellValue = 10.0 else if (currentTab == MIN_VALUE) minValue = 0.01 else if (currentTab == MAX_VALUE) maxValue = -1.0 else if (currentTab == MEDIAN) median = 10000 else if (currentTab == STOCK) stock = 10000 else if (currentTab == MAX_STOCK) maxStock = -1
        RefreshWindow()
    }

    private fun PlusMinus(isShift: Boolean, clickedButton: ItemStack) {
        val s: String = clickedButton.getItemMeta().getDisplayName()
        var editNum: Double = Double.parseDouble(s)
        if (isShift) editNum *= 5.0
        if (currentTab == BUY_VALUE) {
            buyValue += editNum
            if (buyValue < 0.01) buyValue = 0.01
            if (oldSbSame) sellValue = buyValue
        } else if (currentTab == SELL_VALUE) {
            sellValue += editNum
            if (sellValue < 0.01) sellValue = 0.01
        } else if (currentTab == MIN_VALUE) {
            minValue += editNum
            if (minValue < 0.01) minValue = 0.01
        } else if (currentTab == MAX_VALUE) {
            if (maxValue <= 0 && editNum > 0) maxValue = editNum else {
                maxValue += editNum
                if (maxValue < 0.01) maxValue = -1.0
            }
        } else if (currentTab == MEDIAN) {
            if (median <= 0 && editNum > 0) {
                median = editNum.toInt()
                if (stock == -1) stock = 1
            } else {
                median += editNum.toInt()
                if (median < 1) median = -1
            }
        } else if (currentTab == STOCK) {
            if (stock <= 0 && editNum > 0) {
                stock = editNum.toInt()
            } else {
                stock += editNum.toInt()
                if (stock < 1) {
                    stock = -1
                    median = -1
                    maxStock = -1
                }
            }
        } else if (currentTab == MAX_STOCK) {
            if (maxStock <= 0 && editNum > 0) {
                maxStock = editNum.toInt()
            } else {
                maxStock += editNum.toInt()
                if (maxStock < 1) maxStock = -1
            }
        }
        RefreshWindow()
    }

    private fun Divide(isShift: Boolean) {
        var div = 2
        if (isShift) div = 10
        if (currentTab == BUY_VALUE) {
            buyValue /= div.toDouble()
            if (buyValue < 0.01) buyValue = 0.01
            if (oldSbSame) sellValue = buyValue
        } else if (currentTab == SELL_VALUE) {
            sellValue /= div.toDouble()
            if (sellValue < 0.01) sellValue = 0.01
        } else if (currentTab == MIN_VALUE) {
            minValue /= div.toDouble()
            if (minValue < 0.01) minValue = 0.01
        } else if (currentTab == MAX_VALUE) {
            maxValue /= div.toDouble()
            if (maxValue < 0.01) maxValue = 0.01
        } else if (currentTab == MEDIAN) {
            if (median > 1) {
                median /= div
                if (median < 1) median = 1
            }
        } else if (currentTab == STOCK) {
            if (stock > 1) {
                stock /= div
                if (stock < 1) stock = 1
            }
        } else if (currentTab == MAX_STOCK) {
            if (maxStock > 1) {
                maxStock /= div
                if (maxStock < 1) maxStock = 1
            }
        }
        RefreshWindow()
    }

    private fun Multiply(isShift: Boolean) {
        var mul = 2
        if (isShift) mul = 10
        if (currentTab == BUY_VALUE) {
            buyValue *= mul.toDouble()
            if (oldSbSame) sellValue = buyValue
        } else if (currentTab == SELL_VALUE) sellValue *= mul.toDouble() else if (currentTab == MIN_VALUE) minValue *= mul.toDouble() else if (currentTab == MAX_VALUE) maxValue *= mul.toDouble() else if (currentTab == MEDIAN) median *= mul else if (currentTab == STOCK) stock *= mul else if (currentTab == MAX_STOCK) maxStock *= mul
        RefreshWindow()
    }

    private fun RoundDown() {
        if (currentTab == BUY_VALUE) {
            buyValue = MathUtil.RoundDown(buyValue)
            if (oldSbSame) sellValue = buyValue
        } else if (currentTab == SELL_VALUE) sellValue = MathUtil.RoundDown(sellValue) else if (currentTab == MIN_VALUE) minValue = MathUtil.RoundDown(minValue) else if (currentTab == MAX_VALUE) maxValue = MathUtil.RoundDown(maxValue) else if (currentTab == MEDIAN) median = MathUtil.RoundDown(median) else if (currentTab == STOCK) stock = MathUtil.RoundDown(stock) else if (currentTab == MAX_STOCK) maxStock = MathUtil.RoundDown(maxStock)
        RefreshWindow()
    }

    private fun SetEqualToOther() {
        if (currentTab == SELL_VALUE) sellValue = buyValue else if (currentTab == MIN_VALUE) minValue = buyValue else if (currentTab == MAX_VALUE) maxValue = buyValue else if (currentTab == MEDIAN) median = stock else if (currentTab == STOCK) stock = median else if (currentTab == MAX_STOCK) maxStock = stock
        RefreshWindow()
    }

    private fun ValueValidation() {
        if (buyValue < 0.01) buyValue = 0.01
        if (sellValue < 0.01) sellValue = 0.01
        if (minValue < 0.01) minValue = 0.01
        if (maxValue < -1) maxValue = -1.0
        if (median < -1) median = -1
        if (stock < -1) stock = -1
        if (maxStock < -1) maxStock = -1
    }

    private fun RefreshWindow() {
        ValueValidation()
        DynaShopAPI.openItemSettingGui(player, shopName, shopSlotIndex, currentTab, inventory.getItem(SAMPLE_ITEM), buyValue, sellValue, minValue, maxValue, median, stock, maxStock)
        SoundUtil.playerSoundEffect(player, "editItem")
    }
}