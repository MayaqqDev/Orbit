package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.utils.press
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

open class ControlsPassthroughScreen(name: Component) : Screen(name) {
    private val movementKeys by lazy {
        val options = Minecraft.getInstance().options

        listOf(
            options.keyLeft,
            options.keyRight,
            options.keyUp,
            options.keyDown,
            options.keyJump,
            options.keySprint
        ).associateBy<KeyMapping, Int> { it.key.value }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val mapping = movementKeys[keyCode] ?: return super.keyPressed(keyCode, scanCode, modifiers)
        mapping.press()
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val mapping = movementKeys[keyCode] ?: return super.keyPressed(keyCode, scanCode, modifiers)
        mapping.release()
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}