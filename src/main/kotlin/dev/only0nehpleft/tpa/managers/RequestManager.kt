package dev.only0nehpleft.tpa.managers

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class RequestManager(private val plugin: JavaPlugin) {

    private val requestFile: File = File(plugin.dataFolder, "requests.yml")
    private val playersFile: File = File(plugin.dataFolder, "players.yml")
    private val requestConfig: FileConfiguration = YamlConfiguration.loadConfiguration(requestFile)
    private val playersConfig: FileConfiguration = YamlConfiguration.loadConfiguration(playersFile)

    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        ensureFileExists(requestFile) {
            requestConfig.createSection("requests")
            saveConfig(requestConfig, requestFile)
        }

        ensureFileExists(playersFile) {
            playersConfig.createSection("players")
            savePlayersConfig()
        }
    }

    private fun ensureFileExists(file: File, init: () -> Unit) {
        if (!file.exists()) {
            try {
                file.createNewFile()
                init()
            } catch (e: IOException) {
                plugin.logger.severe("Failed to create or access ${file.name}: ${e.message}")
            }
        }
    }

    private fun savePlayersConfig() {
        saveConfig(playersConfig, playersFile)
    }

    private fun saveConfig(config: FileConfiguration, file: File) {
        try {
            config.save(file)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to save ${file.name}: ${e.message}")
        }
    }

    fun toggleAcceptingRequests(player: Player): Boolean {
        val currentState = isAcceptingRequests(player)
        playersConfig.set("players.${player.uniqueId}.accepting_requests", !currentState)
        savePlayersConfig()
        return !currentState
    }

    fun isAcceptingRequests(player: Player): Boolean {
        return playersConfig.getBoolean("players.${player.uniqueId}.accepting_requests", true)
    }

    fun storeRequest(uniqueId: String, targetPlayer: Player, requester: Player) {
        val requestTimeMillis = System.currentTimeMillis()
        requestConfig.set("requests.$uniqueId.target", targetPlayer.name)
        requestConfig.set("requests.$uniqueId.requester", requester.name)
        requestConfig.set("requests.$uniqueId.timeMillis", requestTimeMillis)
        try {
            requestConfig.save(requestFile)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to save request $uniqueId: ${e.message}")
        }
    }

    fun removeRequest(uniqueId: String) {
        requestConfig.set("requests.$uniqueId", null)
        try {
            requestConfig.save(requestFile)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to remove request $uniqueId: ${e.message}")
        }
    }

    fun clearAllRequests() {
        requestConfig.set("requests", null)
        try {
            requestConfig.save(requestFile)
        } catch (e: IOException) {
            plugin.logger.severe("Failed to clear all requests: ${e.message}")
        }
    }

    fun getSentRequests(player: Player): List<Pair<String, Triple<String, String, Long>>> {
        val sentRequests = mutableListOf<Pair<String, Triple<String, String, Long>>>()
        requestConfig.getConfigurationSection("requests")?.getKeys(false)?.forEach { requestId ->
            val targetPlayerName = requestConfig.getString("requests.$requestId.target")
            val requesterName = requestConfig.getString("requests.$requestId.requester")
            val requestTimeMillis = requestConfig.getLong("requests.$requestId.timeMillis")

            if (requesterName == player.name) {
                sentRequests.add(Pair(requestId, Triple(targetPlayerName!!, requesterName, requestTimeMillis)))
            }
        }
        return sentRequests
    }

    fun getReceivedRequests(player: Player): List<Pair<String, Triple<String, String, Long>>> {
        val receivedRequests = mutableListOf<Pair<String, Triple<String, String, Long>>>()
        requestConfig.getConfigurationSection("requests")?.getKeys(false)?.forEach { requestId ->
            val targetPlayerName = requestConfig.getString("requests.$requestId.target")
            val requesterName = requestConfig.getString("requests.$requestId.requester")
            val requestTimeMillis = requestConfig.getLong("requests.$requestId.timeMillis")

            if (targetPlayerName == player.name) {
                receivedRequests.add(Pair(requestId, Triple(targetPlayerName, requesterName!!, requestTimeMillis)))
            }
        }
        return receivedRequests
    }

    fun hasPendingRequestBetween(player1: Player, player2: Player): Boolean {
        return requestConfig.getConfigurationSection("requests")?.getKeys(false)?.any { requestId ->
            val targetPlayerName = requestConfig.getString("requests.$requestId.target")
            val requesterName = requestConfig.getString("requests.$requestId.requester")
            (targetPlayerName == player1.name && requesterName == player2.name) ||
                    (targetPlayerName == player2.name && requesterName == player1.name)
        } == true
    }
}