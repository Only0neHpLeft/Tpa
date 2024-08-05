package dev.only0nehpleft.tpa.menus

import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.RequestSlots.Companion.closeItem
import dev.only0nehpleft.tpa.menus.RequestSlots.Companion.createRequest
import dev.only0nehpleft.tpa.menus.RequestSlots.Companion.effectsItem
import dev.only0nehpleft.tpa.menus.RequestSlots.Companion.refreshItem
import dev.only0nehpleft.tpa.menus.SelectionSlots.Companion.paneItem
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.plugin.java.JavaPlugin

class RequestMenu(
    private val plugin: JavaPlugin,
    private val requestManager: RequestManager,
    private val selectionMenu: SelectionMenu
) : Listener {

    private val guiName = "Tpa Requests"
    private val guiSize = 54

    private val refreshSlot = 4
    private val closeSlot = 49
    private val effectsSlot = 53

    private val requestSlots = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43,
    )

    private val paneSlots = listOf(
        0, 1, 2, 3, 5, 6, 7, 8,
        9, 17,
        18, 26,
        27, 35,
        36, 44,
        45, 46, 47, 48, 50, 51, 52,
    )

    fun openRequestMenu(player: Player) {
        val inventory = Bukkit.createInventory(player, guiSize, guiName)

        inventory.setItem(closeSlot, closeItem)
        inventory.setItem(refreshSlot, refreshItem)
        inventory.setItem(effectsSlot, effectsItem)

        paneSlots.forEach { slot ->
            inventory.setItem(slot, paneItem)
        }

        val receivedRequests = requestManager.getReceivedRequests(player)

        for ((index, slot) in requestSlots.withIndex()) {
            val (_, request) = receivedRequests.getOrNull(index) ?: break
            val (_, requesterName, requestTimeMillis) = request
            val requester = Bukkit.getPlayer(requesterName)
            if (requester != null) {
                val item = createRequest(index + 1, requester, requestTimeMillis)
                inventory.setItem(slot, item)
            }
        }

        player.openInventory(inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (event.view.title != guiName || event.inventory.holder != player) {
            return
        }

        event.isCancelled = true

        when (event.slot) {
            closeSlot -> {
                player.closeInventory()
            }
            refreshSlot -> {
                openRequestMenu(player)
            }
            effectsSlot -> {
                player.performCommand("tpa effects")
            }
            in requestSlots -> {
                val requestIndex = requestSlots.indexOf(event.slot)
                val requestId = requestManager.getReceivedRequests(player).getOrNull(requestIndex)?.first ?: return
                val requesterName = requestManager.getReceivedRequests(player).find { it.first == requestId }?.second?.second ?: return
                val requester = Bukkit.getPlayer(requesterName)
                if (requester != null) {
                    selectionMenu.openMenu(player, requester, requestId)
                }
            }
        }
    }
}