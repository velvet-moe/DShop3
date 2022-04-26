package me.sat7.dynamicshop.transactions

import java.util.HashMap

object Buy {
    fun buy(currency: ItemTrade.CURRENCY, player: Player, shopName: String?, tradeIdx: String, tempIS: ItemStack, priceSum: Double, infiniteStock: Boolean) {
        var priceSum = priceSum
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        var econ: Economy? = null
        if (currency === ItemTrade.CURRENCY.VAULT) {
            econ = DynamicShop.getEconomy()
        }
        var actualAmount = 0
        val stockOld: Int = data.get().getInt("$tradeIdx.stock")
        val priceBuyOld: Double = Calc.getCurrentPrice(shopName, tradeIdx, true)
        val priceSellOld: Double = DynaShopAPI.getSellPrice(shopName, tempIS)
        for (i in 0 until tempIS.getAmount()) {
            if (!infiniteStock && stockOld <= actualAmount + 1) {
                break
            }
            val price: Double = Calc.getCurrentPrice(shopName, tradeIdx, true, true)
            if (currency === ItemTrade.CURRENCY.VAULT) {
                if (priceSum + price > econ.getBalance(player)) break
            } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
                if (priceSum + price > JobsHook.getCurJobPoints(player)) break
            }
            priceSum += price
            if (!infiniteStock) {
                data.get().set("$tradeIdx.stock", data.get().getInt("$tradeIdx.stock") - 1)
            }
            actualAmount++
        }

        // 실 구매 가능량이 0이다 = 돈이 없다.
        if (actualAmount <= 0) {
            var message = ""
            if (currency === ItemTrade.CURRENCY.VAULT) {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(econ.getBalance(player)))
            } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
                message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_POINT").replace("{bal}", n(JobsHook.getCurJobPoints(player)))
            }
            player.sendMessage(message)
            data.get().set("$tradeIdx.stock", stockOld)
            return
        }

        // 상점 재고 부족
        if (!infiniteStock && stockOld <= actualAmount) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.OUT_OF_STOCK"))
            data.get().set("$tradeIdx.stock", stockOld)
            return
        }
        if (data.get().contains("Options.flag.integeronly")) {
            priceSum = Math.ceil(priceSum)
        }
        var r: EconomyResponse? = null
        if (currency === ItemTrade.CURRENCY.VAULT) {
            if (econ.getBalance(player) < priceSum) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NOT_ENOUGH_MONEY").replace("{bal}", n(econ.getBalance(player))))
                return
            }
            r = DynamicShop.getEconomy().withdrawPlayer(player, priceSum)
            if (!r.transactionSuccess()) {
                player.sendMessage(String.format("An error occured: %s", r.errorMessage))
                return
            }
        } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
            if (JobsHook.getCurJobPoints(player) < priceSum) return
            if (!JobsHook.addJobsPoint(player, priceSum * -1)) return
        }
        var leftAmount = actualAmount
        while (leftAmount > 0) {
            var giveAmount: Int = tempIS.getType().getMaxStackSize()
            if (giveAmount > leftAmount) giveAmount = leftAmount
            val iStack = ItemStack(tempIS.getType(), giveAmount)
            iStack.setItemMeta(data.get().get("$tradeIdx.itemStack") as ItemMeta)
            val leftOver: HashMap<Integer, ItemStack> = player.getInventory().addItem(iStack)
            if (leftOver.size() !== 0) {
                player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.INVENTORY_FULL"))
                val loc: Location = player.getLocation()
                val leftStack = ItemStack(tempIS.getType(), leftOver.get(0).getAmount())
                leftStack.setItemMeta(data.get().get("$tradeIdx.itemStack") as ItemMeta)
                player.getWorld().dropItem(loc, leftStack)
            }
            leftAmount -= giveAmount
        }

        //로그 기록
        val currencyString = if (currency === ItemTrade.CURRENCY.VAULT) "vault" else "jobpoint"
        LogUtil.addLog(shopName, tempIS.getType().toString(), actualAmount, priceSum, currencyString, player.getName())
        var message = ""
        val useLocalizedName: Boolean = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName")
        if (currency === ItemTrade.CURRENCY.VAULT) {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance(player)))
        } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.BUY_SUCCESS_JP", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(priceSum))
                    .replace("{bal}", n(JobsHook.getCurJobPoints(player)))
        }
        if (useLocalizedName) {
            message = message.replace("{item}", "<item>")
            LangUtil.sendMessageWithLocalizedItemName(player, message, tempIS.getType())
        } else {
            message = message.replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
            player.sendMessage(message)
        }
        SoundUtil.playerSoundEffect(player, "buy")
        if (data.get().contains("Options.Balance")) {
            ShopUtil.addShopBalance(shopName, priceSum)
        }
        data.save()
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx)
        val event = ShopBuySellEvent(true, priceBuyOld, Calc.getCurrentPrice(shopName, tradeIdx, true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player)
        Bukkit.getPluginManager().callEvent(event)
    }
}