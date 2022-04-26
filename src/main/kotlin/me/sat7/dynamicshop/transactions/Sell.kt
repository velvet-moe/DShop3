package me.sat7.dynamicshop.transactions

import java.util.HashMap

object Sell {
    fun quickSellItem(player: Player?, tempIS: ItemStack, shopName: String?, tradeIdx: Int, isShiftClick: Boolean, slot: Int): Double {
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val priceSellOld: Double = DynaShopAPI.getSellPrice(shopName, tempIS)
        val priceBuyOld: Double = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true)
        val stockOld: Int = data.get().getInt("$tradeIdx.stock")
        val maxStock: Int = data.get().getInt("$tradeIdx.maxStock", -1)
        val priceSum: Double

        // 실제 판매 가능량 확인
        val tradeAmount: Int
        if (player != null) {
            if (isShiftClick) {
                var amount = 0
                for (item in player.getInventory().getStorageContents()) {
                    if (item == null) continue
                    if (item.isSimilar(tempIS)) {
                        if (maxStock == -1) {
                            amount += item.getAmount()
                            player.getInventory().removeItem(item)
                        } else {
                            val tempAmount: Int = Clamp(tempIS.getAmount(), 0, maxStock - stockOld)
                            val itemLeft: Int = item.getAmount() - tempAmount
                            if (itemLeft <= 0) {
                                player.getInventory().removeItem(item)
                            } else {
                                item.setAmount(itemLeft)
                            }
                            amount += tempAmount
                        }
                    }
                    if (maxStock != -1 && amount + stockOld <= maxStock) break
                }
                tradeAmount = amount
            } else {
                if (maxStock == -1) {
                    tradeAmount = player.getInventory().getItem(slot).getAmount()
                    player.getInventory().setItem(slot, null)
                } else {
                    tradeAmount = Clamp(tempIS.getAmount(), 0, maxStock - stockOld)
                    val itemAmountOld: Int = player.getInventory().getItem(slot).getAmount()
                    val itemLeft = itemAmountOld - tradeAmount
                    if (itemLeft <= 0) player.getInventory().setItem(slot, null) else player.getInventory().getItem(slot).setAmount(itemLeft)
                }
            }
            player.updateInventory()
        } else {
            tradeAmount = tempIS.getAmount()
        }

        // 판매할 아이탬이 없음
        if (tradeAmount == 0) {
            if (player != null) player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"))
            return 0
        }
        priceSum = Calc.calcTotalCost(shopName, String.valueOf(tradeIdx), -tradeAmount)

        // 재고 증가
        if (stockOld > 0) {
            data.get().set("$tradeIdx.stock", MathUtil.SafeAdd(stockOld, tradeAmount))
        }

