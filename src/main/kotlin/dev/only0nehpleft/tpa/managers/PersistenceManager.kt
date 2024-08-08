import dev.only0nehpleft.tpa.managers.EffectType
import dev.only0nehpleft.tpa.managers.Effects
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class PersistenceManager(
    private val plugin: JavaPlugin
) {

    private val playersFile = File(plugin.dataFolder, "playereffects.yml")
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(playersFile)

    fun loadEffects(effects: Effects) {
        if (!playersFile.exists()) {
            return
        }
        val effectsConfig = config.getConfigurationSection("effects") ?: return
        effectsConfig.getKeys(false).forEach {
            val uuid = UUID.fromString(it)
            val effectType = EffectType.valueOf(effectsConfig.getString("$it.effect") ?: return@forEach)
            effects.playerEffects[uuid] = effectType
        }
    }

    fun saveEffects(effects: Effects) {
        val effectsConfig = config.createSection("effects")
        effects.playerEffects.forEach { (uuid, effectType) ->
            effectsConfig.set("$uuid.effect", effectType.name)
        }
        config.save(playersFile)
    }
}