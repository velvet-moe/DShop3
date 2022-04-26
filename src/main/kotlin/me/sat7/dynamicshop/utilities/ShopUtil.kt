package me.sat7.dynamicshop.utilities

import java.io.File

object ShopUtil {
    val shopConfigFiles: HashMap<String, CustomConfig> = HashMap()
    fun Reload() {
        ReloadAllShop()
        ConvertOldShopData()
        SetupSampleShopFile()
        SortShopDataAll()
    }

    fun ReloadAllShop() {
        shopConfigFiles.clear()
        val listOfFiles: Array<File> = File(DynamicShop.plugin.getDataFolder() + "/Shop").listFiles()
        if (listOfFiles != null) {
            for (f in listOfFiles) {
                val shopCC = CustomConfig()
                val idx: Int = f.getName().lastIndexOf(".")
                val shopName: String = f.getName().substring(0, idx)
                shopCC.setup(shopName, "Shop")
                shopConfigFiles.put(shopName, shopCC)
            }
        }
    }

    // 상점에서 빈 슬롯 찾기
    fun findEmptyShopSlot(shopName: String?, startIdx: Int, addPage: Boolean): Int {
        var startIdx = startIdx
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return -1
        if (startIdx < 0) startIdx = 0
        var idx = startIdx
        while (data.get().contains(String.valueOf(idx))) idx++
        if (data.get().getInt("Options.page") < idx / 45 + 1) {
            if (addPage) {
                data.get().set("Options.page", idx / 45 + 1)
                data.save()
                return idx
            }
            return -1
        }
        return idx
    }

    // 상점에서 아이탬타입 찾기
    fun findItemFromShop(shopName: String?, item: ItemStack?): Int {
        if (item == null || item.getType().isAir()) return -1
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return -1
        for (s in data.get().getKeys(false)) {
            try {
                val i: Int = Integer.parseInt(s)
            } catch (e: Exception) {
                continue
            }
            if (!data.get().contains("$s.value")) continue  // 장식용임
            if (data.get().getString("$s.mat").equals(item.getType().toString())) {
                val metaStr: String = data.get().getString("$s.itemStack")
                if (metaStr == null && !item.hasItemMeta()) {
                    return Integer.parseInt(s)
                }
                if (metaStr != null && metaStr.equals(item.getItemMeta().toString())) {
                    return Integer.parseInt(s)
                }
            }
        }
        return -1
    }

    // 상점에 아이탬 추가
    fun addItemToShop(shopName: String?, idx: Int, item: ItemStack, buyValue: Double, sellValue: Double, minValue: Double, maxValue: Double, median: Int, stock: Int): Boolean {
        return addItemToShop(shopName, idx, item, buyValue, sellValue, minValue, maxValue, median, stock, -1)
    }

