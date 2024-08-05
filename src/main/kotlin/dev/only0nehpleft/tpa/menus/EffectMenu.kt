package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.managers.EffectManager
import dev.only0nehpleft.tpa.managers.EffectType
import dev.only0nehpleft.tpa.menus.EffectSlots.Companion.closeItem
import dev.only0nehpleft.tpa.menus.EffectSlots.Companion.goBackItem
import dev.only0nehpleft.tpa.menus.EffectSlots.Companion.paneItem
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class EffectMenu(
    private val plugin: JavaPlugin,
    private val effectManager: EffectManager,
) : Listener {

    private val guiName = "Tpa Effects"
    private val guiSize = 54

    private val goBackSlot = 48
    private val closeSlot = 49

    private val effectSlots = mapOf(
        10 to EffectType.PORTAL,
        11 to EffectType.LIGHTNING,
        12 to EffectType.SPARK,
        13 to EffectType.FIREWORK,
        14 to EffectType.SMOKE
    )

    private val paneSlots = listOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 17,
        18, 26,
        27, 35,
        36, 44,
        45, 46, 47, 50, 51, 52, 53
    )

    fun openEffectsMenu(player: Player) {
        val inventory = Bukkit.createInventory(player, guiSize, guiName)

        inventory.setItem(goBackSlot, goBackItem)
        inventory.setItem(closeSlot, closeItem)

        paneSlots.forEach { slot ->
            inventory.setItem(slot, paneItem)
        }

        effectSlots.forEach { (slot, effectType) ->
            val enabled = effectManager.playerEffects[player]?.contains(effectType) == true
            val item = EffectSlots.createEffectItem(player, effectType, enabled)
            inventory.setItem(slot, item)
        }

        player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (event.view.title != guiName) {
            return
        }

        event.isCancelled = true
        val effects = effectManager.playerEffects[player]?.toMutableSet() ?: mutableSetOf()

        when (event.slot) {
            closeSlot -> {
                player.closeInventory()
            }

            goBackSlot -> {
                player.performCommand("tpa requests")
            }

            else -> {
                val effectType = effectSlots[event.slot] ?: return
                if (effects.contains(effectType)) {
                    effects.remove(effectType)
                } else {
                    effects.add(effectType)
                }
                effectManager.setPlayerEffects(player, effects)
                openEffectsMenu(player)
            }
        }
    }
}