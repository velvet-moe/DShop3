package me.sat7.dynamicshop.guis

import java.util.ArrayList

class ItemTrade : InGameUI() {
    private val CLOSE = 9
    private val SELL_ONLY_TOGGLE = 1
    private val BUY_ONLY_TOGGLE = 10
    private val CHECK_BALANCE = 0
    private var player: Player? = null
    private var shopName: String? = null
    private var tradeIdx: String? = null
    private var deliveryCharge = 0
    private var shopData: FileConfiguration? = null
    private var sellBuyOnly: String? = null
    private var material: String? = null
    private var itemMeta: ItemMeta? = null

    init {
        uiType = UI_TYPE.ItemTrade
    }

    enum class CURRENCY {
        VAULT, JOB_POINT
    }

    fun getGui(player: Player, shopName: String, tradeIdx: String): Inventory {
        this.player = player
        this.shopName = shopName
        this.tradeIdx = tradeIdx
        deliveryCharge = CalcShipping(player, shopName)
        shopData = ShopUtil.shopConfigFiles.get(shopName).get()
        sellBuyOnly = shopData.getString(this.tradeIdx.toString() + ".tradeType", "")
        material = shopData.getString("$tradeIdx.mat")
        itemMeta = shopData.get("$tradeIdx.itemStack") as ItemMeta
        DynamicShop.userInteractItem.put(player.getUniqueId(), "$shopName/$tradeIdx")
        var uiTitle: String? = if (shopData.getBoolean("Options.enable", true)) "" else t(player, "SHOP.DISABLED")
        uiTitle += t(player, "TRADE_TITLE")
        inventory = Bukkit.createInventory(player, 18, uiTitle)
        CreateBalanceButton()
        CreateSellBuyOnlyToggle()
        CreateTradeButtons()
        CreateCloseButton(player, CLOSE)
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        player = e.getWhoClicked() as Player
        if (!CheckShopIsEnable()) return
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null) {
            if (e.getSlot() === CLOSE) {
                // 표지판을 클릭해서 거래화면에 진입한 경우에는 상점UI로 돌아가는 대신 인벤토리를 닫음
                if (DynamicShop.userTempData.get(player.getUniqueId()).equalsIgnoreCase("sign")) {
                    DynamicShop.userTempData.put(player.getUniqueId(), "")
                    player.closeInventory()
                } else {
                    DynaShopAPI.openShopGui(player, shopName, Integer.parseInt(tradeIdx) / 45 + 1)
                }
            } else if (e.getSlot() === CHECK_BALANCE) {
                if (data.get().contains("Options.flag.jobpoint")) {
                    player.sendMessage(((DynamicShop.dsPrefix(player) + t(player, "TRADE.BALANCE")).toString() + ":§f " + n(JobsHook.getCurJobPoints(player))).toString() + "Points")
                } else {
                    player.sendMessage((DynamicShop.dsPrefix(player) + t(player, "TRADE.BALANCE")).toString() + ":§f " + n(DynamicShop.getEconomy().getBalance(player)))
                }
            } else if (e.getSlot() === SELL_ONLY_TOGGLE) {
                if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                    val path = tradeIdx.toString() + ".tradeType"
                    if (sellBuyOnly == null || !sellBuyOnly.equalsIgnoreCase("SellOnly")) {
                        sellBuyOnly = "SellOnly"
                        data.get().set(path, "SellOnly")
                    } else {
                        sellBuyOnly = ""
                        data.get().set(path, null)
                    }
                    data.save()
                    RefreshUI()
                }
            } else if (e.getSlot() === BUY_ONLY_TOGGLE) {
                if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                    val path = tradeIdx.toString() + ".tradeType"
                    if (sellBuyOnly == null || !sellBuyOnly.equalsIgnoreCase("BuyOnly")) {
                        sellBuyOnly = "BuyOnly"
                        data.get().set(path, "BuyOnly")
                    } else {
                        sellBuyOnly = ""
                        data.get().set(path, null)
                    }
                    data.save()
                    RefreshUI()
                }
            } else {
                val tempIS = ItemStack(e.getCurrentItem().getType(), e.getCurrentItem().getAmount())
                tempIS.setItemMeta(data.get().get(tradeIdx.toString() + ".itemStack") as ItemMeta)

                // 무한재고&고정가격
                val infiniteStock: Boolean = data.get().getInt(tradeIdx.toString() + ".stock") <= 0

                // 배달비 계산
                val optionS: ConfigurationSection = data.get().getConfigurationSection("Options")
                if (optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge")) {
                    deliveryCharge = CalcShipping(player, shopName)
                    if (deliveryCharge == -1) {
                        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.DELIVERY_CHARGE_NA")) // 다른 월드로 배달 불가능
                        return
                    }
                }
                if (e.getSlot() <= 10) Sell(optionS, tempIS, deliveryCharge, infiniteStock) else Buy(optionS, tempIS, deliveryCharge, infiniteStock)
            }
        }
    }

    private fun CreateBalanceButton() {
        var moneyLore: String = l("TRADE_VIEW.BALANCE")
        val myBalanceString: String
        val optionS: ConfigurationSection = shopData.getConfigurationSection("Options")
        myBalanceString = if (optionS.contains("flag.jobpoint")) {
            "§f" + n(JobsHook.getCurJobPoints(player)) + "Points"
        } else {
            "§f" + n(DynamicShop.getEconomy().getBalance(player))
        }
        var balStr: String?
        if (ShopUtil.getShopBalance(shopName) >= 0) {
            val d: Double = ShopUtil.getShopBalance(shopName)
            balStr = n(d)
            if (optionS.contains("flag.jobpoint")) balStr += "Points"
        } else {
            balStr = t(player, "TRADE.SHOP_BAL_INF")
        }
        var shopBalanceString = ""
        if (!shopData.contains("Options.flag.hideshopbalance")) shopBalanceString = t(player, "TRADE.SHOP_BAL").replace("{num}", balStr)
        moneyLore = moneyLore.replace("{\\nPlayerBalance}", """
     
     $myBalanceString
     """.trimIndent())
        moneyLore = moneyLore.replace("{\\nShopBalance}", if (shopBalanceString.isEmpty()) "" else """
     
     $shopBalanceString
     """.trimIndent())
        moneyLore = moneyLore.replace("{PlayerBalance}", myBalanceString)
        moneyLore = moneyLore.replace("{ShopBalance}", shopBalanceString)
        val temp: String = moneyLore.replace(" ", "")
        if (ChatColor.stripColor(temp).startsWith("\n")) moneyLore = moneyLore.replaceFirst("\n", "")
        CreateButton(CHECK_BALANCE, Material.EMERALD, t(player, "TRADE.BALANCE"), moneyLore)
    }

    private fun CreateSellBuyOnlyToggle() {
        val sellLore: ArrayList<String> = ArrayList()
        if (sellBuyOnly.equalsIgnoreCase("SellOnly")) sellLore.add(t(player, "TRADE.SELL_ONLY_LORE")) else if (sellBuyOnly.equalsIgnoreCase("BuyOnly")) sellLore.add(t(player, "TRADE.BUY_ONLY_LORE"))
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) sellLore.add(t(player, "TRADE.TOGGLE_SELLABLE"))
        val buyLore: ArrayList<String> = ArrayList()
        if (sellBuyOnly.equalsIgnoreCase("SellOnly")) buyLore.add(t(player, "TRADE.SELL_ONLY_LORE")) else if (sellBuyOnly.equalsIgnoreCase("BuyOnly")) buyLore.add(t(player, "TRADE.BUY_ONLY_LORE"))
        if (player.hasPermission(P_ADMIN_SHOP_EDIT)) buyLore.add(t(player, "TRADE.TOGGLE_BUYABLE"))
        CreateButton(SELL_ONLY_TOGGLE, Material.GREEN_STAINED_GLASS, t(player, "TRADE.SELL"), sellLore)
        CreateButton(BUY_ONLY_TOGGLE, Material.RED_STAINED_GLASS, t(player, "TRADE.BUY"), buyLore)
    }

    private fun CreateTradeButtons() {
        if (!sellBuyOnly.equalsIgnoreCase("BuyOnly")) CreateTradeButtons(true)
        if (!sellBuyOnly.equalsIgnoreCase("SellOnly")) CreateTradeButtons(false)
    }

    private fun CreateTradeButtons(sell: Boolean) {
        var amount = 1
        var idx = if (sell) 2 else 11
        for (i in 1..7) {
            val itemStack = ItemStack(Material.getMaterial(material), amount)
            itemStack.setItemMeta(shopData.get(tradeIdx.toString() + ".itemStack") as ItemMeta)
            val meta: ItemMeta = itemStack.getItemMeta()
            val stock: Int = shopData.getInt(tradeIdx.toString() + ".stock")
            val maxStock: Int = shopData.getInt(tradeIdx.toString() + ".maxStock", -1)
            val price: Double = Calc.calcTotalCost(shopName, tradeIdx, if (sell) -amount else amount)
            var lore: String
            var priceText: String
            if (sell) {
                lore = l("TRADE_VIEW.SELL")
                priceText = t(player, "TRADE.SELL_PRICE").replace("{num}", n(price))
            } else {
                lore = l("TRADE_VIEW.BUY")
                priceText = t(player, "TRADE.PRICE").replace("{num}", n(price))
            }
            if (!sell) {
                if (stock != -1 && stock <= amount) // stock은 1이거나 그보다 작을 수 없음. 단 -1은 무한재고를 의미함.
                    continue
            }
            var stockText = ""
            if (!shopData.contains("Options.flag.hidestock")) {
                if (stock <= 0) {
                    stockText = t(player, "TRADE.INF_STOCK")
                } else if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack")) {
                    stockText = t(player, "TRADE.STACKS").replace("{num}", n(stock / 64))
                } else {
                    stockText = n(stock)
                }
            }
            var maxStockText: String?
            if (shopData.contains("Options.flag.showmaxstock") && maxStock != -1) {
                if (DynamicShop.plugin.getConfig().getBoolean("UI.DisplayStockAsStack")) {
                    maxStockText = t(player, "TRADE.STACKS").replace("{num}", n(maxStock / 64))
                } else {
                    maxStockText = n(maxStock)
                }
                stockText = t(player, "SHOP.STOCK_2").replace("{stock}", stockText).replace("{max_stock}", maxStockText)
            } else {
                stockText = t(player, "SHOP.STOCK").replace("{num}", stockText)
            }
            var deliveryChargeText = ""
            if (deliveryCharge > 0) {
                deliveryChargeText = if (sell && price < deliveryCharge) {
                    "§c" + ChatColor.stripColor(t(player, "MESSAGE.DELIVERY_CHARGE")).replace("{fee}", n(deliveryCharge))
                } else {
                    t(player, "MESSAGE.DELIVERY_CHARGE").replace("{fee}", n(deliveryCharge))
                }
            }
            var tradeLoreText: String = if (sell) t(player, "TRADE.CLICK_TO_SELL") else t(player, "TRADE.CLICK_TO_BUY")
            tradeLoreText = tradeLoreText.replace("{amount}", n(amount))
            lore = lore.replace("{\\nPrice}", if (priceText.isEmpty()) "" else """
     
     $priceText
     """.trimIndent())
            lore = lore.replace("{\\nStock}", if (stockText.isEmpty()) "" else """
     
     $stockText
     """.trimIndent())
            lore = lore.replace("{\\nDeliveryCharge}", if (deliveryChargeText.isEmpty()) "" else """
     
     $deliveryChargeText
     """.trimIndent())
            lore = lore.replace("{\\nTradeLore}", """
     
     $tradeLoreText
     """.trimIndent())
            lore = lore.replace("{Price}", priceText)
            lore = lore.replace("{Stock}", stockText)
            lore = lore.replace("{DeliveryCharge}", deliveryChargeText)
            lore = lore.replace("{TradeLore}", tradeLoreText)
            val temp: String = lore.replace(" ", "")
            if (ChatColor.stripColor(temp).startsWith("\n")) lore = lore.replaceFirst("\n", "")
            meta.setLore(ArrayList(Arrays.asList(lore.split("\n"))))
            itemStack.setItemMeta(meta)
            inventory.setItem(idx, itemStack)
            idx++
            if (itemStack.getMaxStackSize() <= 1) {
                amount++
            } else {
                amount = amount * 2
            }
        }
    }

    private fun Sell(options: ConfigurationSection, itemStack: ItemStack, deliveryCharge: Int, infiniteStock: Boolean) {
        val permission: String = options.getString("permission")
        if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission("$permission.sell")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"))
            return
        }

        // 상점이 매입을 거절.
        val stock: Int = shopData.getInt(tradeIdx.toString() + ".stock")
        val maxStock: Int = shopData.getInt(tradeIdx.toString() + ".maxStock", -1)
        if (maxStock != -1 && maxStock < stock + itemStack.getAmount()) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.PURCHASE_REJECTED"))
            return
        }
        if (options.contains("flag.jobpoint")) {
            Sell.sell(CURRENCY.JOB_POINT, player, shopName, tradeIdx, itemStack, -deliveryCharge, infiniteStock)
        } else {
            Sell.sell(CURRENCY.VAULT, player, shopName, tradeIdx, itemStack, -deliveryCharge, infiniteStock)
        }
    }

    private fun Buy(options: ConfigurationSection, itemStack: ItemStack, deliveryCharge: Int, infiniteStock: Boolean) {
        val permission: String = options.getString("permission")
        if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission("$permission.buy")) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"))
            return
        }
        if (options.contains("flag.jobpoint")) {
            Buy.buy(CURRENCY.JOB_POINT, player, shopName, tradeIdx, itemStack, deliveryCharge, infiniteStock)
        } else {
            Buy.buy(CURRENCY.VAULT, player, shopName, tradeIdx, itemStack, deliveryCharge, infiniteStock)
        }
    }

    @Override
    override fun RefreshUI() {
        if (!CheckShopIsEnable()) return
        for (i in 2..8) inventory.setItem(i, null)
        for (i in 11..17) inventory.setItem(i, null)
        CreateBalanceButton()
        CreateSellBuyOnlyToggle()
        CreateTradeButtons()
    }

    fun CheckShopIsEnable(): Boolean {
        if (!ShopUtil.shopConfigFiles.containsKey(shopName) || shopData == null || !shopData.contains(tradeIdx)
                || !shopData.getString(tradeIdx.toString() + ".mat").equals(material)) {
            val otherMeta: ItemMeta = shopData.get(tradeIdx.toString() + ".itemStack") as ItemMeta
            if (itemMeta == null || !itemMeta.equals(otherMeta)) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.INVALID_TRANSACTION"))
                player.closeInventory()
                return false
            }
        }
        val ret = DynaShopAPI.IsShopEnable(shopName) || player.hasPermission(P_ADMIN_SHOP_EDIT)
        if (!ret) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_IS_CLOSED_BY_ADMIN"))
            player.closeInventory()
        }
        return ret
    }

    companion object {
        fun CalcShipping(player: Player, shopName: String?): Int {
            var deliverycharge = 0
            val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
            val optionS: ConfigurationSection = data.get().getConfigurationSection("Options")
            if (optionS.contains("world") && optionS.contains("pos1") && optionS.contains("pos2") && optionS.contains("flag.deliverycharge")) {
                var sameworld = true
                var outside = false
                if (!player.getWorld().getName().equals(optionS.getString("world"))) sameworld = false
                val shopPos1: Array<String> = optionS.getString("pos1").split("_")
                val shopPos2: Array<String> = optionS.getString("pos2").split("_")
                val x1: Int = Integer.parseInt(shopPos1[0])
                val y1: Int = Integer.parseInt(shopPos1[1])
                val z1: Int = Integer.parseInt(shopPos1[2])
                val x2: Int = Integer.parseInt(shopPos2[0])
                val y2: Int = Integer.parseInt(shopPos2[1])
                val z2: Int = Integer.parseInt(shopPos2[2])
                if (((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2 || x2 <= player.getLocation().getBlockX()) && player.getLocation().getBlockX()) > x1) outside = true
                if (((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2 || y2 <= player.getLocation().getBlockY()) && player.getLocation().getBlockY()) > y1) outside = true
                if (!(z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2 || z2 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z1)) outside = true
                if (!sameworld) {
                    deliverycharge = -1
                } else if (outside) {
                    val lo = Location(player.getWorld(), x1, y1, z1)
                    val dist = (player.getLocation().distance(lo) * 0.1 * DynamicShop.plugin.getConfig().getDouble("Shop.DeliveryChargeScale")) as Int
                    deliverycharge = Clamp(dist, DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMin"), DynamicShop.plugin.getConfig().getInt("Shop.DeliveryChargeMax"))
                }
            }
            return deliverycharge
        }
    }
}