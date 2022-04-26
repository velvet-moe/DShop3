package me.sat7.dynamicshop.events

import lombok.Getter

/**
 * This event will fire every time a buy or sell is made in the DynamicShop plugin.
 */
class ShopBuySellEvent(@field:Getter private val buy: Boolean, @field:Getter private val oldBuyPrice: Double, @field:Getter private val newBuyPrice: Double, @field:Getter private val oldSellPrice: Double, @field:Getter private val newSellPrice: Double, @field:Getter private val oldStock: Int, @field:Getter private val newStock: Int, @field:Getter private val median: Int, @field:Getter private val shopName: String, itemStack: ItemStack, p: Player?) : PlayerEvent(p) {
    @Getter
    private val itemStack: ItemStack

    @Getter
    private val jobPoint: Boolean

    init {
        this.itemStack = itemStack
        jobPoint = DynaShopAPI.isJobsPointShop(shopName)
    }

    @Override
    override fun toString(): String {
        return "ShopBuySellEvent{" +
                "buy=" + buy +
                ", oldBuyPrice=" + oldBuyPrice +
                ", newBuyPrice=" + newBuyPrice +
                ", oldSellPrice=" + oldSellPrice +
                ", newSellPrice=" + newSellPrice +
                ", oldStock=" + oldStock +
                ", newStock=" + newStock +
                ", median=" + median +
                ", shopName='" + shopName + '\'' +
                ", itemStack=" + itemStack +
                ", jobPoint=" + jobPoint +
                ", player=" + player.toString() +
                '}'
    }

    @get:Override
    val handlers: HandlerList
        get() = HANDLERS

    companion object {
        private val HANDLERS: HandlerList = HandlerList()
        val handlerList: HandlerList
            get() = HANDLERS
    }
}