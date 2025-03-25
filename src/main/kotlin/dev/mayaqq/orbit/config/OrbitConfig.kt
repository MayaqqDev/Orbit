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
    private val file = config.resolve("config.json").toFile()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun load() {
        if (!config.exists()) config.createDirectory()
        if (!file.exists()) {
            file.createNewFile()
            save()
        } else {
            val reader = FileReader(file)
            Orbit.buttons = gson.fromJson(reader, Array<OrbitButton>::class.java)
        }
    }

    fun save() {
        val writer = FileWriter(file)
        gson.toJson(Orbit.buttons, writer)
    }
}