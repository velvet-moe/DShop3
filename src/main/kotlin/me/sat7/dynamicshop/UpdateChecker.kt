package me.sat7.dynamicshop

import org.bukkit.Bukkit

class UpdateChecker(plugin: JavaPlugin, resourceId: Int) {
    private val plugin: JavaPlugin
    private val resourceId: Int

    init {
        this.plugin = plugin
        this.resourceId = resourceId
    }

    fun getVersion(consumer: Consumer<String?>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin) {
            try {
                URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream().use { inputStream ->
                    Scanner(inputStream).use { scanner ->
                        if (scanner.hasNext()) {
                            consumer.accept(scanner.next())
                        }
                    }
                }
            } catch (exception: IOException) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage())
            }
        }
    }

    companion object {
        const val PROJECT_ID = 65603
        val resourceUrl: String
            get() = "https://spigotmc.org/resources/" + PROJECT_ID
    }
}