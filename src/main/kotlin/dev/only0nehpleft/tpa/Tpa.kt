package dev.only0nehpleft.tpa

import PersistenceManager
import dev.only0nehpleft.tpa.commands.TpaCMD
import dev.only0nehpleft.tpa.listener.PlayerJoinListener
import dev.only0nehpleft.tpa.managers.Effects
import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.EffectMenu
import dev.only0nehpleft.tpa.menus.RequestMenu
import dev.only0nehpleft.tpa.menus.SelectionMenu
import org.bukkit.plugin.java.JavaPlugin

class Tpa : JavaPlugin() {

    private lateinit var selectionMenu: SelectionMenu
    private lateinit var requestMenu: RequestMenu
    private lateinit var requestManager: RequestManager
    private lateinit var effects: Effects
    private lateinit var effectMenu: EffectMenu
    private lateinit var persistenceManager: PersistenceManager

    override fun onEnable() {
        requestManager = RequestManager(this)
        persistenceManager = PersistenceManager(this)
        effects = Effects(this, persistenceManager)

        persistenceManager.loadEffects(effects)

        selectionMenu = SelectionMenu(this, requestManager, effects)
        requestMenu = RequestMenu(this, requestManager, selectionMenu, effects)
        effectMenu = EffectMenu(this, effects)

        registerListeners()
        registerCommands()
        registerEvents()
    }

    override fun onDisable() {
        persistenceManager.saveEffects(effects)
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
        server.pluginManager.registerEvents(PlayerJoinListener(this, effects), this)
    }
}