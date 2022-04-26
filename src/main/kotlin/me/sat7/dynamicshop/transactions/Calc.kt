package me.sat7.dynamicshop.transactions

import me.sat7.dynamicshop.files.CustomConfig

object Calc {
    // 특정 아이탬의 현재 가치를 계산 (다이나믹 or 고정가) (세금 반영)
    fun getCurrentPrice(shopName: String?, idx: String, buy: Boolean): Double {
        return getCurrentPrice(shopName, idx, buy, false)
    }

    fun getCurrentPrice(shopName: String?, idx: String, buy: Boolean, raw: Boolean): Double {
        val data: FileConfiguration = ShopUtil.shopConfigFiles.get(shopName).get()
        val value: Double
        value = if (!buy && data.contains("$idx.value2")) {
            data.getDouble("$idx.value2")
        } else {
            data.getDouble("$idx.value")
        }
        val min: Double = data.getDouble("$idx.valueMin", 0.01)
        val max: Double = data.getDouble("$idx.valueMax")
        val median: Int = data.getInt("$idx.median")
        var stock: Int = data.getInt("$idx.stock")
        var price: Double
        if (median <= 0 || stock <= 0) {
            price = value
        } else {
            if (!buy && stock < Integer.MAX_VALUE) stock = stock + 1
            price = median * value / stock
        }
        if (price < min) {
            price = min
        }
        if (max != 0.0 && price > max) {
            price = max
        }

        // 판매세 계산 (임의 지정된 판매가치가 없는 경우에만)
        if (!buy && !data.contains("$idx.value2")) {
            val tax = price / 100 * getTaxRate(shopName)
            price -= tax
        }
        return if (!raw && data.contains("Options.flag.integeronly")) {
            if (buy) Math.ceil(price) else Math.floor(price)
        } else {
            price
        }
    }

    // 특정 아이탬의 앞으로 n개의 가치합을 계산 (다이나믹 or 고정가) (세금 반영)
    fun calcTotalCost(shopName: String?, idx: String, amount: Int): Double {
        val data: FileConfiguration = ShopUtil.shopConfigFiles.get(shopName).get()
        var total = 0.0
        val median: Int = data.getInt("$idx.median")
        var stock: Int = data.getInt("$idx.stock")
        val value: Double
        value = if (amount < 0 && data.contains("$idx.value2")) {
            data.getDouble("$idx.value2")
        } else {
            data.getDouble("$idx.value")
        }
        if (median <= 0 || stock <= 0) {
            total = value * Math.abs(amount)
        } else {
            for (i in 0 until Math.abs(amount)) {
                if (amount < 0 && stock < Integer.MAX_VALUE) {
                    stock++
                }
                var temp = median * value / stock
                val min: Double = data.getDouble("$idx.valueMin", 0.01)
                val max: Double = data.getDouble("$idx.valueMax")
                if (temp < min) {
                    temp = min
                }
                if (max != 0.0 && temp > max) {
                    temp = max
                }
                total += temp
                if (amount > 0) {
                    stock--
                    if (stock < 2) {
                        break
                    }
                }
            }
        }

        // 세금 적용 (판매가 별도지정시 세금계산 안함)
        if (amount < 0 && !data.contains("$idx.value2")) {
            val tax = total / 100 * getTaxRate(shopName)
            total -= tax
        }
        return if (data.contains("Options.flag.integeronly")) {
            if (amount > 0) Math.ceil(total) else Math.floor(total)
        } else {
            Math.round(total * 100) / 100.0
        }
    }

    // 상점의 세율 반환
    fun getTaxRate(shopName: String?): Int {
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        return if (data.get().contains("Options.SalesTax")) {
            data.get().getInt("Options.SalesTax")
        } else {
            ConfigUtil.getCurrentTax()
        }
    }
}