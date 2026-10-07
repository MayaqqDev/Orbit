package dev.mayaqq.orbit.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.Screen

object McClient {
    val self: Minecraft
        get() = Minecraft.getInstance()

    val font: Font
        get() = self.font

    val gui: Gui
        get() = self.gui

    val screen: Screen?
        get() = gui.screen()

    val options: Options
        get() = self.options

    fun tell(unit: () -> Unit) {
        self.schedule(unit)
    }

    fun setScreen(screen: Screen?) {
        self.gui.setScreen(screen)
    }
}