package me.sat7.dynamicshop.constants

import me.sat7.dynamicshop.guis.UIManager
import me.sat7.dynamicshop.jobshook.JobsHook
import kotlin.Throws
import me.sat7.dynamicshop.UpdateChecker

object Constants {
    const val DYNAMIC_SHOP_PREFIX = "§3[DShop]§f"
    const val P_ADMIN_CREATIVE = "dshop.admin.creative"
    const val P_ADMIN_REMOTE_ACCESS = "dshop.admin.remoteaccess"
    const val P_ADMIN_DELETE_OLD_USER = "dshop.admin.deleteOldUser"
    const val P_ADMIN_SET_DEFAULT_SHOP = "dshop.admin.setdefaultshop"
    const val P_ADMIN_SET_TAX = "dshop.admin.settax"
    const val P_ADMIN_RELOAD = "dshop.admin.reload"
    const val P_ADMIN_CREATE_SIGN = "dshop.admin.createsign"
    const val P_ADMIN_DESTROY_SIGN = "dshop.admin.destroysign"
    const val P_ADMIN_CREATE_SHOP = "dshop.admin.createshop"
    const val P_ADMIN_DELETE_SHOP = "dshop.admin.deleteshop"
    const val P_ADMIN_MERGE_SHOP = "dshop.admin.mergeshop"
    const val P_ADMIN_RENAME_SHOP = "dshop.admin.renameshop"
    const val P_ADMIN_SHOP_EDIT = "dshop.admin.shopedit"
    const val P_ADMIN_OPEN_SHOP = "dshop.admin.openshop"
    const val P_ADMIN_EDIT_ALL = "dshop.admin.editall"
    const val P_USE = "dshop.use" // 이 권한은 기본적으로 지급됨
    const val P_USE_QSELL = "dshop.use.qsell" // 이 권한은 기본적으로 지급됨
}