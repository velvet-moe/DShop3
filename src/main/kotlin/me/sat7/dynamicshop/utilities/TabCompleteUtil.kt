package me.sat7.dynamicshop.utilities

import java.util.stream.Collectors

object TabCompleteUtil {
    val temp: ArrayList<String> = ArrayList()
    val autoCompleteList: ArrayList<String> = ArrayList()
    fun onTabCompleteBody(dynamicShop: DynamicShop, sender: CommandSender, cmd: Command, args: Array<String>): List<String>? {
        if (sender !is Player) return null
        val p: Player = sender as Player
        val uuid: UUID = p.getUniqueId()
        try {
            temp.clear()
            autoCompleteList.clear()
            if (cmd.getName().equalsIgnoreCase("shop") && args.size == 1) {
                if (!dynamicShop.getConfig().getBoolean("Command.UseShopCommand")) return autoCompleteList
                for (entry in ShopUtil.shopConfigFiles.entrySet()) {
                    val options: ConfigurationSection = entry.getValue().get().getConfigurationSection("Options")
                    if (options.contains("flag.signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS)) continue
                    if (options.contains("flag.hiddenincommand") && !sender.hasPermission(P_ADMIN_SHOP_EDIT)) continue
                    val permission: String = options.getString("permission", "")
                    if (permission.isEmpty()
                            || !DynamicShop.plugin.getConfig().getBoolean("Command.PermissionCheckWhenCreatingAShopList")
                            || p.hasPermission(permission)) temp.add(entry.getKey())
                }
                AddToAutoCompleteIfValid(args[0])
                return autoCompleteList
            } else if (cmd.getName().equalsIgnoreCase("DynamicShop")) {
                if (args.size == 1) {
                    Help.showHelp("main", sender as Player, args)
                    temp.add("shop")
                    temp.add("qsell")
                    if (sender.hasPermission(P_ADMIN_CREATE_SHOP)) temp.add("createshop")
                    if (sender.hasPermission(P_ADMIN_DELETE_SHOP)) temp.add("deleteshop")
                    if (sender.hasPermission(P_ADMIN_MERGE_SHOP)) temp.add("mergeshop")
                    if (sender.hasPermission(P_ADMIN_RENAME_SHOP)) temp.add("renameshop")
                    if (sender.hasPermission(P_ADMIN_OPEN_SHOP)) temp.add("openshop")
                    if (sender.hasPermission(P_ADMIN_SET_TAX)) temp.add("settax")
                    if (sender.hasPermission(P_ADMIN_SET_TAX)) temp.add("settax temp")
                    if (sender.hasPermission(P_ADMIN_SET_DEFAULT_SHOP)) temp.add("setdefaultshop")
                    if (sender.hasPermission(P_ADMIN_DELETE_OLD_USER)) temp.add("deleteOldUser")
                    if (sender.hasPermission(P_ADMIN_RELOAD)) temp.add("reload")
                    temp.add("cmdHelp")
                    AddToAutoCompleteIfValid(args[0])
                } else if (args.size >= 2 && args[0].equals("shop")) {
                    val data: CustomConfig = ShopUtil.shopConfigFiles.get(args[1])
                    if (args.size == 2) {
                        Help.showHelp("shop", sender as Player, args)
                        for (entry in ShopUtil.shopConfigFiles.entrySet()) {
                            val options: ConfigurationSection = entry.getValue().get().getConfigurationSection("Options")
                                    ?: continue
                            if (options.contains("flag") && options.getConfigurationSection("flag").contains("signshop") && !sender.hasPermission(Constants.P_ADMIN_REMOTE_ACCESS)) continue
                            if (options.contains("flag.hiddenincommand") && !sender.hasPermission(P_ADMIN_SHOP_EDIT)) continue
                            val permission: String = options.getString("permission", "")
                            if (permission.isEmpty()
                                    || !DynamicShop.plugin.getConfig().getBoolean("Command.PermissionCheckWhenCreatingAShopList")
                                    || p.hasPermission(permission)) temp.add(entry.getKey())
                        }
                        AddToAutoCompleteIfValid(args[1])
                    } else if (args.size >= 3 && (!ShopUtil.shopConfigFiles.containsKey(args[1]) || args[1].length() === 0)) {
                        return null
                    } else if (args.size == 3) {
                        //add,addhand,edit,editall,permission,maxpage,flag
                        if (sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            temp.add("enable")
                            temp.add("add")
                            temp.add("addhand")
                            temp.add("edit")
                            temp.add("editall")
                            temp.add("setToRecAll")
                            temp.add("permission")
                            temp.add("maxpage")
                            temp.add("flag")
                            temp.add("position")
                            temp.add("shophours")
                            temp.add("fluctuation")
                            temp.add("stockStabilizing")
                            temp.add("account")
                            temp.add("sellbuy")
                            temp.add("log")
                        }
                        AddToAutoCompleteIfValid(args[2])
                    } else if (args.size >= 4) {
                        if (args[2].equalsIgnoreCase("enable") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("enable", sender as Player, args)
                            if (args.size == 4) {
                                temp.add("true")
                                temp.add("false")
                            }
                            AddToAutoCompleteIfValid(args[3])
                        } else if (args[2].equalsIgnoreCase("addhand") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("add_hand", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("add") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                Help.showHelp("add", sender as Player, args)
                                for (m in Material.values()) {
                                    temp.add(m.name())
                                }
                                AddToAutoCompleteIfValid(args[3])
                            } else if (args.size == 5) {
                                val mat: String = args[3].toUpperCase()
                                val userTempStr: String = DynamicShop.userTempData.get(uuid)
                                if (!(userTempStr.contains("add") && userTempStr.length() > 3)) {
                                    if (Material.matchMaterial(mat) != null) {
                                        Help.showHelp("add" + args[3], sender as Player, args)
                                    }
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("edit") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                Help.showHelp("edit", sender as Player, args)
                                for (s in data.get().getKeys(false)) {
                                    try {
                                        val i: Int = Integer.parseInt(s)
                                        if (!data.get().contains("$s.value")) continue  // 장식용임
                                        temp.add(s + "/" + data.get().getString("$s.mat"))
                                    } catch (ignored: Exception) {
                                    }
                                }
                                AddToAutoCompleteIfValid(args[3])
                            } else if (args.size == 5) {
                                var mat = args[3]
                                mat = mat.substring(mat.indexOf("/") + 1)
                                mat = mat.toUpperCase()
                                if (Material.matchMaterial(mat) != null) {
                                    Help.showHelp("edit$mat", sender as Player, args)
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("editall") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("edit_all", sender as Player, args)
                            if (args.size == 4) {
                                temp.add("purchaseValue")
                                temp.add("salesValue")
                                temp.add("valueMin")
                                temp.add("valueMax")
                                temp.add("stock")
                                temp.add("median")
                                temp.add("maxStock")
                                AddToAutoCompleteIfValid(args[3])
                            } else if (args.size == 5) {
                                temp.add("=")
                                temp.add("+")
                                temp.add("-")
                                temp.add("/")
                                temp.add("*")
                                AddToAutoCompleteIfValid(args[4])
                            } else if (args.size == 6) {
                                if (args[4].equals("=")) {
                                    temp.add("purchaseValue")
                                    temp.add("salesValue")
                                    temp.add("valueMin")
                                    temp.add("valueMax")
                                    temp.add("stock")
                                    temp.add("median")
                                    temp.add("maxStock")
                                    AddToAutoCompleteIfValid(args[5])
                                }
                            }
                        } else if (args[2].equalsIgnoreCase("setToRecAll") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("set_to_rec_all", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("permission") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("permission", sender as Player, args)
                            if (args.size >= 4) {
                                temp.add("true")
                                temp.add("false")
                                AddToAutoCompleteIfValid(args[3])
                            }
                        } else if (args[2].equalsIgnoreCase("maxpage") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("max_page", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("flag") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                temp.add("signShop")
                                temp.add("localShop")
                                temp.add("deliveryCharge")
                                temp.add("jobPoint")
                                temp.add("showValueChange")
                                temp.add("hideStock")
                                temp.add("hidePricingType")
                                temp.add("hideShopBalance")
                                temp.add("showMaxStock")
                                temp.add("hiddenInCommand")
                                temp.add("integerOnly")
                                AddToAutoCompleteIfValid(args[3])
                            } else if (args.size > 4) {
                                temp.add("set")
                                temp.add("unset")
                                AddToAutoCompleteIfValid(args[4])
                            }
                            Help.showHelp("flag", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("position") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size >= 4) {
                                temp.add("pos1")
                                temp.add("pos2")
                                temp.add("clear")
                                AddToAutoCompleteIfValid(args[3])
                            }
                            Help.showHelp("position", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("shophours") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("shophours", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("fluctuation") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("fluctuation", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("stockStabilizing") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            Help.showHelp("stock_stabilizing", sender as Player, args)
                        } else if (args[2].equalsIgnoreCase("account") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                temp.add("set")
                                temp.add("linkto")
                                temp.add("transfer")
                                AddToAutoCompleteIfValid(args[3])
                                Help.showHelp("account", sender as Player, args)
                            } else if (args.size == 5) {
                                if (args[3].equals("linkto") || args[3].equals("transfer")) {
                                    temp.addAll(ShopUtil.shopConfigFiles.keySet())
                                }
                                when (args[3]) {
                                    "set" -> Help.showHelp("account_set", sender as Player, args)
                                    "transfer" -> {
                                        Help.showHelp("account_transfer", sender as Player, args)
                                        for (onlinePlayer in Bukkit.getServer().getOnlinePlayers()) {
                                            temp.add(onlinePlayer.getName())
                                        }
                                    }
                                    "linkto" -> Help.showHelp("account_link_to", sender as Player, args)
                                }
                                AddToAutoCompleteIfValid(args[4])
                            }
                        } else if (args[2].equalsIgnoreCase("sellbuy") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                temp.add("sellOnly")
                                temp.add("buyOnly")
                                temp.add("clear")
                                AddToAutoCompleteIfValid(args[3])
                                Help.showHelp("sellbuy", sender as Player, args)
                            }
                        } else if (args[2].equalsIgnoreCase("log") && sender.hasPermission(P_ADMIN_SHOP_EDIT)) {
                            if (args.size == 4) {
                                temp.add("enable")
                                temp.add("disable")
                                temp.add("clear")
                                AddToAutoCompleteIfValid(args[3])
                                Help.showHelp("log", sender as Player, args)
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("createshop") && sender.hasPermission(P_ADMIN_CREATE_SHOP)) {
                    if (args.size == 3) {
                        temp.add("true")
                        temp.add("false")
                        AddToAutoCompleteIfValid(args[2])
                    }
                    Help.showHelp("create_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("deleteshop") && sender.hasPermission(P_ADMIN_DELETE_SHOP)) {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet())
                    AddToAutoCompleteIfValid(args[1])
                    Help.showHelp("delete_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("mergeshop") && sender.hasPermission(P_ADMIN_MERGE_SHOP)) {
                    if (args.size <= 3) {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet())
                        AddToAutoCompleteIfValid(args[args.size - 1])
                    }
                    Help.showHelp("merge_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("openshop") && sender.hasPermission(P_ADMIN_OPEN_SHOP)) {
                    if (args.size == 2) {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet())
                        AddToAutoCompleteIfValid(args[args.size - 1])
                    } else if (args.size == 3) {
                        temp.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()))
                        AddToAutoCompleteIfValid(args[args.size - 1])
                    }
                    Help.showHelp("open_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("renameshop") && sender.hasPermission(P_ADMIN_RENAME_SHOP)) {
                    if (args.size == 2) {
                        temp.addAll(ShopUtil.shopConfigFiles.keySet())
                        AddToAutoCompleteIfValid(args[1])
                    }
                    Help.showHelp("rename_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("cmdHelp")) {
                    if (args.size == 2) {
                        autoCompleteList.add("on")
                        autoCompleteList.add("off")
                        Help.showHelp("cmd_help", sender as Player, args)
                    }
                } else if (args[0].equalsIgnoreCase("settax")) {
                    Help.showHelp("set_tax", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("setdefaultshop")) {
                    temp.addAll(ShopUtil.shopConfigFiles.keySet())
                    AddToAutoCompleteIfValid(args[1])
                    Help.showHelp("set_default_shop", sender as Player, args)
                } else if (args[0].equalsIgnoreCase("deleteOldUser")) {
                    Help.showHelp("delete_old_user", sender as Player, args)
                }
                return autoCompleteList
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun AddToAutoCompleteIfValid(arg: String) {
        for (s in temp) {
            if (s.toLowerCase().startsWith(arg.toLowerCase())) autoCompleteList.add(s)
        }
    }
}