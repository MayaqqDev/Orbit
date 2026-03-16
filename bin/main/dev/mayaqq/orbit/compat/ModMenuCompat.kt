package dev.mayaqq.orbit.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.mayaqq.orbit.screen.GlobalConfigScreen
import net.minecraft.client.gui.screens.Screen

object ModMenuCompat : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
        return ConfigScreenFactory<Screen?> { screen: Screen? -> GlobalConfigScreen(previousScreen = screen) }
    }
}