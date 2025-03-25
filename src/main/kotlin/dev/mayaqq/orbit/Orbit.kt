package dev.mayaqq.orbit

import dev.mayaqq.orbit.config.OrbitConfig
import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.screen.OrbitMenu
import net.fabricmc.api.ClientModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW

const val MODID = "orbit"
const val MODNAME = "Orbit"

object Orbit : ClientModInitializer, Logger by LoggerFactory.getLogger(MODNAME) {

    val scheduled = ConcurrentLinkedQueue<ScheduledTask>()

    var buttons: Array<OrbitButton> = Array<OrbitButton>(8) { OrbitButton() }

    val ORBIT: KeyMapping = KeyBindingHelper.registerKeyBinding(
        KeyMapping(
            "key.orbit.orbit",
            GLFW.GLFW_KEY_Y,
            "key.category.orbit"
        )
    )

    override fun onInitializeClient() {
        info("Orbiting your Cursor")
        OrbitConfig.load()
        ClientTickEvents.START_CLIENT_TICK.register {
            scheduled.forEach { task ->
                if (task.ticker > 0) {
                    task.ticker--
                } else {
                    task.run()
                    scheduled.remove(task)
                }
            }
        }
        ClientTickEvents.END_CLIENT_TICK.register {
            if (ORBIT.isDown) {
                Minecraft.getInstance().schedule {
                    Minecraft.getInstance().setScreen(OrbitMenu())
                }
            }
        }
    }

    class ScheduledTask(var ticker: Int, var action: () -> Unit) : Runnable {
        override fun run() {
            action()
        }
    }
}