        // 실제 거래부----------
        val econ: Economy = DynamicShop.getEconomy()
        var r: EconomyResponse? = null
        if (player != null) r = DynamicShop.getEconomy().depositPlayer(player, priceSum)
        if (player == null || r.transactionSuccess()) {
            data.save()

            //로그 기록
            LogUtil.addLog(shopName, tempIS.getType().toString(), -tradeAmount, priceSum, "vault", if (player != null) player.getName() else shopName)
            if (player != null) {
                val useLocalizedName: Boolean = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName")
                var message: String = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS", !useLocalizedName)
                        .replace("{amount}", Integer.toString(tradeAmount))
                        .replace("{price}", n(r.amount))
                        .replace("{bal}", n(econ.getBalance(player)))
                if (useLocalizedName) {
                    message = message.replace("{item}", "<item>")
                    LangUtil.sendMessageWithLocalizedItemName(player, message, tempIS.getType())
                } else {
                    message = message.replace("{item}", ItemsUtil.getBeautifiedName(tempIS.getType()))
                    player.sendMessage(message)
                }
                player.playSound(player.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1, 1)
            }
            if (data.get().contains("Options.Balance")) {
                ShopUtil.addShopBalance(shopName, priceSum * -1)
            }
            if (player != null) {
                val event = ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true),
                        priceSellOld,
                        DynaShopAPI.getSellPrice(shopName, tempIS),
                        stockOld,
                        DynaShopAPI.getStock(shopName, tempIS),
                        DynaShopAPI.getMedian(shopName, tempIS),
                        shopName, tempIS, player)
                Bukkit.getPluginManager().callEvent(event)
            }
        } else {
            player.sendMessage(String.format("[Vault] An error occured: %s", r.errorMessage))
        }
        return priceSum
    }

    fun sell(currency: ItemTrade.CURRENCY, player: Player, shopName: String?, tradeIdx: String, tempIS: ItemStack, priceSum: Double, infiniteStock: Boolean) {
        var priceSum = priceSum
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val priceSellOld: Double = DynaShopAPI.getSellPrice(shopName, tempIS)
        val priceBuyOld: Double = Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true)
        val stockOld: Int = data.get().getInt("$tradeIdx.stock")
        // 상점에 돈이 없음
        if (ShopUtil.getShopBalance(shopName) !== -1 && ShopUtil.getShopBalance(shopName) < Calc.calcTotalCost(shopName, tradeIdx, tempIS.getAmount())) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SHOP_BAL_LOW"))
            return
        }

        // 실제 판매 가능량 확인
        var actualAmount: Int = tempIS.getAmount()
        val hashMap: HashMap<Integer, ItemStack> = player.getInventory().removeItem(tempIS)
        player.updateInventory()
        if (!hashMap.isEmpty()) {
            actualAmount -= hashMap.get(0).getAmount()
        }

        // 판매할 아이탬이 없음
        if (actualAmount == 0) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.NO_ITEM_TO_SELL"))
            return
        }
        priceSum += Calc.calcTotalCost(shopName, tradeIdx, -actualAmount)

        // 재고 증가
        if (!infiniteStock) {
            data.get().set("$tradeIdx.stock", MathUtil.SafeAdd(stockOld, actualAmount))
        }
        var econ: Economy? = null
        var r: EconomyResponse? = null
        if (currency === ItemTrade.CURRENCY.VAULT) {
            econ = DynamicShop.getEconomy()
            r = DynamicShop.getEconomy().depositPlayer(player, priceSum)
            if (!r.transactionSuccess()) return
        } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
            if (!JobsHook.addJobsPoint(player, priceSum)) return
        }

        //로그 기록
        val currencyString = if (currency === ItemTrade.CURRENCY.VAULT) "vault" else "jobpoint"
        LogUtil.addLog(shopName, tempIS.getType().toString(), -actualAmount, priceSum, currencyString, player.getName())
        val useLocalizedName: Boolean = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName")
        var message = ""
        if (currency === ItemTrade.CURRENCY.VAULT) {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS", !useLocalizedName)
                    .replace("{amount}", Integer.toString(actualAmount))
                    .replace("{price}", n(r.amount))
                    .replace("{bal}", n(econ.getBalance(player)))
        } else if (currency === ItemTrade.CURRENCY.JOB_POINT) {
            message = DynamicShop.dsPrefix(player) + t(player, "MESSAGE.SELL_SUCCESS_JP", !useLocalizedName)
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
        SoundUtil.playerSoundEffect(player, "sell")
        if (data.get().contains("Options.Balance")) {
            ShopUtil.addShopBalance(shopName, priceSum * -1)
        }
        data.save()
        DynaShopAPI.openItemTradeGui(player, shopName, tradeIdx)
        val event = ShopBuySellEvent(false, priceBuyOld, Calc.getCurrentPrice(shopName, String.valueOf(tradeIdx), true), priceSellOld, DynaShopAPI.getSellPrice(shopName, tempIS), stockOld, DynaShopAPI.getStock(shopName, tempIS), DynaShopAPI.getMedian(shopName, tempIS), shopName, tempIS, player)
        Bukkit.getPluginManager().callEvent(event)
    }
}