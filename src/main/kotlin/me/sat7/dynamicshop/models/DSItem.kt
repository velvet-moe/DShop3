package me.sat7.dynamicshop.models

import org.bukkit.inventory.ItemStack

@Getter
@Setter
class DSItem(itemStack: ItemStack?, buyValue: Double, sellValue: Double, minPrice: Double, maxPrice: Double, median: Int, stock: Int, maxStock: Int) {
    private val itemStack: ItemStack? = null
    private val buyValue = 0.0
    private val sellValue = 0.0
    private val minPrice = 0.0
    private val maxPrice = 0.0
    private val median = 0
    private val stock = 0
    private val maxStock = 0

    init {
        setItemStack(itemStack)
        setBuyValue(Math.round(buyValue * 100) / 100.0)
        setSellValue(Math.round(sellValue * 100) / 100.0)
        setMinPrice(Math.round(minPrice * 100) / 100.0)
        setMaxPrice(Math.round(maxPrice * 100) / 100.0)
        setMedian(median)
        setStock(stock)
        setMaxStock(maxStock)
    }
}