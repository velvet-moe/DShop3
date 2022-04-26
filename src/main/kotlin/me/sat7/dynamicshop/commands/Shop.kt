package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.files.CustomConfig

object Shop {
    fun GetShopName(args: Array<String>): String {
        return if (args.size == 1) {
            DynamicShop.plugin.getConfig().getString("Command.DefaultShopName")
        } else if (args.size > 1) {
            args[1]
        } else {
            ""
        }
    }

    fun shopCommand(args: Array<String>, sender: CommandSender?) {
        var player: Player? = null
        if (sender is Player) player = sender as Player?
        if (player != null && args.size == 1 && DynamicShop.plugin.getConfig().getBoolean("Command.OpenStartPageInsteadOfDefaultShop")) {
            DynaShopAPI.openStartPage(player)
            return
        }
        val shopName = GetShopName(args)

        // 그런 이름을 가진 상점이 있는지 확인
        if (player != null && !ShopUtil.shopConfigFiles.containsKey(shopName)) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.SHOP_NOT_FOUND"))
            return
        }
        val shopData: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)

        // 상점 UI 열기
        if (player != null && args.size <= 2) {
            //권한 확인
            val s: String = shopData.get().getString("Options.permission")
            if (s != null && s.length() > 0) {
                if (!player.hasPermission(s) && !player.hasPermission("$s.buy") && !player.hasPermission("$s.sell")) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_PERMISSION"))
                    return
                }
            }

            // 플래그 확인
            val shopConf: ConfigurationSection = shopData.get().getConfigurationSection("Options")
            if (shopConf.contains("flag.signshop")) {
                if (!player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS)) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.SIGN_SHOP_REMOTE_ACCESS"))
                    return
                }
            }
            if (shopConf.contains("flag.localshop") && !shopConf.contains("flag.deliverycharge") && shopConf.contains("world") && shopConf.contains("pos1") && shopConf.contains("pos2")) {
                var outside: Boolean = !player.getWorld().getName().equals(shopConf.getString("world"))
                val shopPos1: Array<String> = shopConf.getString("pos1").split("_")
                val shopPos2: Array<String> = shopConf.getString("pos2").split("_")
                val x1: Int = Integer.parseInt(shopPos1[0])
                val y1: Int = Integer.parseInt(shopPos1[1])
                val z1: Int = Integer.parseInt(shopPos1[2])
                val x2: Int = Integer.parseInt(shopPos2[0])
                val y2: Int = Integer.parseInt(shopPos2[1])
                val z2: Int = Integer.parseInt(shopPos2[2])
                if (((x1 <= player.getLocation().getBlockX() && player.getLocation().getBlockX() <= x2 || x2 <= player.getLocation().getBlockX()) && player.getLocation().getBlockX()) > x1) outside = true
                if (((y1 <= player.getLocation().getBlockY() && player.getLocation().getBlockY() <= y2 || y2 <= player.getLocation().getBlockY()) && player.getLocation().getBlockY()) > y1) outside = true
                if (((z1 <= player.getLocation().getBlockZ() && player.getLocation().getBlockZ() <= z2 || z2 <= player.getLocation().getBlockZ()) && player.getLocation().getBlockZ()) > z1) outside = true
                if (outside && !player.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS)) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.LOCAL_SHOP_REMOTE_ACCESS"))
                    var posString: String = t(player, "SHOP.SHOP_LOCATION")
                    posString = posString.replace("{x}", n(x1))
                    posString = posString.replace("{y}", n(y1))
                    posString = posString.replace("{z}", n(z1))
                    player.sendMessage(DynamicShop.dsPrefix(player) + posString)
                    return
                }
            }
            if (shopConf.contains("shophours") && !player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                var curTime = player.getWorld().getTime() as Int / 1000 + 6
                if (curTime > 24) curTime -= 24
                if (!ShopUtil.CheckShopHour(shopName, player)) {
                    val temp: Array<String> = shopConf.getString("shophours").split("~")
                    val open: Int = Integer.parseInt(temp[0])
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "TIME.SHOP_IS_CLOSED").replace("{time}", open.toString() + "").replace("{curTime}", curTime.toString() + ""))
                    return
                }
            }
            DynaShopAPI.openShopGui(player, shopName, 1)
        } else if (args.size >= 3) {
            CMDManager.RunCMD(args[2].toLowerCase(), args, sender)
        }
    }
}