package me.sat7.dynamicshop.guis

import java.util.ArrayList

class ShopSettings : InGameUI() {
    private val ENABLE_TOGGLE = 0
    private val PERMISSION = 1
    private val MAX_PAGE = 2
    private val SHOP_HOUR = 6
    private val SHOP_HOUR_OPEN = 7
    private val SHOP_HOUR_CLOSE = 8
    private val FLUC = 15
    private val FLUC_INTERVAL = 16
    private val FLUC_STRENGTH = 17
    private val STABLE = 24
    private val STABLE_INTERVAL = 25
    private val STABLE_STRENGTH = 26
    private val FLAG1 = 9
    private val FLAG2 = 10
    private val FLAG3 = 11
    private val FLAG4 = 12
    private val FLAG5 = 18
    private val FLAG6 = 19
    private val FLAG7 = 20
    private val FLAG8 = 21
    private val FLAG9 = 22
    private val FLAG10 = 13
    private val FLAG11 = 27
    private val TAX_TOGGLE = 33
    private val TAX_AMOUNT = 34
    private val LOG_TOGGLE = 42
    private val LOG_DELETE = 43
    private val CLOSE = 36
    private var shopName: String? = null

    init {
        uiType = UI_TYPE.ShopSettings
    }

    fun getGui(player: Player, shopName: String): Inventory {
        this.shopName = shopName
        inventory = Bukkit.createInventory(player, 45, t(player, "SHOP_SETTING_TITLE") + "§7 | §8" + shopName)
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val confSec_Options: ConfigurationSection = data.get().getConfigurationSection("Options")

        // 닫기 버튼
        CreateCloseButton(player, CLOSE)

        // 활성화 버튼
        val isShopEnable: Boolean = confSec_Options.getBoolean("enable", true)
        val enableToggleMat: Material = if (isShopEnable) Material.GREEN_STAINED_GLASS else Material.RED_STAINED_GLASS
        val current: String = if (isShopEnable) t(player, "SHOP_SETTING.STATE_ENABLE") else t(player, "SHOP_SETTING.STATE_DISABLE")
        val set: String = if (isShopEnable) t(player, "SHOP_SETTING.STATE_DISABLE") else t(player, "SHOP_SETTING.STATE_ENABLE")
        var enableToggleLore = "§9" + t(player, "CUR_STATE") + ": " + current
        enableToggleLore += """
               
               §e${t(player, "CLICK")}: $set
               """.trimIndent()
        CreateButton(ENABLE_TOGGLE, enableToggleMat, t(player, "SHOP_SETTING.STATE"), enableToggleLore)

        // 권한 버튼
        var permStr: String? = confSec_Options.getString("permission")
        var permNew = "dshop.user.shop.$shopName"
        val permIcon: Material
        if (permStr == null || permStr.isEmpty()) {
            permStr = t(player, "NULL(OPEN)")
            permIcon = Material.IRON_BLOCK
        } else {
            permNew = t(player, "NULL(OPEN)")
            permIcon = Material.GOLD_BLOCK
        }
        val permLore: ArrayList<String> = ArrayList()
        permLore.add("§9" + t(player, "CUR_STATE") + ": " + permStr)
        if (!permStr.equalsIgnoreCase(t(player, "NULL(OPEN)"))) {
            permLore.add("§9 - $permStr.buy")
            permLore.add("§9 - $permStr.sell")
        }
        permLore.add("§e" + t(player, "CLICK") + ": " + permNew)
        CreateButton(PERMISSION, permIcon, t(player, "SHOP_SETTING.PERMISSION"), permLore)

        //최대 페이지 버튼
        CreateButton(MAX_PAGE, InGameUI.GetPageButtonIconMat(), t(player, "SHOP_SETTING.MAX_PAGE"), ArrayList(Arrays.asList(t(player, "SHOP_SETTING.MAX_PAGE_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), data.get().getInt("Options.page"))

        // 영업시간 버튼
        var curTime = player.getWorld().getTime() as Int / 1000 + 6
        if (curTime > 24) curTime -= 24
        if (data.get().contains("Options.shophours")) {
            val temp: Array<String> = data.get().getString("Options.shophours").split("~")
            val open: Int = Integer.parseInt(temp[0])
            val close: Int = Integer.parseInt(temp[1])
            val shopHourLore: ArrayList<String> = ArrayList(Arrays.asList(
                    t(player, "TIME.CUR").replace("{time}", curTime.toString() + ""),
                    "§9" + t(player, "CUR_STATE") + ": ",
                    "§9 - " + t(player, "TIME.OPEN") + ": " + open,
                    "§9 - " + t(player, "TIME.CLOSE") + ": " + close,
                    "§e" + t(player, "CLICK") + ": " + t(player, "TIME.OPEN24")))
            CreateButton(SHOP_HOUR, Material.CLOCK, t(player, "TIME.SHOPHOURS"), shopHourLore)
            CreateButton(SHOP_HOUR_OPEN, Material.CLOCK, "§f" + t(player, "TIME.OPEN"), ArrayList(Arrays.asList(t(player, "TIME.OPEN_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), open)
            CreateButton(SHOP_HOUR_CLOSE, Material.CLOCK, "§f" + t(player, "TIME.CLOSE"), ArrayList(Arrays.asList(t(player, "TIME.CLOSE_LORE"), t(player, "SHOP_SETTING.L_R_SHIFT"))), close)
        } else {
            val shopHourLore: ArrayList<String> = ArrayList(Arrays.asList(
                    t(player, "TIME.CUR").replace("{time}", curTime.toString() + ""),
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "TIME.OPEN24"),
                    "§e" + t(player, "CLICK") + ": " + t(player, "TIME.SET_SHOPHOURS")))
            CreateButton(SHOP_HOUR, Material.CLOCK, t(player, "TIME.SHOPHOURS"), shopHourLore)
        }

        // 랜덤스톡 버튼
        val flucConf: ConfigurationSection = data.get().getConfigurationSection("Options.fluctuation")
        if (flucConf != null) {
            val fluctuationLore: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "ON"),
                    "§e" + t(player, "LMB") + ": " + t(player, "OFF")
            ))
            CreateButton(FLUC, Material.COMPARATOR, t(player, "FLUCTUATION.FLUCTUATION"), fluctuationLore)
            val fluctuation_interval_lore: ArrayList<String> = ArrayList(Arrays.asList(
                    t(player, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": " + flucConf.getInt("interval") / 2.0 + "h",
                    "§e" + t(player, "CLICK") + ": " + t(player, "SHOP_SETTING.L_R_SHIFT")))
            CreateButton(FLUC_INTERVAL, Material.COMPARATOR, t(player, "FLUCTUATION.INTERVAL"), fluctuation_interval_lore, Clamp(flucConf.getInt("interval") / 2, 1, 64))
            val fluctuation_strength_lore: ArrayList<String> = ArrayList(Arrays.asList(
                    t(player, "FLUCTUATION.STRENGTH_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": ~" + flucConf.get("strength") + "%",
                    "§e" + t(player, "CLICK") + ": " + t(player, "STOCK_STABILIZING.L_R_SHIFT")))
            CreateButton(FLUC_STRENGTH, Material.COMPARATOR, t(player, "FLUCTUATION.STRENGTH"), fluctuation_strength_lore, Clamp((flucConf.getDouble("strength") * 10) as Int, 1, 64))
        } else {
            val flucToggleBtn: ItemStack = ItemsUtil.createItemStack(Material.COMPARATOR, null,
                    t(player, "FLUCTUATION.FLUCTUATION"),
                    ArrayList(Arrays.asList(
                            "§9" + t(player, "CUR_STATE") + ": " + t(player, "OFF"),
                            "§e" + t(player, "LMB") + ": " + t(player, "ON")
                    )),
                    1)
            inventory.setItem(FLUC, flucToggleBtn)
        }

        // 재고 안정화 버튼
        val stockStableConf: ConfigurationSection = data.get().getConfigurationSection("Options.stockStabilizing")
        if (stockStableConf != null) {
            val stableLore: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "ON"),
                    "§e" + t(player, "LMB") + ": " + t(player, "OFF")
            ))
            CreateButton(STABLE, Material.COMPARATOR, t(player, "STOCK_STABILIZING.SS"), stableLore)
            val stable_interval_Lore: ArrayList<String> = ArrayList(Arrays.asList(
                    t(player, "FLUCTUATION.INTERVAL_LORE"),
                    "§9" + t(player, "CUR_STATE") + ": " + stockStableConf.getInt("interval") / 2.0 + "h",
                    "§e" + t(player, "CLICK") + ": " + t(player, "SHOP_SETTING.L_R_SHIFT")))
            CreateButton(STABLE_INTERVAL, Material.COMPARATOR, t(player, "FLUCTUATION.INTERVAL"), stable_interval_Lore, Clamp(stockStableConf.getInt("interval") / 2, 1, 64))
            val stable_strength_Lore: ArrayList<String> = ArrayList(Arrays.asList(
                    if (DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization")) t(player, "STOCK_STABILIZING.STRENGTH_LORE_A") else t(player, "STOCK_STABILIZING.STRENGTH_LORE_B"),
                    "§9" + t(player, "CUR_STATE") + ": ~" + stockStableConf.get("strength") + "%",
                    "§e" + t(player, "CLICK") + ": " + t(player, "STOCK_STABILIZING.L_R_SHIFT")))
            CreateButton(STABLE_STRENGTH, Material.COMPARATOR, t(player, "FLUCTUATION.STRENGTH"), stable_strength_Lore, Clamp((stockStableConf.getDouble("strength") * 10) as Int, 1, 64))
        } else {
            val stableLore: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "OFF"),
                    "§e" + t(player, "LMB") + ": " + t(player, "ON")
            ))
            CreateButton(STABLE, Material.COMPARATOR, t(player, "STOCK_STABILIZING.SS"), stableLore)
        }

        // 세금
        val globalTax: Int = ConfigUtil.getCurrentTax()
        if (data.get().contains("Options.SalesTax")) {
            val taxLore: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + t(player, "TAX.USE_LOCAL"),
                    "§e" + t(player, "CLICK") + ": " +
                            t(player, "TAX.USE_GLOBAL").replace("{tax}", globalTax.toString() + "")))
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore)
            val taxLore2: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " + data.get().getInt("Options.SalesTax") + "%",
                    t(player, "SHOP_SETTING.L_R_SHIFT")))
            CreateButton(TAX_AMOUNT, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore2, Clamp(data.get().getInt("Options.SalesTax"), 1, 64))
        } else {
            val taxLore: ArrayList<String> = ArrayList(Arrays.asList(
                    "§9" + t(player, "CUR_STATE") + ": " +
                            t(player, "TAX.USE_GLOBAL").replace("{tax}", globalTax.toString() + ""),
                    "§e" + t(player, "CLICK") + ": " + t(player, "TAX.USE_LOCAL")))
            CreateButton(TAX_TOGGLE, Material.IRON_INGOT, t(player, "TAX.SALES_TAX"), taxLore)
        }

        // 플래그 버튼들
        CreateFlagButton(FLAG1, confSec_Options.contains("flag.signshop"), "signShop", t(player, "SHOP_SETTING.SIGN_SHOP_LORE"))
        CreateFlagButton(FLAG2, confSec_Options.contains("flag.localshop"), "localShop", t(player, "SHOP_SETTING.LOCAL_SHOP_LORE"))
        CreateFlagButton(FLAG3, confSec_Options.contains("flag.deliverycharge"), "deliveryCharge", t(player, "SHOP_SETTING.DELIVERY_CHARGE_LORE"))
        CreateFlagButton(FLAG4, confSec_Options.contains("flag.jobpoint"), "jobPoint", t(player, "SHOP_SETTING.JOB_POINT_LORE"))
        CreateFlagButton(FLAG5, confSec_Options.contains("flag.showvaluechange"), "showValueChange", t(player, "SHOP_SETTING.SHOW_VALUE_CHANGE_LORE"))
        CreateFlagButton(FLAG6, confSec_Options.contains("flag.hidestock"), "hideStock", t(player, "SHOP_SETTING.HIDE_STOCK"))
        CreateFlagButton(FLAG7, confSec_Options.contains("flag.hidepricingtype"), "hidePricingType", t(player, "SHOP_SETTING.HIDE_PRICING_TYPE"))
        CreateFlagButton(FLAG8, confSec_Options.contains("flag.hideshopbalance"), "hideShopBalance", t(player, "SHOP_SETTING.HIDE_SHOP_BALANCE"))
        CreateFlagButton(FLAG9, confSec_Options.contains("flag.showmaxstock"), "showMaxStock", t(player, "SHOP_SETTING.SHOW_MAX_STOCK"))
        CreateFlagButton(FLAG10, confSec_Options.contains("flag.hiddenincommand"), "hiddenInCommand", t(player, "SHOP_SETTING.HIDDEN_IN_COMMAND"))
        CreateFlagButton(FLAG11, confSec_Options.contains("flag.integeronly"), "integerOnly", t(player, "SHOP_SETTING.INTEGER_ONLY"))

        // 로그 버튼
        val log_cur: String
        val log_set: String
        if (confSec_Options.contains("log")) {
            log_cur = t(player, "ON")
            log_set = t(player, "OFF")
        } else {
            log_cur = t(player, "OFF")
            log_set = t(player, "ON")
        }
        val logLore: ArrayList<String> = ArrayList()
        logLore.add("§9" + t(player, "CUR_STATE") + ": " + log_cur)
        logLore.add("§e" + t(player, "LMB") + ": " + log_set)
        CreateButton(LOG_TOGGLE, Material.BOOK, t(player, "LOG.LOG"), logLore)
        CreateButton(LOG_DELETE, Material.RED_STAINED_GLASS_PANE, t(player, "LOG.DELETE"), "")
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        val player: Player = e.getWhoClicked() as Player
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)

        // 닫기버튼
        if (e.getSlot() === CLOSE) {
            DynaShopAPI.openShopGui(player, shopName, 1)
        }
        // 활성화
        if (e.getSlot() === ENABLE_TOGGLE) {
            if (data.get().getBoolean("Options.enable", true)) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName enable false")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName enable true")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === PERMISSION) {
            if (data.get().getString("Options.permission").isEmpty()) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName permission true")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName permission false")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === MAX_PAGE) {
            val oldvalue: Int = data.get().getInt("Options.page")
            var targetValue: Int
            if (e.isRightClick()) {
                targetValue = oldvalue + 1
                if (e.isShiftClick()) targetValue += 4
                if (targetValue >= 20) targetValue = 20
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName maxpage $targetValue")
            } else {
                targetValue = oldvalue - 1
                if (e.isShiftClick()) targetValue -= 4
                if (targetValue <= 1) targetValue = 1
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName maxpage $targetValue")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === SHOP_HOUR || e.getSlot() === SHOP_HOUR_OPEN || e.getSlot() === SHOP_HOUR_CLOSE) {
            if (data.get().contains("Options.shophours")) {
                val shopHour: Array<String> = data.get().getString("Options.shophours").split("~")
                var open: Integer = Integer.parseInt(shopHour[0])
                var close: Int = Integer.parseInt(shopHour[1])
                var edit = -1
                if (e.isRightClick()) edit = 1
                if (e.isShiftClick()) edit *= 5
                if (e.getSlot() === SHOP_HOUR) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName shophours 0 0")
                } else if (e.getSlot() === SHOP_HOUR_OPEN) {
                    open += edit
                    if (open.equals(close)) {
                        if (e.isRightClick()) {
                            open += 1
                        } else {
                            open -= 1
                        }
                    }
                    open = Clamp(open, 1, 24)
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName shophours $open $close")
                } else if (e.getSlot() === SHOP_HOUR_CLOSE) {
                    close += edit
                    if (open.equals(close)) {
                        if (e.isRightClick()) {
                            close += 1
                        } else {
                            close -= 1
                        }
                    }
                    close = Clamp(close, 1, 24)
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName shophours $open $close")
                }
                DynaShopAPI.openShopSettingGui(player, shopName)
            } else {
                if (e.getSlot() === SHOP_HOUR) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName shophours 20 6")
                    DynaShopAPI.openShopSettingGui(player, shopName)
                }
            }
        } else if (e.getSlot() === FLUC || e.getSlot() === FLUC_INTERVAL || e.getSlot() === FLUC_STRENGTH) {
            if (data.get().contains("Options.fluctuation")) {
                var interval: Int = data.get().getInt("Options.fluctuation.interval")
                var strength: Double = data.get().getDouble("Options.fluctuation.strength")
                if (e.getSlot() === FLUC) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName fluctuation off")
                } else if (e.getSlot() === FLUC_INTERVAL) {
                    var edit = -1
                    if (e.isRightClick()) edit = 1
                    if (e.isShiftClick()) edit *= 5
                    interval += edit
                    interval = Clamp(interval, 1, 999)
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName fluctuation $interval $strength")
                } else if (e.getSlot() === FLUC_STRENGTH) {
                    var edit = -0.1
                    if (e.isRightClick()) edit = 0.1
                    if (e.isShiftClick()) edit *= 5.0
                    strength += edit
                    strength = Clamp(strength, 0.1, 64)
                    strength = Math.round(strength * 100) / 100.0
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName fluctuation $interval $strength")
                }
                DynaShopAPI.openShopSettingGui(player, shopName)
            } else {
                if (e.getSlot() === FLUC) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName fluctuation 48 0.1")
                    DynaShopAPI.openShopSettingGui(player, shopName)
                }
            }
        } else if (e.getSlot() === STABLE || e.getSlot() === STABLE_INTERVAL || e.getSlot() === STABLE_STRENGTH) {
            if (data.get().contains("Options.stockStabilizing")) {
                var interval: Int = data.get().getInt("Options.stockStabilizing.interval")
                var strength: Double = data.get().getDouble("Options.stockStabilizing.strength")
                if (e.getSlot() === STABLE) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName stockStabilizing off")
                } else if (e.getSlot() === STABLE_INTERVAL) {
                    var edit = -1
                    if (e.isRightClick()) edit = 1
                    if (e.isShiftClick()) edit *= 5
                    interval += edit
                    interval = Clamp(interval, 1, 999)
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName stockStabilizing $interval $strength")
                } else if (e.getSlot() === STABLE_STRENGTH) {
                    var edit = -0.1
                    if (e.isRightClick()) edit = 0.1
                    if (e.isShiftClick()) edit *= 5.0
                    strength += edit
                    strength = Clamp(strength, 0.1, 25)
                    strength = Math.round(strength * 100) / 100.0
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName stockStabilizing $interval $strength")
                }
                DynaShopAPI.openShopSettingGui(player, shopName)
            } else {
                if (e.getSlot() === STABLE) {
                    Bukkit.dispatchCommand(player, "DynamicShop shop $shopName stockStabilizing 48 0.5")
                    DynaShopAPI.openShopSettingGui(player, shopName)
                }
            }
        } else if (e.getSlot() === TAX_TOGGLE || e.getSlot() === TAX_AMOUNT) {
            // 전역,지역 토글
            if (e.getSlot() === TAX_TOGGLE) {
                if (data.get().contains("Options.SalesTax")) {
                    data.get().set("Options.SalesTax", null)
                } else {
                    data.get().set("Options.SalesTax", DynamicShop.plugin.getConfig().getInt("Shop.SalesTax"))
                }
                DynaShopAPI.openShopSettingGui(player, shopName)
            } else if (data.get().contains("Options.SalesTax")) {
                var edit = -1
                if (e.isRightClick()) edit = 1
                if (e.isShiftClick()) edit *= 5
                val result: Int = Clamp(data.get().getInt("Options.SalesTax") + edit, 0, 99)
                data.get().set("Options.SalesTax", result)
                DynaShopAPI.openShopSettingGui(player, shopName)
            }
            data.save()
        } else if (e.getSlot() === FLAG1) {
            if (data.get().contains("Options.flag.signshop")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag signShop unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag signShop set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG2) {
            if (data.get().contains("Options.flag.localshop")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag localShop unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag localShop set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG3) {
            if (data.get().contains("Options.flag.deliverycharge")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag deliveryCharge unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag deliveryCharge set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG4) {
            if (data.get().contains("Options.flag.jobpoint")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag jobPoint unset")
            } else {
                if (!JobsHook.jobsRebornActive) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.JOBS_REBORN_NOT_FOUND"))
                    return
                }
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag jobPoint set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG5) {
            if (data.get().contains("Options.flag.showvaluechange")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag showValueChange unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag showValueChange set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG6) {
            if (data.get().contains("Options.flag.hidestock")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hideStock unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hideStock set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG7) {
            if (data.get().contains("Options.flag.hidepricingtype")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hidePricingType unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hidePricingType set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG8) {
            if (data.get().contains("Options.flag.hideshopbalance")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hideShopBalance unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hideShopBalance set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG9) {
            if (data.get().contains("Options.flag.showmaxstock")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag showMaxStock unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag showMaxStock set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG10) {
            if (data.get().contains("Options.flag.hiddenincommand")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hiddenInCommand unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag hiddenInCommand set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === FLAG11) {
            if (data.get().contains("Options.flag.integeronly")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag integerOnly unset")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName flag integerOnly set")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === LOG_TOGGLE) {
            if (data.get().contains("Options.log") && data.get().getBoolean("Options.log")) {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName log disable")
            } else {
                Bukkit.dispatchCommand(player, "DynamicShop shop $shopName log enable")
            }
            DynaShopAPI.openShopSettingGui(player, shopName)
        } else if (e.getSlot() === LOG_DELETE) {
            Bukkit.dispatchCommand(player, "DynamicShop shop $shopName log clear")
            DynaShopAPI.openShopSettingGui(player, shopName)
        }
    }

    private fun CreateFlagButton(buttonPosition: Int, isEnable: Boolean, title: String, lore: String) {
        val current: String
        val set: String
        val icon: Material
        if (isEnable) {
            icon = Material.GREEN_STAINED_GLASS_PANE
            current = t(null, "SET")
            set = t(null, "UNSET")
        } else {
            icon = Material.BLACK_STAINED_GLASS_PANE
            current = t(null, "UNSET")
            set = t(null, "SET")
        }
        val loreArray: ArrayList<String> = ArrayList()
        if (lore.contains("\n")) {
            val temp: Array<String> = lore.split("\n")
            Collections.addAll(loreArray, temp)
        } else {
            loreArray.add(lore)
        }
        loreArray.add("§9" + t(null, "CUR_STATE") + ": " + current)
        loreArray.add("§e" + t(null, "CLICK") + ": " + set)
        CreateButton(buttonPosition, icon, t(null, "SHOP_SETTING.FLAG") + ": " + title, loreArray)
    }
}