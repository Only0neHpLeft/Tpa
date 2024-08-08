package dev.only0nehpleft.tpa.commands

import dev.only0nehpleft.tpa.managers.RequestManager
import dev.only0nehpleft.tpa.menus.EffectMenu
import dev.only0nehpleft.tpa.menus.RequestMenu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.StringUtil

class TpaCMD(
    private val plugin: JavaPlugin,
    private val requestManager: RequestManager,
    private val requestMenu: RequestMenu,
    private val effectMenu: EffectMenu
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be used by players.")
            return true
        }

        if (!sender.hasPermission("teleportation.tpa")) {
            sender.sendMessage("§cYou do not have permission to use this command.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§aUsage: /tpa <player|requests|effects|toggle>")
            return true
        }

        when (args[0].lowercase()) {
            "requests" -> {
                requestMenu.openRequestMenu(sender)
                return true
            }
            "effects" -> {
                effectMenu.openEffectsMenu(sender)
                return true
            }
            "toggle" -> {
                val newStatus = requestManager.toggleAcceptingRequests(sender)
                sender.sendMessage("§bTeleport Requests §7are now ${if (newStatus) "§aenabled" else "§cdisabled"}.")
                return true
            }
            else -> {
                val targetPlayer = Bukkit.getPlayer(args[0])
                if (targetPlayer == null) {
                    sender.sendMessage("§cPlayer not found.")
                    return true
                }

                val receivedRequests = requestManager.getReceivedRequests(targetPlayer)
                if (receivedRequests.size >= requestMenu.requestSlots.size) {
                    sender.sendMessage("§b${targetPlayer.name} §7has all request slots full.")
                    return true
                }

                if (targetPlayer == sender) {
                    sender.sendMessage("§cYou cannot send a teleport request to yourself.")
                    return true
                }

                if (!requestManager.isAcceptingRequests(targetPlayer)) {
                    sender.sendMessage("§b${targetPlayer.name} §7is not accepting teleport requests.")
                    return true
                }

                if (requestManager.hasPendingRequestBetween(sender, targetPlayer)) {
                    sender.sendMessage("§cYou already have an ongoing request with this player.")
                    return true
                }

                val uniqueId = java.util.UUID.randomUUID().toString()
                val checkRequestsMessage = Component.text("CHECK REQUESTS!")
                    .color(TextColor.color(0xFFFF00))
                    .decoration(TextDecoration.BOLD, true)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa requests"))
                val message = Component.text()
                    .append(Component.text("§b${sender.name} §7has requested to teleport to you. "))
                    .append(checkRequestsMessage)
                    .build()

                targetPlayer.sendMessage(message)
                requestManager.storeRequest(uniqueId, targetPlayer, sender)

                sender.sendMessage("§b${targetPlayer.name} §7has §ereceived §7your teleport request.")

                return true
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val onlinePlayers = Bukkit.getOnlinePlayers().map { it.name }
            val suggestions = listOf("requests", "effects", "toggle") + onlinePlayers
            return suggestions.filter { StringUtil.startsWithIgnoreCase(it, args[0]) }
        }
        return emptyList()
    }
}