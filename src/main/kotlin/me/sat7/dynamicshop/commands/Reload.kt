package me.sat7.dynamicshop.commands

import me.sat7.dynamicshop.guis.QuickSell

class Reload : DSCMD() {
    init {
        inGameUseOnly = false
        permission = P_ADMIN_RELOAD
        validArgCount.add(1)
    }

    @Override
    override fun SendHelpMessage(player: Player) {
        player.sendMessage(DynamicShop.dsPrefix(player) + t(player, "HELP.TITLE").replace("{command}", "reload"))
        player.sendMessage(" - " + t(player, "HELP.USAGE") + ": /ds reload")
        player.sendMessage("")
    }

    @Override
    override fun RunCMD(args: Array<String?>?, sender: CommandSender) {
        if (!CheckValid(args, sender)) return
        LangUtil.ccLang.reload()
        LayoutUtil.ccLayout.reload()
        LayoutUtil.Setup()
        ShopUtil.Reload()
        StartPage.ccStartPage.reload()
        DynamicShop.ccSign.reload()
        WorthUtil.ccWorth.reload()
        WorthUtil.setupWorthFile()
        SoundUtil.ccSound.reload()
        SoundUtil.setupSoundFile()
        DynamicShop.plugin.reloadConfig()
        ConfigUtil.configSetup(DynamicShop.plugin)
        DynamicShop.plugin.PeriodicRepetitiveTask()
        DynamicShop.plugin.startCullLogsTask()
        QuickSell.quickSellGui.reload()
        QuickSell.SetupQuickSellGUIFile()
        LangUtil.setupLangFile(DynamicShop.plugin.getConfig().getString("Language"))
        DynamicShop.plugin.getConfig().set("Version", configVersion)
        sender.sendMessage(DynamicShop.dsPrefix(sender) + t(sender, "HELP.RELOADED"))
    }
}