package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.config.OrbitConfig
import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.utils.McClient
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import kotlin.math.min

abstract class OrbitBaseScreen(val orbitButton: OrbitButton, title: Component) : Screen(title) {

    val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)

    override fun init() {
        layout.addTitleHeader(this.title, McClient.font)

        populate()

        layout.addToFooter(
            Widgets.button().withSize(min(layout.width, 200), 20).withTexture(UIConstants.BUTTON).withCallback { this.onClose() }.withRenderer(
                WidgetRenderers.text(CommonComponents.GUI_DONE)
            )
        )

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    abstract fun populate()

    override fun onClose() {
        super.onClose()
        OrbitConfig.save()
    }

    override fun repositionElements() {
        layout.arrangeElements()
    }
}