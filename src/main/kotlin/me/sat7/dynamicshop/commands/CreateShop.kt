package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.files.CustomConfig

class CreateShop : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_CREATE_SHOP
        validArgCount.add(2)
        validArgCount.add(3)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "createshop"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds create <shopname> [<permission>]")
        player.sendMessage(" - " + t(player, "HELP.CREATE_SHOP_2"))
        player.sendMessage("")
    }

    @Override
    fun RunCMD(args: Array<String>, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        var player: Player? = null
        if (sender is Player) player = sender as Player
        val shopname: String = args[1].replace("/", "")
        val data = CustomConfig()
        data.setup(shopname, "Shop")
        if (!ShopUtil.shopConfigFiles.containsKey(shopname)) {
            data.get().set("Options.title", shopname)
            data.get().set("Options.enable", false)
            data.get().set("Options.lore", "")
            data.get().set("Options.page", 2)
            if (args.size >= 3) {
                if (args[2].equalsIgnoreCase("true")) {
                    data.get().set("Options.permission", "dshop.user.shop.$shopname")
                } else if (args[2].equalsIgnoreCase("false")) {
                    data.get().set("Options.permission", "")
                } else {
                    data.get().set("Options.permission", args[2])
                }
            } else {
                data.get().set("Options.permission", "")
            }
            data.get().set("0.mat", "DIRT")
            data.get().set("0.value", 1)
            data.get().set("0.median", 10000)
            data.get().set("0.stock", 10000)
            data.save()
            ShopUtil.shopConfigFiles.put(shopname, data)
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "MESSAGE.SHOP_CREATED"))
            if (player != null) DynaShopAPI.openShopGui(player, shopname, 1)
        } else {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.SHOP_EXIST"))
        }
    }
}