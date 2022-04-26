package me.sat7.dynamicshop.commands.shop

import me.sat7.dynamicshop.DynamicShop

class EditAll : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_SHOP_EDIT
        validArgCount.add(6)
    }

    @Override
    fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "editall"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds shop <shopname> editall <purchaseValue | salesValue | valueMin | valueMax | median | stock | max stock> <= | + | - | * | /> <amount>")
        player.sendMessage(" - " + t(player, "HELP.EDIT_ALL"))
        player.sendMessage(" - " + t(player, "HELP.EDIT_ALL_2"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        val shopName: String = Shop.GetShopName(args)
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        val mod: String
        var value = 0f
        var dataType: String
        try {
            dataType = args[3]
            if (!dataType.equals("stock") && !dataType.equals("median") && !dataType.equals("purchaseValue") && !dataType.equals("salesValue") && !dataType.equals("valueMin") && !dataType.equals("valueMax") && !dataType.equals("maxStock")) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
                return
            }
            if (dataType.equals("purchaseValue")) dataType = "value"
            if (dataType.equals("salesValue")) dataType = "value2"
            mod = args[4]
            if (!mod.equals("=") &&
                    !mod.equals("+") && !mod.equals("-") &&
                    !mod.equals("*") && !mod.equals("/")) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
                return
            }
            if (!args[5].equals("stock") && !args[5].equals("median") && !args[5].equals("purchaseValue") && !args[5].equals("salesValue") && !args[5].equals("valueMin") && !args[5].equals("valueMax") && !args[5].equals("maxStock")) value = Float.parseFloat(args[5])
        } catch (e: Exception) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_DATATYPE"))
            return
        }

        // 수정
        for (s in shopData.get().getKeys(false)) {
            try {
                @SuppressWarnings("unused") val i: Int = Integer.parseInt(s) // 의도적으로 넣은 코드임. 숫자가 아니면 건너뛰기 위함.
                if (!shopData.get().contains("$s.value")) continue  //장식용임
            } catch (e: Exception) {
                continue
            }
            when (args[5]) {
                "stock" -> value = shopData.get().getInt("$s.stock")
                "median" -> value = shopData.get().getInt("$s.median")
                "purchaseValue" -> value = shopData.get().getInt("$s.value")
                "salesValue" -> value = shopData.get().getInt("$s.value2")
                "valueMin" -> value = shopData.get().getInt("$s.valueMin")
                "valueMax" -> value = shopData.get().getInt("$s.valueMax")
                "maxStock" -> value = shopData.get().getInt("$s.maxStock")
            }
            if (mod.equalsIgnoreCase("=")) {
                shopData.get().set("$s.$dataType", value.toInt())
            } else if (mod.equalsIgnoreCase("+")) {
                shopData.get().set("$s.$dataType", (shopData.get().getInt("$s.$dataType") + value) as Int)
            } else if (mod.equalsIgnoreCase("-")) {
                shopData.get().set("$s.$dataType", (shopData.get().getInt("$s.$dataType") - value) as Int)
            } else if (mod.equalsIgnoreCase("/")) {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock")) {
                    shopData.get().set("$s.$dataType", (shopData.get().getInt("$s.$dataType") / value) as Int)
                } else {
                    shopData.get().set("$s.$dataType", shopData.get().getDouble("$s.$dataType") / value)
                }
            } else if (mod.equalsIgnoreCase("*")) {
                if (args[5].equals("stock") || args[5].equals("median") || args[5].equals("maxStock")) {
                    shopData.get().set("$s.$dataType", (shopData.get().getInt("$s.$dataType") * value) as Int)
                } else {
                    shopData.get().set("$s.$dataType", shopData.get().getDouble("$s.$dataType") * value)
                }
            }
            if (shopData.get().getDouble("$s.valueMin") < 0) {
                shopData.get().set("$s.valueMin", null)
            }
            if (shopData.get().getDouble("$s.valueMax") < 0) {
                shopData.get().set("$s.valueMax", null)
            }
            if (shopData.get().getDouble("$s.maxStock") < 1) {
                shopData.get().set("$s.maxStock", null)
            }
        }
        shopData.save()
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.ITEM_UPDATED"))
    }
}