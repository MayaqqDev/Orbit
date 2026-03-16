package dev.mayaqq.orbit.config

import com.google.gson.GsonBuilder
import dev.mayaqq.orbit.Orbit
import dev.mayaqq.orbit.data.OrbitButton
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

object OrbitConfig {

    private val config = FabricLoader.getInstance().configDir.resolve("orbit")
    private val buttons = config.resolve("buttons.json").toFile()
    private val configFile = config.resolve("config.json").toFile()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    var CONFIG: Config = Config()
    var BUTTONS: List<OrbitButton> = emptyList()

    fun load() {
        if (!config.exists()) config.createDirectory()

        val (loadedConfig, resetConfig) = readConfig()
        val normalizedButtonCount = loadedConfig.buttonCount.coerceAtLeast(0)
        CONFIG = loadedConfig.copy(buttonCount = normalizedButtonCount)

        val (loadedButtons, resetButtons) = readButtons()

        val defaultButtons = List(CONFIG.buttonCount) { OrbitButton() }
        BUTTONS = when {
            loadedButtons.isEmpty() -> defaultButtons
            loadedButtons.size < CONFIG.buttonCount -> loadedButtons + defaultButtons.subList(loadedButtons.size, CONFIG.buttonCount)
            else -> loadedButtons
        }
        Orbit.buttons = BUTTONS.subList(0, CONFIG.buttonCount)

        if (resetConfig || resetButtons || normalizedButtonCount != loadedConfig.buttonCount || BUTTONS != loadedButtons) {
            save()
        }
    }

    fun save() {
        val buttonList = BUTTONS.toMutableList()
        Orbit.buttons.forEachIndexed { index, button ->
            if (index < buttonList.size) {
                buttonList[index] = button
            } else {
                buttonList.add(button)
            }
        }
        BUTTONS = buttonList

        writeJson(configFile) {
            gson.toJson(CONFIG, it)
        }
        writeJson(buttons) { writer ->
            gson.toJson(BUTTONS, writer)
        }
    }

    fun saveAndLoad() {
        save()
        load()
    }

    private fun readConfig(): Pair<Config, Boolean> {
        if (!configFile.exists()) return Config() to true
        return runCatching {
            FileReader(configFile).use {
                gson.fromJson(it, Config::class.java) ?: Config()
            }
        }.fold(
            onSuccess = { loadedConfig -> loadedConfig to false },
            onFailure = {
                Orbit.warn("Failed to read orbit config, resetting to defaults", it)
                Config() to true
            }
        )
    }

    private fun readButtons(): Pair<List<OrbitButton>, Boolean> {
        if (!buttons.exists()) return emptyList<OrbitButton>() to true
        return runCatching {
            FileReader(buttons).use {
                gson.fromJson(it, Array<OrbitButton>::class.java)?.toList().orEmpty()
            }
        }.fold(
            onSuccess = { loadedButtons -> loadedButtons to false },
            onFailure = {
                Orbit.warn("Failed to read orbit buttons, resetting to defaults", it)
                emptyList<OrbitButton>() to true
            }
        )
    }

    private fun writeJson(file: File, write: (FileWriter) -> Unit) {
        FileWriter(file).use(write)
    }

    data class Config(var buttonCount: Int = 8)
}
