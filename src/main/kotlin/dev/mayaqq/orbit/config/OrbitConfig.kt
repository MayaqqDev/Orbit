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
                val array = gson.fromJson(it, Array<OrbitButton>::class.java)
                Orbit.buttons = Array(CONFIG.buttonCount) { index ->
                    if (index < array.size) array[index] else OrbitButton()
                }
            }
        }
    }

    fun save() {
        FileWriter(buttons).use {
            gson.toJson(Orbit.buttons, it)
        }
        FileWriter(configFile).use {
            gson.toJson(CONFIG, it)
        }
    }

    data class Config(var buttonCount: Int = 8)
}