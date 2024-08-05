package dev.only0nehpleft.tpa.managers

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class EffectManager(
    private val plugin: JavaPlugin
) {

    private val defaultEffects: Set<EffectType> = setOf(EffectType.PORTAL)
    val playerEffects: MutableMap<Player, Set<EffectType>> = mutableMapOf()

    fun setPlayerEffects(player: Player, effects: Set<EffectType>) {
        playerEffects[player] = effects
    }

    fun setDefaultEffect(player: Player) {
        if (!playerEffects.containsKey(player)) {
            playerEffects[player] = defaultEffects
        }
    }

    fun playTeleportEffects(player: Player) {
        val effects = playerEffects[player] ?: return
        effects.forEach { effectType ->
            when (effectType) {
                EffectType.PORTAL -> playPortalEffect(player)
                EffectType.LIGHTNING -> playLightningEffect(player)
                EffectType.SPARK -> playSparkEffect(player)
                EffectType.FIREWORK -> playFireworkEffect(player)
                EffectType.SMOKE -> playSmokeEffect(player)
            }
        }
    }

    // Effects

    private fun playPortalEffect(player: Player) {
        val location = player.location
        location.world?.spawnParticle(Particle.PORTAL, location, 100, 1.0, 1.0, 1.0, 0.1)
        playTeleportSound(player)
    }

    private fun playLightningEffect(player: Player) {
        val location = player.location
        location.world?.strikeLightningEffect(location)
        playLightingSound(player)
    }

    private fun playSparkEffect(player: Player) {
        val location = player.location
        location.world?.spawnParticle(Particle.ELECTRIC_SPARK, location, 100, 1.0, 1.0, 1.0, 0.1)
        playSparkSound(player)
    }

    private fun playFireworkEffect(player: Player) {
        val location = player.location
        location.world?.spawnParticle(Particle.FIREWORK, location, 100, 1.0, 1.0, 1.0, 0.1)
        playFireworkSound(player)
    }

    private fun playSmokeEffect(player: Player) {
        val location = player.location
        location.world?.spawnParticle(Particle.LARGE_SMOKE, location, 100, 1.0, 1.0, 1.0, 0.1)
        playSmokeSound(player)
    }

    // Sounds

    private fun playTeleportSound(player: Player) {
        val location = player.location
        location.world?.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
    }

    private fun playFireworkSound(player: Player) {
        val location = player.location
        location.world?.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f)
    }

    private fun playLightingSound(player: Player) {
        val location = player.location
        location.world?.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f)
    }

    private fun playSparkSound(player: Player) {
        val location = player.location
        location.world?.playSound(location, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 1.0f, 1.0f)
    }

    private fun playSmokeSound(player: Player) {
        val location = player.location
        location.world?.playSound(location, Sound.BLOCK_CAMPFIRE_CRACKLE, 1.0f, 1.0f)
    }
}