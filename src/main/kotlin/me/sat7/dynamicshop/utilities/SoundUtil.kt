package me.sat7.dynamicshop.utilities

import org.bukkit.Sound

object SoundUtil {
    var ccSound: CustomConfig? = null

    // 소리 재생
    fun playerSoundEffect(player: Player, key: String) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(ccSound.get().getString(key)), 1, 1)
        } catch (e: Exception) {
            if (ccSound.get().contains(key)) {
                if (ccSound.get().getString(key).length() > 1) {
                    DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sound play failed: " + key + "/" + ccSound.get().getString(key))
                }
            } else {
                DynamicShop.console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Sound play failed. Path is missing: " + key)
            }
        }
    }

    fun setupSoundFile() {
        ccSound.setup("Sound", null)
        ccSound.get().options().header("Enter 0 to mute.\nhttps://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html")
        ccSound.get().addDefault("sell", "ENTITY_EXPERIENCE_ORB_PICKUP")
        ccSound.get().addDefault("buy", "ENTITY_EXPERIENCE_ORB_PICKUP")
        ccSound.get().addDefault("editItem", "ENTITY_PAINTING_PLACE")
        ccSound.get().addDefault("deleteItem", "BLOCK_GRAVEL_BREAK")
        ccSound.get().addDefault("addItem", "BLOCK_GRAVEL_PLACE")
        ccSound.get().addDefault("click", "BLOCK_METAL_STEP")
        ccSound.get().addDefault("tradeview", "ENTITY_CHICKEN_EGG")
        ccSound.get().options().copyDefaults(true)
        ccSound.save()
    }
}