    fun addItemToShop(shopName: String?, idx: Int, item: ItemStack, buyValue: Double, sellValue: Double, minValue: Double, maxValue: Double, median: Int, stock: Int, maxStock: Int): Boolean {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return false
        return try {
            data.get().set("$idx.mat", item.getType().toString())
            if (item.hasItemMeta()) {
                data.get().set("$idx.itemStack", item.getItemMeta())
            } else {
                data.get().set("$idx.itemStack", null)
            }
            if (buyValue > 0) {
                data.get().set("$idx.value", buyValue)
                if (buyValue == sellValue) {
                    data.get().set("$idx.value2", null)
                } else {
                    data.get().set("$idx.value2", sellValue)
                }
                if (minValue > 0.01) {
                    data.get().set("$idx.valueMin", minValue)
                } else {
                    data.get().set("$idx.valueMin", null)
                }
                if (maxValue > 0.01) {
                    data.get().set("$idx.valueMax", maxValue)
                } else {
                    data.get().set("$idx.valueMax", null)
                }
                data.get().set("$idx.median", median)
                data.get().set("$idx.stock", stock)
                if (maxStock > 0) {
                    data.get().set("$idx.maxStock", maxStock)
                } else {
                    data.get().set("$idx.maxStock", null)
                }
            } else {
                // idx,null하면 안됨. 존재는 하되 하위 데이터만 없어야함.
                data.get().set("$idx.value", null)
                data.get().set("$idx.value2", null)
                data.get().set("$idx.valueMin", null)
                data.get().set("$idx.valueMax", null)
                data.get().set("$idx.median", null)
                data.get().set("$idx.stock", null)
                data.get().set("$idx.maxStock", null)
            }
            data.save()
            true
        } catch (e: Exception) {
            DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " ERR.AddItemToShop.")
            for (s in e.getStackTrace()) {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " " + s.toString())
            }
            false
        }
    }

    // 상점 아이탬의 value, median, stock을 수정
    fun editShopItem(shopName: String?, idx: Int, buyValue: Double, sellValue: Double, minValue: Double, maxValue: Double, median: Int, stock: Int) {
        editShopItem(shopName, idx, buyValue, sellValue, minValue, maxValue, median, stock, -1)
    }

    fun editShopItem(shopName: String?, idx: Int, buyValue: Double, sellValue: Double, minValue: Double, maxValue: Double, median: Int, stock: Int, maxStock: Int) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        data.get().set("$idx.value", buyValue)
        if (buyValue == sellValue) {
            data.get().set("$idx.value2", null)
        } else {
            data.get().set("$idx.value2", sellValue)
        }
        if (minValue > 0.01) {
            data.get().set("$idx.valueMin", minValue)
        } else {
            data.get().set("$idx.valueMin", null)
        }
        if (maxValue > 0.01) {
            data.get().set("$idx.valueMax", maxValue)
        } else {
            data.get().set("$idx.valueMax", null)
        }
        if (maxStock > 0) {
            data.get().set("$idx.maxStock", maxStock)
        } else {
            data.get().set("$idx.maxStock", null)
        }
        data.get().set("$idx.median", median)
        data.get().set("$idx.stock", stock)
        data.save()
    }

    // 상점에서 아이탬 제거
    fun removeItemFromShop(shopName: String?, idx: Int) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        data.get().set(String.valueOf(idx), null)
        data.save()
    }

    // 상점 페이지 삽입
    fun insetShopPage(shopName: String?, page: Int) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        data.get().set("Options.page", data.get().getInt("Options.page") + 1)
        for (i in (data.get().getInt("Options.page") * 45 downTo page - 1) * 45) {
            val temp: ConfigurationSection = data.get().getConfigurationSection(String.valueOf(i))
            data.get().set(String.valueOf(i + 45), temp)
            data.get().set(String.valueOf(i), null)
        }
        data.save()
        data.reload()
    }

    // 상점 페이지 삭제
    fun deleteShopPage(shopName: String?, page: Int) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        data.get().set("Options.page", data.get().getInt("Options.page") - 1)
        for (s in data.get().getKeys(false)) {
            try {
                val i: Int = Integer.parseInt(s)
                if (i >= (page - 1) * 45 && i < page * 45) {
                    data.get().set(s, null)
                } else if (i >= page * 45) {
                    val temp: ConfigurationSection = data.get().getConfigurationSection(s)
                    data.get().set(String.valueOf(i - 45), temp)
                    data.get().set(s, null)
                }
            } catch (ignored: Exception) {
            }
        }
        data.save()
        data.reload()
    }

    // 상점 이름 바꾸기
    fun renameShop(shopName: String?, newName: String?) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        data.rename(newName)
        data.get().set("Options.title", newName)
        shopConfigFiles.put(newName, data)
        shopConfigFiles.remove(shopName)
    }

    // 상점 병합
    fun mergeShop(shopA: String, shopB: String) {
        // A에 B의 아이템을 다 밀어넣는 방식임.
        // 상점잔액 합쳐주는것 말고는 별도의 처리 없음
        val dataA: CustomConfig = shopConfigFiles.get(shopA)
        val dataB: CustomConfig = shopConfigFiles.get(shopB)
        val pg1: Int = dataA.get().getInt("Options.page")
        val pg2: Int = dataB.get().getInt("Options.page")
        dataA.get().set("Options.page", pg1 + pg2)
        if (dataA.get().contains("Options.Balance") || dataB.get().contains("Options.Balance")) {
            var a = getShopBalance(shopA)
            if (a == -1.0) a = 0.0
            var b = 0.0
            if (!(dataA.get().getString("Options.Balance").equals(shopB) || dataB.get().getString("Options.Balance").equals(shopA))) {
                b = getShopBalance(shopB)
            }
            if (b == -1.0) b = 0.0
            if (a + b > 0) {
                dataA.get().set("Options.Balance", a + b)
            } else {
                dataA.get().set("Options.Balance", null)
            }
        }
        for (item in dataB.get().getKeys(false)) {
            try {
                dataA.get().set(String.valueOf(Integer.parseInt(item) + pg1 * 45), dataB.get().get(item))
            } catch (ignored: Exception) {
            }
        }
        dataB.delete()
        shopConfigFiles.remove(shopB)
        dataA.save()
        dataA.reload()
    }

    // 상점의 잔액 확인
    fun getShopBalance(shopName: String): Double {
        val data: CustomConfig = shopConfigFiles.get(shopName)
                ?: return (-1).toDouble()

        // 무한
        if (!data.get().contains("Options.Balance")) return (-1).toDouble()
        var shopBal: Double
        try {
            shopBal = Double.parseDouble(data.get().getString("Options.Balance")) // 파싱에 실패하면 캐치로 가는 방식.
        } // 연동형
        catch (ee: Exception) {
            val linkedShop: String = data.get().getString("Options.Balance")

            // 그런 상점이 없음.
            val linkedShopData: CustomConfig = shopConfigFiles.get(linkedShop)
            if (linkedShopData == null) {
                data.get().set("Options.Balance", null)
                data.save()
                DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + shopName).toString() + ", " + linkedShop + "/ target shop not found")
                DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + shopName).toString() + "/ balance has been reset")
                return (-1).toDouble()
            }

            // 연결 대상이 실제 계좌가 아님.
            try {
                if (linkedShopData.get().contains("Options.Balance")) {
                    val temp: Double = Double.parseDouble(linkedShopData.get().getString("Options.Balance"))
                } else {
                    return (-1).toDouble()
                }
            } catch (e: Exception) {
                DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX +
                        shopName).toString() + ", " + linkedShop + "/ " +
                        t(null, "ERR.SHOP_LINK_TARGET_ERR"))
                data.get().set("Options.Balance", null)
                data.save()
                DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + shopName).toString() + "/ balance has been reset")
                return (-1).toDouble()
            }
            shopBal = linkedShopData.get().getDouble("Options.Balance")
        }
        return shopBal
    }

    // 상점의 잔액 수정
    fun addShopBalance(shopName: String, amount: Double) {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return
        val old = getShopBalance(shopName)
        if (old < 0) return
        var newValue = old + amount
        newValue = Math.round(newValue * 100) / 100.0
        try {
            val temp: Double = Double.parseDouble(data.get().getString("Options.Balance"))
            data.get().set("Options.Balance", newValue)
        } // 연동형
        catch (ee: Exception) {
            val linkedShop: String = data.get().getString("Options.Balance")
            val linkedShopData: CustomConfig = shopConfigFiles.get(linkedShop)
            if (linkedShopData != null) linkedShopData.get().set("Options.Balance", newValue)
        }
    }

    // 2틱 후 인벤토리 닫기
    fun closeInventoryWithDelay(player: Player?) {
        //todo 왜 이렇게 만들었을까??? 2틱 딜레이가 필요한 이유가 뭐지?
        Bukkit.getScheduler().runTaskLater(DynamicShop.plugin, player::closeInventory, 2)
    }

    fun SetToRecommendedValueAll(shop: String?, sender: CommandSender?) {
        val data: CustomConfig = shopConfigFiles.get(shop) ?: return
        for (itemIndex in data.get().getKeys(false)) {
            try {
                val i: Int = Integer.parseInt(itemIndex) // options에 대해 적용하지 않기 위해.
                if (!data.get().contains("$itemIndex.value")) continue  // 장식용은 스킵
                var itemName: String = data.get().getString("$itemIndex.mat")
                var worth: Double = WorthUtil.ccWorth!!.get().getDouble(itemName)
                if (worth == 0.0) {
                    itemName = itemName.replace("-", "")
                    itemName = itemName.replace("_", "")
                    itemName = itemName.toLowerCase()
                    worth = WorthUtil.ccWorth!!.get().getDouble(itemName)
                }
                if (worth != 0.0) {
                    val numberOfPlayer: Int = DynamicShop.plugin.getConfig().getInt("Shop.NumberOfPlayer")
                    val sugMid = CalcRecommendedMedian(worth, numberOfPlayer)
                    editShopItem(shop, i, worth, worth, 0.01, -1.0, sugMid, sugMid)
                } else {
                    if (sender != null) sender.sendMessage((DynamicShop.dsPrefix(sender) + t(sender, "ERR.NO_RECOMMEND_DATA")).toString() + " : " + itemName)
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun CalcRecommendedMedian(worth: Double, numberOfPlayer: Int): Int {
        return (4 / Math.pow(worth, 0.35) * 1000 * numberOfPlayer)
    }

    fun FindTheBestShopToSell(player: Player?, itemStack: ItemStack): Array<String> {
        var topShopName = ""
        var bestPrice = -1.0
        var tradeIdx = -1

        // 접근가능한 상점중 최고가 찾기
        for (entry in shopConfigFiles.entrySet()) {
            val data: CustomConfig = entry.getValue()

            // 권한 없는 상점
            if (player != null) {
                val permission: String = data.get().getString("Options.permission")
                if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission("$permission.sell")) {
                    continue
                }
            }

            // 비활성화된 상점
            val enable: Boolean = data.get().getBoolean("Options.enable", true)
            if (!enable) continue

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            if (data.get().contains("Options.flag.localshop") || data.get().contains("Options.flag.signshop") || data.get().contains("Options.flag.jobpoint")) continue

            // 영업시간 확인
            if (player != null && !CheckShopHour(entry.getKey(), player)) continue
            val sameItemIdx = findItemFromShop(entry.getKey(), itemStack)
            if (sameItemIdx != -1) {
                val tradeType: String = data.get().getString("$sameItemIdx.tradeType")
                if (tradeType != null && tradeType.equalsIgnoreCase("BuyOnly")) continue  // 구매만 가능함

                // 상점에 돈이 없음
                if (getShopBalance(entry.getKey()) != -1.0 && getShopBalance(entry.getKey()) < Calc.calcTotalCost(entry.getKey(), String.valueOf(sameItemIdx), itemStack.getAmount())) {
                    continue
                }

                // 최대 재고를 넘겨서 매입 거절
                val maxStock: Int = data.get().getInt("$sameItemIdx.maxStock", -1)
                val stock: Int = data.get().getInt("$sameItemIdx.stock")
                if (maxStock != -1 && maxStock <= stock) continue
                val value: Double = Calc.getCurrentPrice(entry.getKey(), String.valueOf(sameItemIdx), false)
                if (bestPrice < value) {
                    topShopName = entry.getKey()
                    bestPrice = value
                    tradeIdx = sameItemIdx
                }
            }
        }
        return arrayOf(topShopName, Integer.toString(tradeIdx))
    }

    fun FindTheBestShopToBuy(player: Player, itemStack: ItemStack?): Array<String> {
        var topShopName = ""
        var bestPrice = Double.MAX_VALUE
        var tradeIdx = -1

        // 접근가능한 상점중 최저가 찾기
        for (entry in shopConfigFiles.entrySet()) {
            val data: CustomConfig = entry.getValue()

            // 권한 없는 상점
            val permission: String = data.get().getString("Options.permission")
            if (permission != null && permission.length() > 0 && !player.hasPermission(permission) && !player.hasPermission("$permission.buy")) {
                continue
            }

            // 비활성화된 상점
            val enable: Boolean = data.get().getBoolean("Options.enable", true)
            if (!enable) continue

            // 표지판 전용 상점, 지역상점, 잡포인트 상점
            if (data.get().contains("Options.flag.localshop") || data.get().contains("Options.flag.signshop") || data.get().contains("Options.flag.jobpoint")) continue

            // 영업시간 확인
            if (!CheckShopHour(entry.getKey(), player)) continue
            val sameItemIdx = findItemFromShop(entry.getKey(), itemStack)
            if (sameItemIdx != -1) {
                val tradeType: String = data.get().getString("$sameItemIdx.tradeType")
                if (tradeType != null && tradeType.equalsIgnoreCase("SellOnly")) continue

                // 재고가 없음
                val stock: Int = data.get().getInt("$sameItemIdx.stock")
                if (stock != -1 && stock < 2) continue
                val value: Double = Calc.getCurrentPrice(entry.getKey(), String.valueOf(sameItemIdx), true)
                if (bestPrice > value) {
                    topShopName = entry.getKey()
                    bestPrice = value
                    tradeIdx = sameItemIdx
                }
            }
        }
        return arrayOf(topShopName, Integer.toString(tradeIdx))
    }

    fun GetShopMaxPage(shopName: String?): Int {
        val data: CustomConfig = shopConfigFiles.get(shopName) ?: return 0
        return data.get().getConfigurationSection("Options").getInt("page")
    }

    private var randomStockTimer = 1
    fun randomChange(generator: Random?) {
        val legacyStabilizer: Boolean = DynamicShop.plugin.getConfig().getBoolean("Shop.UseLegacyStockStabilization")
        for (entry in shopConfigFiles.entrySet()) {
            var somethingIsChanged = false
            val data: CustomConfig = entry.getValue()

            // 인게임 30분마다 실행됨 (500틱)
            randomStockTimer += 1
            if (randomStockTimer >= Integer.MAX_VALUE) {
                randomStockTimer = 0
            }
            //DynamicShop.console.sendMessage("debug... " + randomStockTimer);

            // fluctuation
            val confSec: ConfigurationSection = data.get().getConfigurationSection("Options.fluctuation")
            if (confSec != null) {
                var interval: Int = confSec.getInt("interval")
                if (interval < 1 || interval > 999) {
                    DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + entry.getKey()).toString() + ".Options.fluctuation.interval")
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 48")
                    confSec.set("interval", 48)
                    interval = 48
                }
                if (randomStockTimer % interval != 0) continue
                for (item in data.get().getKeys(false)) {
                    try {
                        val i: Int = Integer.parseInt(item) // options에 대해 적용하지 않기 위해.
                        if (!data.get().contains("$item.value")) continue  // 장식용은 스킵
                        var stock: Int = data.get().getInt("$item.stock")
                        if (stock <= 1) continue  // 무한재고에 대해서는 스킵
                        val median: Int = data.get().getInt("$item.median")
                        if (median <= 1) continue  // 고정가 상품에 대해서는 스킵
                        stock = RandomStockFluctuation(generator, stock, median, confSec.getDouble("strength"))
                        data.get().set("$item.stock", stock)
                        somethingIsChanged = true
                    } catch (ignored: Exception) {
                    }
                }
            }

            // stock stabilizing
            val confSec2: ConfigurationSection = data.get().getConfigurationSection("Options.stockStabilizing")
            if (confSec2 != null) {
                var interval: Int = confSec2.getInt("interval")
                if (interval < 1 || interval > 999) {
                    DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + " Wrong value at " + entry.getKey()).toString() + ".Options.stockStabilizing.interval")
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Reset to 48")
                    confSec2.set("interval", 48)
                    interval = 48
                }
                if (randomStockTimer % interval != 0) continue
                for (item in data.get().getKeys(false)) {
                    try {
                        val i: Int = Integer.parseInt(item) // options에 대해 적용하지 않기 위해.
                        if (!data.get().contains("$item.value")) continue  // 장식용은 스킵
                        var stock: Int = data.get().getInt("$item.stock")
                        if (stock < 1) continue  // 무한재고에 대해서는 스킵
                        val median: Int = data.get().getInt("$item.median")
                        if (median < 1) continue  // 고정가 상품에 대해서는 스킵
                        if (stock == median) continue  // 이미 같으면 스킵
                        stock = StockStabilizing(legacyStabilizer, generator, stock, median, confSec2.getDouble("strength"))
                        data.get().set("$item.stock", stock)
                        somethingIsChanged = true
                    } catch (ignored: Exception) {
                    }
                }
            }
            if (somethingIsChanged) data.save()
        }
    }

    fun SetupSampleShopFile() {
        if (shopConfigFiles.isEmpty()) {
            val data = CustomConfig()
            data.setup("SampleShop", "Shop")
            data.get().options().header("Shop name can not contain formatting codes, '/' and ' '")
            data.get().options().copyHeader(true)
            data.get().set("Options.page", 2)
            data.get().set("Options.title", "Sample Shop")
            data.get().set("Options.lore", "This is sample shop")
            data.get().set("Options.permission", "")
            data.get().set("0.mat", "DIRT")
            data.get().set("0.value", 1)
            data.get().set("0.median", 10000)
            data.get().set("0.stock", 10000)
            data.get().set("1.mat", "COBBLESTONE")
            data.get().set("1.value", 1.5)
            data.get().set("1.median", 10000)
            data.get().set("1.stock", 10000)
            shopConfigFiles.put("SampleShop", data)
            data.get().options().copyDefaults(true)
            data.save()
        }
    }

    // Shop.yml 한덩어리로 되있는 데이터를 새 버전 방식으로 변환함
    fun ConvertOldShopData() {
        val file = File(DynamicShop.plugin.getDataFolder(), "Shop.yml")
        if (file.exists()) {
            val oldShopData = CustomConfig()
            oldShopData.setup("Shop", null)
            for (oldShopName in oldShopData.get().getKeys(false)) {
                val oldData: ConfigurationSection = oldShopData.get().getConfigurationSection(oldShopName)
                val data = CustomConfig()
                data.setup(oldShopName, "Shop")
                for (s in oldData.getKeys(false)) {
                    data.get().set(s, oldData.get(s))
                }
                if (data.get().contains("Options.hideStock")) {
                    data.get().set("Options.flag.hidestock", "")
                    data.get().set("Options.hideStock", null)
                }
                if (data.get().contains("Options.hidePricingType")) {
                    data.get().set("Options.flag.hidepricingtype", "")
                    data.get().set("Options.hidePricingType", null)
                }
                if (!data.get().contains("Options.lore")) {
                    data.get().set("Options.lore", "")
                }
                data.save()
                shopConfigFiles.put(oldShopName, data)
            }
            file.delete()
        }
    }

    fun SortShopDataAll() {
        for (s in shopConfigFiles.keySet()) {
            SortShopData(s)
        }
    }

    // yml 파일 안의 거래 인덱스들을 정렬해서 다시 작성함
    fun SortShopData(shopName: String?) {
        val data: CustomConfig = shopConfigFiles.get(shopName)
        val sortData: HashMap<Integer, Object> = HashMap()
        for (s in data.get().getKeys(false)) {
            try {
                val dummy: Int = Integer.parseInt(s) // 아이템 데이터가 아닌걸 건너뛰기 위함
                sortData.put(dummy, data.get().get(s))
                data.get().set(s, null)
            } catch (ignore: Exception) {
            }
        }
        val keys: Array<Object> = sortData.keySet().toArray()
        Arrays.sort(keys)
        for (o in keys) {
            data.get().set(o.toString(), sortData.get(o))
        }
        data.save()
    }

    fun RandomStockFluctuation(generator: Random, stock: Int, median: Int, strength: Double): Int {
        var stock = stock
        var down: Boolean = generator.nextBoolean()
        val rate = stock / median.toDouble()
        if (rate < 0.5 && generator.nextBoolean()) down = false else if (rate > 2 && generator.nextBoolean()) down = true
        var amount = (median * (strength / 100.0) * generator.nextFloat()) as Int
        if (down) amount *= -1
        stock += amount
        if (stock < 2) stock = 2
        return stock
    }

    fun StockStabilizing(isLegacyMode: Boolean, generator: Random, stock: Int, median: Int, strength: Double): Int {
        var stock = stock
        if (isLegacyMode) {
            val amount = (median * (strength / 100.0)).toInt()
            if (stock < median) {
                stock += amount
                if (stock > median) stock = median
            } else {
                stock -= amount
                if (stock < median) stock = median
            }
        } else {
            var amount = ((median - stock) * (strength / 100.0)).toInt()
            if (amount == 0) {
                if (generator.nextInt() % 2 === 0) {
                    amount = if (stock > median) -1 else 1
                }
            }
            stock += amount
        }
        return stock
    }

    fun CheckShopHour(shopName: String?, player: Player): Boolean {
        val shopData: CustomConfig = shopConfigFiles.get(shopName) ?: return true
        val shopConf: ConfigurationSection = shopData.get().getConfigurationSection("Options") ?: return true
        return if (shopConf.contains("shophours")) {
            var curTimeHour = player.getWorld().getTime() as Int / 1000 + 6
            if (curTimeHour > 24) curTimeHour -= 24
            val temp: Array<String> = shopConf.getString("shophours").split("~")
            val open: Int = Integer.parseInt(temp[0])
            val close: Int = Integer.parseInt(temp[1])
            if (close > open) {
                open <= curTimeHour && curTimeHour < close
            } else {
                open <= curTimeHour || curTimeHour < close
            }
        } else {
            true
        }
    }
}