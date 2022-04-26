package me.sat7.dynamicshop

import me.clip.placeholderapi.PlaceholderAPI

class DynamicShop : JavaPlugin(), Listener {
    private var periodicRepetitiveTask: BukkitTask? = null
    private var cullLogsTask: BukkitTask? = null
    @Override
    fun onEnable() {
        plugin = this
        console = plugin.getServer().getConsoleSender()
        SetupVault()
    }

    private fun Init() {
        CMDManager.Init()
        registerEvents()
        initCommands()
        makeFolders()
        InitConfig()
        PeriodicRepetitiveTask()
        startCullLogsTask()
        hookIntoJobs()
        InitPapi()

        // 완료
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Enabled! :)")
        CheckUpdate()
        InitBstats()
    }

    // 볼트 이코노미 초기화
    private fun SetupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled due to no Vault dependency found!")
            getServer().getPluginManager().disablePlugin(this)
            return
        } else {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Vault Found")
        }
        SetupRSP()
    }

    private var setupRspRetryCount = 0
    private fun SetupRSP() {
        val rsp: RegisteredServiceProvider<Economy> = getServer().getServicesManager().getRegistration(Economy::class.java)
        if (rsp != null) {
            econ = rsp.getProvider()
            Init()
        } else {
            if (setupRspRetryCount >= 3) {
                console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled due to no Vault dependency found!")
                getServer().getPluginManager().disablePlugin(this)
                return
            }
            setupRspRetryCount++
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Economy provider not found. Retry... " + setupRspRetryCount + "/3")
            Bukkit.getScheduler().runTaskLater(this, { SetupRSP() }, 30L)
        }
    }

    private fun ConvertVersionStringToNumber(string: String): Int {
        val temp: Array<String> = string.replace("-snapshot", "").split("\\.")
        return if (temp.size != 3) 1 else try {
            var ret: Int = Integer.parseInt(temp[0]) * 10000
            ret += Integer.parseInt(temp[1]) * 100
            ret += Integer.parseInt(temp[2])
            ret
        } catch (e: Exception) {
            1
        }
    }

    private fun CheckUpdate() {
        UpdateChecker(this, UpdateChecker.PROJECT_ID).getVersion { version ->
            try {
                lastVersion = version
                yourVersion = getDescription().getVersion()
                val you = ConvertVersionStringToNumber(yourVersion)
                val last = ConvertVersionStringToNumber(lastVersion)
                if (last <= you) {
                    updateAvailable = false
                    console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Plugin is up to date!")
                } else {
                    updateAvailable = true
                    console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Plugin outdated!")
                    console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + UpdateChecker.getResourceUrl())
                }
            } catch (e: Exception) {
                console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Failed to check update. Try again later.")
            }
        }
    }

    private fun InitBstats() {
        try {
            val pluginId = 4258
            val metrics = Metrics(this, pluginId)
        } catch (e: Exception) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + "Failed to Init bstats : " + e)
        }
    }

    private fun InitPapi() {
        isPapiExist = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
        if (isPapiExist) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " PlaceholderAPI Found")
        } else {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " PlaceholderAPI Not Found")
        }
    }

    fun startCullLogsTask() {
        if (getConfig().getBoolean("Log.CullLogs")) {
            if (cullLogsTask != null) {
                cullLogsTask.cancel()
            }
            cullLogsTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, LogUtil::cullLogs, 0L, 20L * 60L * getConfig().getInt("Log.LogCullTimeMinutes") as Long)
        }
    }

    fun PeriodicRepetitiveTask() {
        if (periodicRepetitiveTask != null) {
            periodicRepetitiveTask.cancel()
        }
        periodicRepetitiveTask = Bukkit.getScheduler().runTaskTimer(plugin, { RepeatAction() }, 100, 100) // 1000틱 = 50초/25/12.5
    }

    private var repeatTaskCount = 0
    private fun RepeatAction() {
        repeatTaskCount++

        //SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yyyy,HH.mm.ss");
        //String time = sdf.format(System.currentTimeMillis());
        //console.sendMessage(time + " / " + repeatTaskCount);
        if (repeatTaskCount == 5) {
            ShopUtil.randomChange(Random())
            repeatTaskCount = 0
        }
        UIManager.RefreshUI()
    }

    private fun hookIntoJobs() {
        // Jobs
        if (getServer().getPluginManager().getPlugin("Jobs") == null) {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Not Found")
            JobsHook.jobsRebornActive = false
        } else {
            console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Jobs Reborn Found")
            JobsHook.jobsRebornActive = true
        }
    }

    private fun initCommands() {
        // 명령어 등록 (개별 클레스로 되어있는것들)
        getCommand("DynamicShop").setExecutor(Root())
        getCommand("shop").setExecutor(Optional())

        // 자동완성
        getCommand("DynamicShop").setTabCompleter(this)
        getCommand("shop").setTabCompleter(this)
    }

    private fun registerEvents() {
        getServer().getPluginManager().registerEvents(this, this)
        getServer().getPluginManager().registerEvents(JoinQuit(), this)
        getServer().getPluginManager().registerEvents(OnClick(), this)
        getServer().getPluginManager().registerEvents(OnSignClick(), this)
        getServer().getPluginManager().registerEvents(OnChat(), this)
        uiManager = UIManager()
        getServer().getPluginManager().registerEvents(uiManager, this)
    }

    private fun makeFolders() {
        val shopFolder = File(getDataFolder(), "Shop")
        shopFolder.mkdir() // new 하고 같은줄에서 바로 하면 폴더 안만들어짐.
        val LogFolder = File(getDataFolder(), "Log")
        LogFolder.mkdir()
    }

    private fun InitConfig() {
        LangUtil.ccLang = CustomConfig()
        LayoutUtil.ccLayout = CustomConfig()
        ccUser = CustomConfig()
        StartPage.ccStartPage = CustomConfig()
        ccSign = CustomConfig()
        WorthUtil.ccWorth = CustomConfig()
        SoundUtil.ccSound = CustomConfig()
        LogUtil.ccLog = CustomConfig()
        ShopUtil.Reload()
        ConfigUtil.configSetup(this)
        LangUtil.setupLangFile(getConfig().getString("Language"))
        LayoutUtil.Setup()
        setupUserFile()
        StartPage.setupStartPageFile()
        setupSignFile()
        WorthUtil.setupWorthFile()
        SoundUtil.setupSoundFile()
        LogUtil.setupLogFile()
        QuickSell.quickSellGui = CustomConfig()
        QuickSell.SetupQuickSellGUIFile()
        getConfig().set("Version", configVersion)
        saveConfig()
    }

    private fun setupUserFile() {
        ccUser.setup("User", null)
        ccUser.get().options().copyDefaults(true)
        val userVersion: Int = getConfig().getInt("Version")
        if (userVersion < 3) {
            for (s in ccUser.get().getKeys(false)) {
                ccUser.get().getConfigurationSection(s).set("tmpString", null)
                ccUser.get().getConfigurationSection(s).set("interactItem", null)
            }
        }
        ccUser.save()
    }

    private fun setupSignFile() {
        ccSign.setup("Sign", null)
        ccSign.get().options().copyDefaults(true)
        ccSign.save()
    }

    // 명령어 자동완성
    @Override
    fun onTabComplete(sender: CommandSender?, cmd: Command?, commandLabel: String?, args: Array<String?>?): List<String> {
        return TabCompleteUtil.onTabCompleteBody(this, sender, cmd, args)
    }

    @Override
    fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)
        console.sendMessage(Constants.DYNAMIC_SHOP_PREFIX + " Disabled")
    }

    companion object {
        private var econ: Economy? = null // 볼트에 물려있는 이코노미
        val economy: Economy?
            get() = econ
        var plugin: DynamicShop? = null
        var console: ConsoleCommandSender? = null
        fun dsPrefix(commandSender: CommandSender?): String {
            var player: Player? = null
            if (commandSender is Player) player = commandSender as Player?
            return dsPrefix(player)
        }

        fun dsPrefix(player: Player?): String {
            var temp = dsPrefix_
            if (plugin.getConfig().getBoolean("UI.UseHexColorCode")) temp = LangUtil.TranslateHexColor(temp)
            return if (isPapiExist && player != null && plugin.getConfig().getBoolean("UI.UsePlaceholderAPI")) PlaceholderAPI.setPlaceholders(player, temp) else temp
        }

        var dsPrefix_ = "§3DShop3 §7| §f"
        var ccUser: CustomConfig? = null
        var ccSign: CustomConfig? = null
        var updateAvailable = false
        var lastVersion = ""
        var yourVersion = ""
        var uiManager: UIManager? = null
        val userTempData: HashMap<UUID, String> = HashMap()
        val userInteractItem: HashMap<UUID, String> = HashMap()
        val localeManager: LocaleManager = LocaleManager()
        var isPapiExist = false
        fun CreateLink(text: String?, bold: Boolean, color: ChatColor?, link: String?): TextComponent {
            val component = TextComponent(text)
            component.setBold(bold)
            component.setUnderlined(true)
            component.setColor(color)
            component.setClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, link))
            component.setHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(link).create()))
            return component
        }
    }
}