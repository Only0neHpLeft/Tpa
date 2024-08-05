package dev.only0nehpleft.tpa

import dev.only0nehpleft.tpa.commands.TpaCMD
import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.RequestMenu
import dev.only0nehpleft.tpa.menus.SelectionMenu
import org.bukkit.plugin.java.JavaPlugin

class Tpa : JavaPlugin() {

    private lateinit var selectionMenu: SelectionMenu
    private lateinit var requestMenu: RequestMenu
    private lateinit var requestManager: RequestManager

    override fun onEnable() {
        requestManager = RequestManager(this)
        selectionMenu = SelectionMenu(this, requestManager)

        requestMenu = RequestMenu(this, requestManager, selectionMenu)

        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        requestManager.clearAllRequests()
    }

    private fun registerCommands() {
        val tpaCommand = getCommand("tpa")
        tpaCommand?.let {
            it.setExecutor(TpaCMD(this, requestManager, requestMenu))
            it.tabCompleter = TpaCMD(this, requestManager, requestMenu)
        }
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(requestMenu, this)
        server.pluginManager.registerEvents(selectionMenu, this)
    }
}