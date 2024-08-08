package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.managers.Effects
import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.SelectionSlots.Companion.acceptItem
import dev.only0nehpleft.tpa.menus.SelectionSlots.Companion.paneItem
import dev.only0nehpleft.tpa.menus.SelectionSlots.Companion.rejectItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class SelectionMenu(
    private val plugin: JavaPlugin,
    private val requestManager: RequestManager,
    private val effects: Effects
) : Listener {

    private val guiName = "Confirm Request"
    private val guiSize = 27
    private val rejectSlot = 10
    private val acceptSlot = 16

    private val requestMap = mutableMapOf<Player, String>()

    fun openMenu(player: Player, requester: Player, requestId: String) {
        val inventory = Bukkit.createInventory(player, guiSize, guiName)

        inventory.setItem(rejectSlot, rejectItem)
        inventory.setItem(acceptSlot, acceptItem)

        val paneSlots = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 11, 12, 13, 14, 15, 17,
            18, 19, 20, 21, 22, 23, 24, 25, 26
        )

        for (slot in paneSlots) {
            inventory.setItem(slot, paneItem)
        }

        requestMap[player] = requestId

        player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (event.view.title != guiName || event.inventory.holder != player) {
            return
        }

        event.isCancelled = true
        if (event.currentItem == null || event.currentItem?.type == Material.AIR) {
            return
        }

        when (event.slot) {
            rejectSlot -> {
                val requestId = requestMap[player] ?: return
                requestMap.remove(player)
                requestManager.removeRequest(requestId)
                val requesterName = requestManager.getSentRequests(player).find { it.first == requestId }?.second?.second
                val requester = requesterName?.let { Bukkit.getPlayer(it) }
                player.sendMessage("§cSuccessfully rejected the offer.")
                requester?.sendMessage("§b${player.name} §7has §crejected §7your teleport request.")
                player.closeInventory()
            }
            acceptSlot -> {
                val requestId = requestMap[player] ?: return
                requestMap.remove(player)
                val requestDetails = requestManager.getReceivedRequests(player).find { it.first == requestId }?.second
                val requesterName = requestDetails?.second
                val requester = requesterName?.let { Bukkit.getPlayer(it) }
                if (requester != null) {
                    player.teleport(requester.location)
                    player.sendMessage("§aSuccessfully teleported to §b${requester.name}§a.")
                    requester.sendMessage("§b${player.name} §7has §aaccepted §7your teleport request.")
                    effects.playTeleportEffect(requester)
                    requestManager.removeRequest(requestId)
                }
                player.closeInventory()
            }
        }
    }
}