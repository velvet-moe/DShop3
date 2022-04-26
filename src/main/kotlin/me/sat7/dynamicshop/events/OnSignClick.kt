package me.sat7.dynamicshop.events

import me.sat7.dynamicshop.DynaShopAPI

class OnSignClick : Listener {
    // 생성
    @EventHandler
    fun onSignChange(e: SignChangeEvent) {
        if (!e.getPlayer().hasPermission(P_ADMIN_CREATE_SIGN)) return
        if (e.getLine(0).equalsIgnoreCase("[dshop]")
                || e.getLine(0).equalsIgnoreCase("[ds]")
                || e.getLine(0).equalsIgnoreCase("[dynamicshop]")) {
            val signId = CreateID(e.getBlock())
            if (e.getLine(1).length() === 0) {
                e.setLine(1, "Error")
                e.setLine(2, "shop name is null")
                e.getBlock().getState().update()
                return
            }
            if (!ShopUtil.shopConfigFiles.containsKey(e.getLine(1))) {
                e.setLine(1, "Error")
                e.setLine(2, "No shop")
                e.setLine(3, "with that name")
                e.getBlock().getState().update()
                return
            }
            e.setLine(0, e.getLine(3))
            e.setLine(1, "§a" + e.getLine(1))
            e.setLine(3, "")
            e.getBlock().getState().update()
            DynamicShop.ccSign.get().set("$signId.shop", ChatColor.stripColor(e.getLine(1)))
            val tempBlock: Block = e.getBlock()
            var blockBehind: Block? = null
            if (tempBlock.getState() is Sign) {
                val data: BlockData = tempBlock.getBlockData()
                if (data is Directional) {
                    val directional: Directional = data as Directional
                    blockBehind = tempBlock.getRelative(directional.getFacing().getOppositeFace())
                }
            }
            if (blockBehind != null) {
                DynamicShop.ccSign.get().set("$signId.attached", CreateID(blockBehind))
            } else {
                e.setLine(1, "Error")
                e.setLine(2, "Sign must be ")
                e.setLine(3, "placed on wall")
                e.getBlock().getState().update()
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Err. Sign must be placed on wall. " + signId)
                return
            }
            try {
                val shop: String = ChatColor.stripColor(e.getLine(1))
                val mat: String = ChatColor.stripColor(e.getLine(2)).toUpperCase()
                val i: Int = ShopUtil.findItemFromShop(shop, ItemStack(Material.getMaterial(mat)))
                e.setLine(2, ShopUtil.shopConfigFiles.get(shop).get().getString("$i.mat"))
                DynamicShop.ccSign.get().set("$signId.mat", mat)
            } catch (exception: Exception) {
                e.setLine(2, "")
            }
            DynamicShop.ccSign.save()
        }
    }

