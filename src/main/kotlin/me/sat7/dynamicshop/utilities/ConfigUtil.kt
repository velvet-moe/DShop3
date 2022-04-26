package me.sat7.dynamicshop.utilities

import lombok.Getter

object ConfigUtil {
    @Getter
    @Setter
    private var currentTax = 0
    const val configVersion = 3
    private val dataKeys = arrayOf(
            "Language",
            "Prefix",
            "Command.UseShopCommand",
            "Command.OpenStartPageInsteadOfDefaultShop",
            "Command.DefaultShopName",
            "Command.PermissionCheckWhenCreatingAShopList",
            "Shop.SalesTax",
            "Shop.DeliveryChargeScale",
            "Shop.DeliveryChargeMin",
            "Shop.DeliveryChargeMax",
            "Shop.NumberOfPlayer",
            "Shop.UseLegacyStockStabilization",
            "UI.DisplayStockAsStack",
            "UI.OpenStartPageWhenClickCloseButton",
            "UI.CloseButtonIcon",
            "UI.PageButtonIcon",
            "UI.ShopInfoButtonIcon",
            "UI.IntFormat",
            "UI.DoubleFormat",
            "UI.LocalizedItemName",
            "UI.UsePlaceholderAPI",
            "UI.UseHexColorCode",
            "UI.EnableInventoryClickSearch.StartPage",
            "UI.EnableInventoryClickSearch.Shop",
            "Log.SaveLogs",
            "Log.LogFileNameFormat",
            "Log.CullLogs",
            "Log.LogCullAgeMinutes",
            "Log.LogCullTimeMinutes"
    )

    fun configSetup(dynamicShop: DynamicShop) {
        val config: FileConfiguration = dynamicShop.getConfig()
        config.options().header(
                """
                    Language: ex) en-US,ko-KR
                    UseShopCommand: Set this to false if you want to disable '/shop'
                    DeliveryChargeScale: This is only used for shop with the 'delivery charge' flag. 0.01 ~
                    NumberOfPlayer: This is used to calculate the recommended median. 3~100
                    UseLegacyStockStabilization: false = Changed by n% of the gap with median. true = Changed by n% of median.
                    DisplayStockAsStack: ex) true: 10Stacks, false: 640
                    Version: Do NOT edit this
                    """.trimIndent()
        )
        config.options().copyHeader(true)

        //기본값이 저장되어야 하는데 saveDefaultConfig() 가 작동 안해서 이렇게 처리함.
        for (s in dataKeys) {
            config.set(s, config.get(s))
        }
        DynamicShop.dsPrefix_ = config.getString("Prefix")
        val salesTax: Double = Clamp(config.getDouble("Shop.SalesTax"), 0, 99)
        config.set("Shop.SalesTax", salesTax)
        setCurrentTax(salesTax.toInt())
        var DeliveryChargeScale: Double = config.getDouble("Shop.DeliveryChargeScale")
        if (DeliveryChargeScale < 0.01) DeliveryChargeScale = 0.01
        config.set("Shop.DeliveryChargeScale", DeliveryChargeScale)
        var DeliveryChargeMin: Int = config.getInt("Shop.DeliveryChargeMin")
        if (DeliveryChargeMin < 1) DeliveryChargeMin = 1
        config.set("Shop.DeliveryChargeMin", DeliveryChargeMin)
        var DeliveryChargeMax: Int = config.getInt("Shop.DeliveryChargeMax")
        if (DeliveryChargeMax < 1) DeliveryChargeMax = 1
        if (DeliveryChargeMax < DeliveryChargeMin) DeliveryChargeMax = DeliveryChargeMin
        config.set("Shop.DeliveryChargeMax", DeliveryChargeMax)
        val numPlayer: Int = Clamp(config.getInt("Shop.NumberOfPlayer"), 3, 100)
        config.set("Shop.NumberOfPlayer", numPlayer)
        ConvertV2toV3()
        dynamicShop.saveConfig()
    }

    fun resetTax() {
        currentTax = DynamicShop.plugin.getConfig().getInt("Shop.SalesTax")
    }

    private fun ConvertV2toV3() {
        val config: FileConfiguration = DynamicShop.plugin.getConfig()
        if (config.get("ShowTax") != null) {
            config.set("ShowTax", null)
        }
        if (config.get("UseShopCommand") != null) {
            config.set("Command.UseShopCommand", config.get("UseShopCommand"))
            config.set("UseShopCommand", null)
        }
        if (config.get("OpenStartPageInsteadOfDefaultShop") != null) {
            config.set("Command.OpenStartPageInsteadOfDefaultShop", config.get("OpenStartPageInsteadOfDefaultShop"))
            config.set("OpenStartPageInsteadOfDefaultShop", null)
        }
        if (config.get("DefaultShopName") != null) {
            config.set("Command.DefaultShopName", config.get("DefaultShopName"))
            config.set("DefaultShopName", null)
        }
        if (config.get("SalesTax") != null) {
            config.set("Shop.SalesTax", config.get("SalesTax"))
            config.set("SalesTax", null)
        }
        if (config.get("DeliveryChargeScale") != null) {
            config.set("Shop.DeliveryChargeScale", config.get("DeliveryChargeScale"))
            config.set("DeliveryChargeScale", null)
        }
        if (config.get("NumberOfPlayer") != null) {
            config.set("Shop.NumberOfPlayer", config.get("NumberOfPlayer"))
            config.set("NumberOfPlayer", null)
        }
        if (config.get("DisplayStockAsStack") != null) {
            config.set("UI.DisplayStockAsStack", config.get("DisplayStockAsStack"))
            config.set("DisplayStockAsStack", null)
        }
        if (config.get("OnClickCloseButton_OpenStartPage") != null) {
            config.set("UI.OpenStartPageWhenClickCloseButton", config.get("OnClickCloseButton_OpenStartPage"))
            config.set("OnClickCloseButton_OpenStartPage", null)
        }
        if (config.get("ShopInfoButtonIcon") != null) {
            config.set("UI.ShopInfoButtonIcon", config.get("ShopInfoButtonIcon"))
            config.set("ShopInfoButtonIcon", null)
        }
        if (config.get("SaveLogs") != null) {
            config.set("Log.SaveLogs", config.get("SaveLogs"))
            config.set("SaveLogs", null)
        }
        if (config.get("CullLogs") != null) {
            config.set("Log.CullLogs", config.get("CullLogs"))
            config.set("CullLogs", null)
        }
        if (config.get("LogCullAgeMinutes") != null) {
            config.set("Log.LogCullAgeMinutes", config.get("LogCullAgeMinutes"))
            config.set("LogCullAgeMinutes", null)
        }
        if (config.get("LogCullTimeMinutes") != null) {
            config.set("Log.LogCullTimeMinutes", config.get("LogCullTimeMinutes"))
            config.set("LogCullTimeMinutes", null)
        }
    }
}