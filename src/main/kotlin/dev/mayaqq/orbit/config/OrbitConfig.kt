package dev.mayaqq.orbit.config

import com.google.gson.GsonBuilder
import dev.mayaqq.orbit.Orbit
import dev.mayaqq.orbit.data.OrbitButton
import net.fabricmc.loader.api.FabricLoader
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
        if (!configFile.exists()) {
            configFile.createNewFile()
            save()
        } else {
            FileReader(configFile).use {
                val json = gson.fromJson(it, Config::class.java)
                CONFIG = json
            }
        }
        if (!buttons.exists()) {
            buttons.createNewFile()
            save()
        } else {
            FileReader(buttons).use {
                BUTTONS = gson.fromJson(it, Array<OrbitButton>::class.java).toList()
            }
        }

        // populate BUTTONS with default buttons if it's empty
        val defaultButtons = List(CONFIG.buttonCount) { OrbitButton(it) }
        if (BUTTONS.isEmpty()) {
            BUTTONS = defaultButtons
        } else if (BUTTONS.size < CONFIG.buttonCount) {
            BUTTONS = BUTTONS + defaultButtons.subList(BUTTONS.size, CONFIG.buttonCount)
        }
        Orbit.buttons = BUTTONS.subList(0, CONFIG.buttonCount)
    }

    fun save() {
        val buttonList = BUTTONS.toMutableList()
        Orbit.buttons.forEachIndexed { index, button ->
            buttonList[index] = button
        }
        BUTTONS = buttonList

        FileWriter(configFile).use {
            gson.toJson(CONFIG, it)
        }
        FileWriter(buttons).use { writer ->
            gson.toJson(BUTTONS, writer)
        }
    }

    fun saveAndLoad() {
        save()
        load()
    }

    data class Config(var buttonCount: Int = 8)
}