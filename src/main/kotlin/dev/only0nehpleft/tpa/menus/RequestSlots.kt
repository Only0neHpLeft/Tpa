package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.handlers.Utils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class RequestSlots {

    companion object {
        private fun createItem(material: Material, name: String, lore: List<String>): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta?.setDisplayName(Utils.color(name))
            meta?.lore = lore.map { Utils.color(it) }
            item.itemMeta = meta
            return item
        }

        private fun createCustomHead(player: Player, name: String, lore: List<String>): ItemStack {
            val item = ItemStack(Material.PLAYER_HEAD, 1)
            val meta = item.itemMeta as? SkullMeta ?: return item
            meta.owningPlayer = player
            meta.setDisplayName(Utils.color(name))
            meta.lore = lore.map { Utils.color(it) }
            item.itemMeta = meta
            return item
        }

        fun createRequest(requestPosition: Int, requester: Player, requestTimeMillis: Long): ItemStack {
            val worldName = when (requester.world.name) {
                "world" -> "§aOverworld"
                "world_nether" -> "§cNether"
                "world_the_end" -> "§5The End"
                else -> "§7Unknown"
            }

            fun convertRealTime(requestTimeMillis: Long): String {
                val currentTimeMillis = System.currentTimeMillis()
                val elapsedMillis = currentTimeMillis - requestTimeMillis

                val seconds = (elapsedMillis / 1000).toInt()
                val hours = (seconds / 3600).toInt()
                val minutes = ((seconds % 3600) / 60).toInt()
                val secondsRemaining = (seconds % 60).toInt()

                val hourColor = if (hours > 0) "§a" else "§7"
                val minuteColor = if (minutes > 0 || hours > 0) "§a" else "§7"
                val secondColor = "§a"

                return "${hourColor}%d h, ${minuteColor}%d m, ${secondColor}%d s".format(hours, minutes, secondsRemaining)
            }

            val timeSinceRequest = convertRealTime(requestTimeMillis)

            val lore = listOf(
                "",
                "§7Requester: §e${requester.name}",
                "",
                "§7World: $worldName",
                "§7Time: $timeSinceRequest"
            )

            return createCustomHead(requester, "§bRequest #$requestPosition", lore)
        }

        val closeItem = createItem(
            Material.BARRIER,
            "§cClose",
            listOf()
        )

        val refreshItem = createItem(
            Material.CLOCK,
            "§bThe Refresher",
            listOf(
                "§dMystical §7item from §3the ancient §7times",
                "§7that allows §ausers §7to §erefresh",
                "§7and §eupdate §7the current state",
                "§7or information in their surroundings.",
                "",
                "§eClick to refresh!"
            )
        )

        val effectsItem = createItem(
            Material.ENDER_PEARL,
            "§bEffects",
            listOf(
                "&#008DFFC&#2088FEu&#3F84FEs&#5F7FFDt&#7E7AFDo&#9E75FCm&#BD71FBi&#DD6CFBz&#FC67FAe §7your teleportation",
                "§7experience with various §5effects.",
                "",
                "§c§lCOMING SOON"
            )
        )
    }
}