    // 상호작용
    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val p: Player = e.getPlayer()
        if (e.getAction() === Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock().getType().toString().contains("WALL_SIGN")) {
                val s: Sign = e.getClickedBlock().getState() as Sign
                val signId = CreateID(e.getClickedBlock())

                // 정보가 없음
                if (!DynamicShop.ccSign.get().contains(signId) && s.getLine(1).length() > 0 &&
                        ShopUtil.shopConfigFiles.containsKey(ChatColor.stripColor(s.getLine(1)))) {
                    // 재생성 시도
                    if (e.getPlayer().hasPermission(P_ADMIN_CREATE_SIGN)) {
                        val shop: String = ChatColor.stripColor(s.getLine(1))
                        DynamicShop.ccSign.get().set("$signId.shop", shop)
                        s.setLine(0, "")
                        s.setLine(1, "§a" + s.getLine(1))
                        try {
                            val mat: String = ChatColor.stripColor(s.getLine(2)).toUpperCase()
                            val i: Int = ShopUtil.findItemFromShop(shop, ItemStack(Material.getMaterial(mat)))
                            s.setLine(2, ShopUtil.shopConfigFiles.get(shop).get().getString("$i.mat"))
                            DynamicShop.ccSign.get().set("$signId.mat", mat)
                        } catch (exception: Exception) {
                            s.setLine(2, "")
                        }
                        s.update()
                        DynamicShop.ccSign.save()
                    } else {
                        return
                    }
                }
                if (DynamicShop.ccSign.get().contains(signId) && !DynamicShop.ccSign.get().contains("$signId.attached")) {
                    val tempBlock: Block = e.getClickedBlock()
                    var blockBehind: Block? = null
                    if (tempBlock != null && tempBlock.getState() is Sign) {
                        val data: BlockData = tempBlock.getBlockData()
                        if (data is Directional) {
                            val directional: Directional = data as Directional
                            blockBehind = tempBlock.getRelative(directional.getFacing().getOppositeFace())
                        }
                    }
                    DynamicShop.ccSign.get().set("$signId.attached", CreateID(blockBehind))
                    DynamicShop.ccSign.save()
                }
                val shopName: String = DynamicShop.ccSign.get().getString("$signId.shop")
                if (shopName == null || shopName.length() === 0) return

                // 상점 존재 확인
                if (ShopUtil.shopConfigFiles.containsKey(shopName)) {
                    if (p.getGameMode() === GameMode.CREATIVE && !p.hasPermission(Constants.P_ADMIN_CREATIVE)) {
                        p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.CREATIVE"))
                        return
                    }
                    if (!p.hasPermission(P_ADMIN_SHOP_EDIT)) e.setCancelled(true) else {
                        val itemName: String = p.getInventory().getItemInMainHand().getType().name()
                        if (itemName.contains("INK_SAC") || itemName.contains("_DYE")) return
                    }


                    //권한 확인
                    val permission: String = ShopUtil.shopConfigFiles.get(shopName).get().getString("Options.permission")
                    if (permission != null && permission.length() > 0) {
                        if (!p.hasPermission(permission) && !p.hasPermission("$permission.buy") && !p.hasPermission("$permission.sell")) {
                            p.sendMessage(DynamicShop.dsPrefix(p) + t(p, "ERR.NO_PERMISSION"))
                            return
                        }
                    }
                    try {
                        val idx: Int = ShopUtil.findItemFromShop(shopName, ItemStack(Material.getMaterial(DynamicShop.ccSign.get().getString("$signId.mat"))))
                        if (idx != -1) {
                            DynamicShop.userTempData.put(p.getUniqueId(), "sign")
                            DynaShopAPI.openItemTradeGui(p, shopName, String.valueOf(idx))
                        } else {
                            DynamicShop.userTempData.put(p.getUniqueId(), "sign")
                            DynaShopAPI.openShopGui(p, shopName, 1)
                        }
                    } catch (exception: Exception) {
                        DynamicShop.userTempData.put(p.getUniqueId(), "sign")
                        DynaShopAPI.openShopGui(p, shopName, 1)
                    }
                }
            }
        }
    }

    // 파괴
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val b: Block = e.getBlock()
        val eventID = CreateID(b)
        for (s in DynamicShop.ccSign.get().getKeys(false)) {
            if (s.equals(eventID)) {
                if (!e.getPlayer().hasPermission(P_ADMIN_DESTROY_SIGN)) {
                    e.setCancelled(true)
                } else {
                    DynamicShop.ccSign.get().set(eventID, null)
                    DynamicShop.ccSign.save()
                }
                break
            }
            if (eventID.equals(DynamicShop.ccSign.get().getString("$s.attached"))) {
                if (!e.getPlayer().hasPermission(P_ADMIN_DESTROY_SIGN)) {
                    e.setCancelled(true)
                } else {
                    DynamicShop.ccSign.get().set(s, null)
                    DynamicShop.ccSign.save()
                }
                break
            }
        }
    }

    // 상점 표지판이 폭발하는것 방지
    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val b: List<Block> = event.blockList()
        for (bl in b) {
            if (bl.getType().toString().contains("WALL_SIGN")) {
                if (DynamicShop.ccSign.get().contains(CreateID(bl))) {
                    event.setCancelled(true)
                    return
                }
            }
        }
    }

    // 상점 표지판이 불타는것 방지
    @EventHandler
    fun onBlockBurn(e: BlockBurnEvent) {
        val signList: ArrayList<Block> = ArrayList()
        signList.add(e.getBlock())
        signList.add(e.getBlock().getRelative(BlockFace.EAST))
        signList.add(e.getBlock().getRelative(BlockFace.WEST))
        signList.add(e.getBlock().getRelative(BlockFace.NORTH))
        signList.add(e.getBlock().getRelative(BlockFace.SOUTH))
        for (b in signList) {
            if (b.getType().toString().contains("WALL_SIGN")) {
                if (DynamicShop.ccSign.get().contains(CreateID(b))) {
                    e.setCancelled(true)
                }
            }
        }
    }

    private fun CreateID(attachedBlock: Block?): String {
        val x: Int = attachedBlock.getX()
        val y: Int = attachedBlock.getY()
        val z: Int = attachedBlock.getZ()
        return attachedBlock.getWorld() + "_" + x + "_" + y + "_" + z
    }
}