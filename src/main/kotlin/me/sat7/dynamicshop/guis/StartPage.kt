package me.sat7.dynamicshop.guis

import java.util.ArrayList

class StartPage : InGameUI() {
    private var selectedIndex = -1

    init {
        uiType = UI_TYPE.StartPage
    }

    fun getGui(player: Player): Inventory {
        selectedIndex = -1
        inventory = Bukkit.createInventory(player, ccStartPage.get().getInt("Options.UiSlotCount"), ccStartPage.get().getString("Options.Title"))

        //아이콘, 이름, 로어, 인덱스, 커맨드
        val cs: ConfigurationSection = ccStartPage.get().getConfigurationSection("Buttons")
        for (s in cs.getKeys(false)) {
            try {
                val idx: Int = Integer.parseInt(s)
                var name = " "
                if (cs.contains("$s.displayName")) {
                    name = cs.getConfigurationSection(s).getString("displayName")
                }
                val tempList: ArrayList<String> = ArrayList()
                if (cs.contains("$s.lore")) {
                    val lore: Array<String> = cs.getConfigurationSection(s).getString("lore").split(ccStartPage.get().getString("Options.LineBreak"))
                    tempList.addAll(Arrays.asList(lore))
                }
                if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                    val cmd: String = cs.getString("$s.action")
                    if (cmd != null && cmd.length() > 0) {
                        tempList.add(t(player, "START_PAGE.ITEM_MOVE_LORE"))
                    } else {
                        tempList.add(t(player, "START_PAGE.ITEM_REMOVE_LORE"))
                        tempList.add(t(player, "START_PAGE.ITEM_COPY_LORE"))
                    }
                    tempList.add(t(player, "START_PAGE.ITEM_EDIT_LORE"))
                }
                val btn = ItemStack(Material.getMaterial(cs.getConfigurationSection(s).getString("icon")))
                val meta: ItemMeta = btn.getItemMeta()
                meta.setDisplayName(name)
                meta.setLore(tempList)
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
                btn.setItemMeta(meta)
                inventory.setItem(idx, btn)
            } catch (e: Exception) {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Fail to create Start page button")
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + e)
            }
        }
        return inventory
    }

    @Override
    override fun OnClickUpperInventory(e: InventoryClickEvent) {
        val player: Player = e.getWhoClicked() as Player
        if (e.isLeftClick()) {
            if (e.isShiftClick() && player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                if (e.getCurrentItem() != null && e.getCurrentItem().getType() !== Material.AIR) {
                    val actionString: String = ccStartPage.get().getString("Buttons." + e.getSlot() + ".action")
                    if (actionString == null || actionString.isEmpty()) {
                        ccStartPage.get().set("Buttons." + e.getSlot(), null)
                        ccStartPage.save()
                        DynaShopAPI.openStartPage(player)
                    }
                }
            } else {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() === Material.AIR) {
                    // 새 버튼 추가
                    if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
                        ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", "§3New Button")
                        ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", "§fnew button")
                        ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", Material.SUNFLOWER.name())
                        ccStartPage.get().set("Buttons." + e.getSlot() + ".action", "")
                        ccStartPage.save()
                        DynaShopAPI.openStartPage(player)
                    } else {
                        return
                    }
                }
                val actionStr: String = ccStartPage.get().getString("Buttons." + e.getSlot() + ".action")
                if (actionStr != null && actionStr.length() > 0) {
                    val action: Array<String> = actionStr.split(ccStartPage.get().getString("Options.LineBreak"))
                    for (s in action) {
                        Bukkit.dispatchCommand(player, s)
                    }
                }
            }
        } else if (player.hasPermission(P_ADMIN_SHOP_EDIT)) {
            // 편집
            if (e.isShiftClick()) {
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() === Material.AIR) return
                selectedIndex = e.getSlot()
                DynaShopAPI.openStartPageSettingGui(player, selectedIndex)
            } else {
                if (selectedIndex == -1) {
                    if (e.getCurrentItem() == null || e.getCurrentItem().getType() === Material.AIR) return
                    selectedIndex = e.getSlot()
                    player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "SHOP.ITEM_MOVE_SELECTED"))
                } else {
                    if (e.getCurrentItem() != null && e.getCurrentItem().getType() !== Material.AIR) return
                    ccStartPage.get().set("Buttons." + e.getSlot() + ".displayName", ccStartPage.get().get("Buttons.$selectedIndex.displayName"))
                    ccStartPage.get().set("Buttons." + e.getSlot() + ".lore", ccStartPage.get().get("Buttons.$selectedIndex.lore"))
                    ccStartPage.get().set("Buttons." + e.getSlot() + ".icon", ccStartPage.get().get("Buttons.$selectedIndex.icon"))
                    ccStartPage.get().set("Buttons." + e.getSlot() + ".action", ccStartPage.get().get("Buttons.$selectedIndex.action"))
                    if (ccStartPage.get().getString("Buttons.$selectedIndex.action").length() > 0) {
                        ccStartPage.get().set("Buttons.$selectedIndex", null)
                    }
                    ccStartPage.save()
                    DynaShopAPI.openStartPage(player)
                }
            }
        }
    }

    @Override
    override fun OnClickLowerInventory(e: InventoryClickEvent) {
        if (!DynamicShop.plugin.getConfig().getBoolean("UI.EnableInventoryClickSearch.StartPage")) return
        val player: Player = e.getWhoClicked() as Player
        val itemStack: ItemStack = e.getCurrentItem()
        if (itemStack == null || itemStack.getType().isAir()) {
            player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "MESSAGE.CLICK_YOUR_ITEM_START_PAGE"))
            return
        }
        if (!e.isLeftClick() && !e.isRightClick()) return
        val isSell: Boolean = e.isRightClick()
        val ret: Array<String>
        ret = if (isSell) {
            ShopUtil.FindTheBestShopToSell(player, e.getCurrentItem())
        } else {
            ShopUtil.FindTheBestShopToBuy(player, e.getCurrentItem())
        }
        if (ret[0].isEmpty()) return
        DynaShopAPI.openShopGui(player, ret[0], Integer.parseInt(ret[1]) / 45 + 1)
        val useLocalizedName: Boolean = DynamicShop.plugin.getConfig().getBoolean("UI.LocalizedItemName")
        var message: String
        message = if (isSell) {
            DynamicShop.dsPrefix(player) + t(player, "MESSAGE.MOVE_TO_BEST_SHOP_SELL", !useLocalizedName)
        } else {
            DynamicShop.dsPrefix(player) + t(player, "MESSAGE.MOVE_TO_BEST_SHOP_BUY", !useLocalizedName)
        }
        if (useLocalizedName) {
            message = message.replace("{item}", "<item>")
            LangUtil.sendMessageWithLocalizedItemName(player, message, e.getCurrentItem().getType())
        } else {
            val itemName: String = ItemsUtil.getBeautifiedName(e.getCurrentItem().getType())
            player.sendMessage(message.replace("{item}", itemName))
        }
    }

    companion object {
        var ccStartPage: CustomConfig? = null
        fun setupStartPageFile() {
            ccStartPage.setup("Startpage", null)
            ccStartPage.get().options().header("LineBreak: Do not use \\, | and brackets. Recommended : /, _")
            ccStartPage.get().addDefault("Options.Title", "§3§lStart Page")
            ccStartPage.get().addDefault("Options.UiSlotCount", 27)
            ccStartPage.get().addDefault("Options.LineBreak", "/")
            if (ccStartPage.get().getKeys(false).size() === 0) {
                ccStartPage.get().set("Buttons.0.displayName", "§3§lExample Button")
                ccStartPage.get().set("Buttons.0.lore", "§fThis is Example Button/§aClick empty slot to create new button")
                ccStartPage.get().set("Buttons.0.icon", "SUNFLOWER")
                ccStartPage.get().set("Buttons.0.action", "")
            }
            ccStartPage.get().options().copyDefaults(true)
            ccStartPage.save()
        }
    }
}