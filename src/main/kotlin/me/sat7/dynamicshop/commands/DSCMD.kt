package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.DynamicShop

class DSCMD {
    var inGameUseOnly = true
    var permission: String? = null
    val validArgCount: ArrayList<Integer> = ArrayList()
    fun SendHelpMessage(player: Player?) {}
    fun CheckValid(args: Array<String?>, sender: CommandSender): Boolean {
        if (inGameUseOnly && sender !is Player) {
            sender.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " You can't run this command in console")
            return false
        }
        if (!validArgCount.contains(args.size)) {
            if (validArgCount.size() !== 0 && Collections.max(validArgCount) >= args.size) {
                sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.WRONG_USAGE"))
                return false
            }
        }
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "ERR.NO_PERMISSION"))
            return false
        }
        return true
    }

    fun RunCMD(args: Array<String?>?, sender: CommandSender?) {}
}