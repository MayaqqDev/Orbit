package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.utils.McClient
import dev.mayaqq.orbit.utils.press
import net.minecraft.client.KeyMapping
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component

open class ControlsPassthroughScreen(name: Component) : Screen(name) {
    private val movementKeys by lazy {
        val options = McClient.options

        listOf(
            options.keyLeft,
            options.keyRight,
            options.keyUp,
            options.keyDown,
            options.keyJump,
            options.keySprint
        ).associateBy<KeyMapping, Int> { it.key.value }
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        val mapping = movementKeys[event.key] ?: return super.keyPressed(event)
        mapping.press()
        return super.keyPressed(event)
    }

    override fun keyReleased(event: KeyEvent): Boolean {
        val mapping = movementKeys[event.key] ?: return super.keyPressed(event)
        mapping.release()
        return super.keyPressed(event)
    }
}