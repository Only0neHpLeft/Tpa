package dev.only0nehpleft.tpa

import dev.only0nehpleft.tpa.commands.TpaCMD
import dev.only0nehpleft.tpa.listener.PlayerJoinListener
import dev.only0nehpleft.tpa.managers.EffectManager
import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.EffectMenu
import dev.only0nehpleft.tpa.menus.RequestMenu
import dev.only0nehpleft.tpa.menus.SelectionMenu
import org.bukkit.plugin.java.JavaPlugin

class Tpa : JavaPlugin() {

    private lateinit var selectionMenu: SelectionMenu
    private lateinit var requestMenu: RequestMenu
    private lateinit var requestManager: RequestManager
    private lateinit var effectManager: EffectManager
    private lateinit var effectMenu: EffectMenu

    override fun onEnable() {
        requestManager = RequestManager(this)
        effectManager = EffectManager(this)
        selectionMenu = SelectionMenu(this, requestManager, effectManager)
        requestMenu = RequestMenu(this, requestManager, selectionMenu)
        effectMenu = EffectMenu(this, effectManager)

        registerListeners()
        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        requestManager.clearAllRequests()
    }

    private fun registerCommands() {
        val tpaCommand = getCommand("tpa")
        tpaCommand?.let {
            it.setExecutor(TpaCMD(this, requestManager, requestMenu, effectMenu))
            it.tabCompleter = TpaCMD(this, requestManager, requestMenu, effectMenu)
        }
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(requestMenu, this)
        server.pluginManager.registerEvents(selectionMenu, this)
        server.pluginManager.registerEvents(effectMenu, this)
    }

    private fun registerListeners() {
        server.pluginManager.registerEvents(PlayerJoinListener(this, effectManager), this)
    }
}