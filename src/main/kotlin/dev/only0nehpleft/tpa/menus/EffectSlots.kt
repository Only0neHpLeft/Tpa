package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.handlers.Utils
import dev.only0nehpleft.tpa.managers.EffectType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkMeta

class EffectSlots {

    companion object {

        fun createItem(material: Material, name: String, lore: List<String>): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta?.setDisplayName(Utils.color(name))
            meta?.lore = lore.map { Utils.color(it) }
            item.itemMeta = meta
            return item
        }

        fun createEffectItem(player: Player, effectType: EffectType, enabled: Boolean): ItemStack {
            val material = when (effectType) {
                EffectType.PORTAL -> Material.ENDER_PEARL
                EffectType.LIGHTNING -> Material.LIGHTNING_ROD
                EffectType.SPARK -> Material.LIGHT
                EffectType.FIREWORK -> Material.FIREWORK_ROCKET
                EffectType.SMOKE -> Material.CAMPFIRE
            }

            val status = if (enabled) "§aEnabled" else "§cDisabled"
            val name = "§b${effectType.displayName} Effect"
            val lore = listOf(
                "",
                "§7Status: $status",
                "",
                "§eClick to change!"
            )

            val item = createItem(material, name, lore)
            if (effectType == EffectType.FIREWORK) {
                val fireworkMeta = item.itemMeta as FireworkMeta
                fireworkMeta.power = 0
                item.itemMeta = fireworkMeta
            }

            return item
        }


        val closeItem = createItem(
            Material.BARRIER,
            "§cClose",
            listOf()
        )

        val goBackItem = createItem(
            Material.ARROW,
            "§aGo Back",
            listOf()
        )

        val paneItem = createItem(
            Material.GRAY_STAINED_GLASS_PANE,
            "§7",
            listOf()
        )
    }
}