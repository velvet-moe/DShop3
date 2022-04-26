package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.commands.shop.*

object CMDManager {
    val CMDHashMap: HashMap<String, DSCMD> = HashMap()
    var commandHelp: CommandHelp? = null
    var createShop: CreateShop? = null
    var deleteShop: DeleteShop? = null
    var deleteUser: DeleteUser? = null
    var mergeShop: MergeShop? = null
    var openShop: OpenShop? = null
    var reload: Reload? = null
    var renameShop: RenameShop? = null
    var setDefaultShop: SetDefaultShop? = null
    var setTax: SetTax? = null
    var account: Account? = null
    var add: Add? = null
    var addHand: AddHand? = null
    var edit: Edit? = null
    var editAll: EditAll? = null
    var enable: Enable? = null
    var flag: Flag? = null
    var fluctuation: Fluctuation? = null
    var log: Log? = null
    var maxPage: MaxPage? = null
    var permission: Permission? = null
    var position: Position? = null
    var sellBuy: SellBuy? = null
    var setToRecAll: SetToRecAll? = null
    var shopHours: ShopHours? = null
    var stockStabilizing: StockStabilizing? = null
    fun Init() {
        CMDHashMap.clear()

        // ds
        commandHelp = CommandHelp()
        createShop = CreateShop()
        deleteShop = DeleteShop()
        deleteUser = DeleteUser()
        mergeShop = MergeShop()
        openShop = OpenShop()
        renameShop = RenameShop()
        reload = Reload()
        setDefaultShop = SetDefaultShop()
        setTax = SetTax()
        CMDHashMap.put("cmdhelp", commandHelp)
        CMDHashMap.put("createshop", createShop)
        CMDHashMap.put("deleteshop", deleteShop)
        CMDHashMap.put("deleteolduser", deleteUser)
        CMDHashMap.put("mergeshop", mergeShop)
        CMDHashMap.put("openshop", openShop)
        CMDHashMap.put("renameshop", renameShop)
        CMDHashMap.put("reload", reload)
        CMDHashMap.put("setdefaultshop", setDefaultShop)
        CMDHashMap.put("settax", setTax)

        // ds shop
        account = Account()
        add = Add()
        addHand = AddHand()
        edit = Edit()
        editAll = EditAll()
        enable = Enable()
        flag = Flag()
        fluctuation = Fluctuation()
        log = Log()
        maxPage = MaxPage()
        permission = Permission()
        position = Position()
        sellBuy = SellBuy()
        setToRecAll = SetToRecAll()
        shopHours = ShopHours()
        stockStabilizing = StockStabilizing()
        CMDHashMap.put("account", account)
        CMDHashMap.put("add", add)
        CMDHashMap.put("addhand", addHand)
        CMDHashMap.put("edit", edit)
        CMDHashMap.put("editall", editAll)
        CMDHashMap.put("enable", enable)
        CMDHashMap.put("flag", flag)
        CMDHashMap.put("fluctuation", fluctuation)
        CMDHashMap.put("log", log)
        CMDHashMap.put("maxpage", maxPage)
        CMDHashMap.put("permission", permission)
        CMDHashMap.put("position", position)
        CMDHashMap.put("sellbuy", sellBuy)
        CMDHashMap.put("settorecall", setToRecAll)
        CMDHashMap.put("shophours", shopHours)
        CMDHashMap.put("stockstabilizing", stockStabilizing)
    }

    fun RunCMD(key: String?, args: Array<String>?, sender: CommandSender?) {
        if (CMDHashMap.containsKey(key)) {
            CMDHashMap.get(key).RunCMD(args, sender)
        }
    }
}