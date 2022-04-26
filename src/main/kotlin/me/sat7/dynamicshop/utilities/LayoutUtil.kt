package me.sat7.dynamicshop.utilities

import me.sat7.dynamicshop.files.CustomConfig

object LayoutUtil {
    var ccLayout: CustomConfig? = null
    fun Setup() {
        ccLayout!!.setup("Layout", null)
        ccLayout!!.get().options().header(
                """
                    {Tag} : A predefined placeholder. Available for each item only.
                    {\nTag} : Line breaks when this item is displayed.
                    SHOP.INFO: §f{ShopLore}{\nPermission}{\nTax}{\nShopBalance}{\nShopHour}{\nShopPosition}
                    SHOP.ITEM_INFO: §f{Sell}{\nBuy}{\nStock}{\nPricingType}{\nItemMetaLore}{\nTradeLore}
                    TRADE_VIEW.BUY: §f{Price}{\nStock}{\nDeliveryCharge}{\nTradeLore}
                    TRADE_VIEW.SELL: §f{Price}{\nStock}{\nDeliveryCharge}{\nTradeLore}
                    TRADE_VIEW.BALANCE: §f{PlayerBalance}{\nShopBalance}
                    """.trimIndent()
        )
        ccLayout!!.get().addDefault("SHOP.INFO", "§f{ShopLore}{\\nPermission}{\\nTax}{\\nShopBalance}{\\nShopHour}{\\nShopPosition}")
        ccLayout!!.get().addDefault("SHOP.ITEM_INFO", "§f{Sell}{\\nBuy}{\\nStock}{\\nPricingType}\n{\\nItemMetaLore}{\\nTradeLore}")
        ccLayout!!.get().addDefault("TRADE_VIEW.BUY", "§f{Price}{\\nStock}{\\nDeliveryCharge}\n{\\nTradeLore}")
        ccLayout!!.get().addDefault("TRADE_VIEW.SELL", "§f{Price}{\\nStock}{\\nDeliveryCharge}\n{\\nTradeLore}")
        ccLayout!!.get().addDefault("TRADE_VIEW.BALANCE", "§f{PlayerBalance}{\\nShopBalance}")
        ccLayout!!.get().options().copyDefaults(true)
        ccLayout!!.save()
    }

    fun l(key: String?): String {
        return ccLayout!!.get().getString(key)
    }
}