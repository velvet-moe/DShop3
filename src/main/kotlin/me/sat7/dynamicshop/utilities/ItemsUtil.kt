package me.sat7.dynamicshop.utilities

import java.util.ArrayList

object ItemsUtil {
    // 지정된 이름,lore,수량의 아이탬 스택 생성및 반환
    fun createItemStack(material: Material?, _meta: ItemMeta?, name: String?, lore: ArrayList<String?>?, amount: Int): ItemStack {
        val istack = ItemStack(material, amount)
        var meta: ItemMeta? = _meta
        if (_meta == null) meta = istack.getItemMeta()
        if (name != null && !name.equals("")) meta.setDisplayName(name)
        meta.setLore(lore)
        istack.setItemMeta(meta)
        return istack
    }

    // 아이탬 이름 정돈
    fun getBeautifiedName(mat: Material): String {
        return getBeautifiedName(mat.toString())
    }

    fun getBeautifiedName(matName: String): String {
        val temp: String = matName.replace("_", " ").toLowerCase()
        val temparr: Array<String> = temp.split(" ")
        var finalStr = StringBuilder()
        for (s in temparr) {
            s = ("" + s.charAt(0)).toUpperCase() + s.substring(1)
            finalStr.append(s).append(" ")
        }
        finalStr = StringBuilder(finalStr.substring(0, finalStr.length() - 1))
        return finalStr.toString()
    }

    // 아이탬 정보 출력
    fun sendItemInfo(sender: CommandSender?, shopName: String?, idx: Int, msgType: String?) {
        if (sender is Player) sendItemInfo(sender as Player?, shopName, idx, msgType)
    }

    fun sendItemInfo(player: Player, shopName: String?, idx: Int, msgType: String?) {
        val data: CustomConfig = ShopUtil.shopConfigFiles.get(shopName)
        var info = " value:" + data.get().getDouble("$idx.value")
        val valueMin: Double = data.get().getDouble("$idx.valueMin")
        if (valueMin > 0.01) info += " min:$valueMin"
        val valueMax: Double = data.get().getDouble("$idx.valueMax")
        if (valueMax > 0) info += " max:$valueMax"
        info += " median:" + data.get().getInt("$idx.median")
        info += " stock:" + data.get().getInt("$idx.stock")
        player.sendMessage(" - " + t(player, msgType).replace("{item}", data.get().getString("$idx.mat")).replace("{info}", info)
        )
    }
}