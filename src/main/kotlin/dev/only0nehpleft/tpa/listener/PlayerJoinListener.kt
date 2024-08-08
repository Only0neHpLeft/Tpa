package dev.only0nehpleft.tpa.listener

import dev.only0nehpleft.tpa.managers.Effects
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerJoinListener(
    private val plugin: JavaPlugin,
    private val effects: Effects
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerUUID = player.uniqueId
        if (!effects.playerEffects.containsKey(playerUUID)) {
            effects.setPlayerEffect(player, effects.getDefaultEffect(player))
        }
    }
}