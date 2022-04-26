package me.sat7.dynamicshop.files

import me.sat7.dynamicshop.DynamicShop

class CustomConfig {
    private var file: File? = null
    protected var customFile // 버킷의 데이터 타입
            : FileConfiguration? = null

    //Finds or generates the custom config file
    fun setup(name: String, folder: String?) {
        var path = "$name.yml"
        if (folder != null) {
            path = "$folder/$path"
            val directory = File(DynamicShop.plugin.getDataFolder() + "/" + folder)
            if (!directory.exists()) {
                directory.mkdir()
            }
        }
        file = File(DynamicShop.plugin.getDataFolder(), path)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                DynamicShop.console.sendMessage("Fatal error! Config Setup Fail. File name: $name")
                //DynamicShop.console.sendMessage(e.toString());
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file)
    }

    fun open(name: String, folder: String): Boolean {
        file = File(DynamicShop.plugin.getDataFolder(), "$folder/$name.yml")
        if (!file.exists()) {
            DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + name).toString() + " not found")
            return false
        }
        customFile = YamlConfiguration.loadConfiguration(file)
        return true
    }

    fun get(): FileConfiguration? {
        return customFile
    }

    fun save() {
        try {
            customFile.save(file)
        } catch (e: IOException) {
            DynamicShop.console.sendMessage("Couldn't save file :$e")
        }
    }

    fun rename(newName: String?) {
        if (!file.exists()) return
        try {
            val newFile = File(file.toPath().resolveSibling(newName) + ".yml")
            Files.copy(file.toPath(), newFile.toPath())
            file.delete()
            file = newFile
        } catch (e: Exception) {
            System.out.println("rename fail :$e")
        }
    }

    fun delete() {
        if (file.exists()) {
            if (!file.delete()) System.out.println("file delete fail: " + file.getName())
        } else {
            System.out.println("file delete fail: not exist")
        }
    }

    fun reload() {
        customFile = YamlConfiguration.loadConfiguration(file)
    }

    companion object {
        fun GetFileFromPath(name: String, folder: String): FileConfiguration? {
            val tempFile = File(Bukkit.getServer().getPluginManager().getPlugin("DynamicShop").getDataFolder(), "$folder/$name.yml")
            if (!tempFile.exists()) {
                DynamicShop.console.sendMessage((Constants.DYNAMIC_SHOP_PREFIX + name).toString() + " not found")
                return null
            }
            return YamlConfiguration.loadConfiguration(tempFile)
        }
    }
}