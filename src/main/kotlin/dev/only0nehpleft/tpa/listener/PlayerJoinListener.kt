package dev.only0nehpleft.tpa.listener

import dev.only0nehpleft.tpa.managers.EffectManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoinListener(
    private val plugin: JavaPlugin,
    private val effectManager: EffectManager
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        effectManager.setDefaultEffect(player)
    }
}