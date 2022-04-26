package me.sat7.dynamicshop.events

import me.sat7.dynamicshop.DynaShopAPI

class OnClick : Listener {
    @EventHandler
    fun OnInventoryDragEvent(e: InventoryDragEvent) {
        // UI 인벤토리에 드래그로 아이탬 올리는것을 막음
        if (UIManager.IsPlayerUsingPluginGUI(e.getWhoClicked() as Player)) e.setCancelled(true)
    }

    @EventHandler
    fun OnInventoryClickEvent(e: InventoryClickEvent) {
        if (e.getClickedInventory() == null) return
        val player: Player = e.getWhoClicked() as Player

        // 위쪽 인벤토리를 클릭함 (= 내 인벤이 아님)
        if (e.getClickedInventory() !== player.getInventory()) {
            // UUID 확인 // todo 이게 왜 필요하지?
            val pUuid: String = player.getUniqueId().toString()
            if (DynamicShop.ccUser.get().getConfigurationSection(pUuid) == null) {
                if (!DynaShopAPI.recreateUserData(player)) {
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "ERR.NO_USER_ID"))
                    e.setCancelled(true)
                    return
                }
            }
            if (UIManager.IsPlayerUsingPluginGUI(player)) {
                e.setCancelled(true)
                UIManager.OnClickUpperInventory(e)
            }
        } else {
            if (UIManager.GetPlayerCurrentUIType(player) === InGameUI.UI_TYPE.ItemPalette || UIManager.GetPlayerCurrentUIType(player) === InGameUI.UI_TYPE.QuickSell || UIManager.GetPlayerCurrentUIType(player) === InGameUI.UI_TYPE.ItemSettings || UIManager.GetPlayerCurrentUIType(player) === InGameUI.UI_TYPE.Shop || UIManager.GetPlayerCurrentUIType(player) === InGameUI.UI_TYPE.StartPage) {
                e.setCancelled(true)
                UIManager.OnClickLowerInventory(e)
            } else if (e.isShiftClick() && UIManager.IsPlayerUsingPluginGUI(player)) {
                e.setCancelled(true)
            }
        }
    }
}