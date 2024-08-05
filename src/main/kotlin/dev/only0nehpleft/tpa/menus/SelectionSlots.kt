package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.handlers.Utils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SelectionSlots {

    companion object {
        private fun createItem(material: Material, name: String, lore: List<String>): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta?.setDisplayName(Utils.color(name))
            meta?.lore = lore.map { Utils.color(it) }
            item.itemMeta = meta
            return item
        }

        val acceptItem = createItem(
            Material.LIME_STAINED_GLASS_PANE,
            "§aAccept Request",
            listOf()
        )

        val rejectItem = createItem(
            Material.RED_STAINED_GLASS_PANE,
            "§cReject Request",
            listOf()
        )

        val paneItem = createItem(
            Material.GRAY_STAINED_GLASS_PANE,
            "§7",
            listOf()
        )
    }
}