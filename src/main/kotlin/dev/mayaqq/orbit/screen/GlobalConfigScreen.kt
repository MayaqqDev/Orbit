package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.config.OrbitConfig
import dev.mayaqq.orbit.utils.McClient
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Style
import java.awt.Color
import kotlin.math.min

class GlobalConfigScreen(val previousScreen: Screen? = null) : Screen(Text.trans("config.orbit.global")) {

    val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)
    var buttonCount = OrbitConfig.CONFIG.buttonCount
    val state = State.of(buttonCount)
    val slider = Widgets.intInput(state)

    override fun init() {
        layout.addTitleHeader(this.title, McClient.font)
        val column = Layouts.column()
        val numInput = Layouts.row()

        slider.withPlaceholder("8")
        slider.withSize(20, 20)

        numInput.withChild(
            Widgets.text(
                Text.trans("config.orbit.global.button_count").withColor(Color.WHITE.rgb)
            )
                .withLeftAlignment()
                .withShadow()
                .withSize(180, 20)
        )
        numInput.withChild(slider)
        column.withChild(numInput)

        layout.addToContents(column)
        layout.addToFooter(
            Widgets.button().withSize(min(layout.width, 200), 20).withTexture(UIConstants.BUTTON).withCallback { this.onClose() }.withRenderer(
                WidgetRenderers.text(CommonComponents.GUI_DONE)
            )
        )

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun onClose() {
        OrbitConfig.CONFIG.buttonCount = slider.value.toInt()
        McClient.setScreen(previousScreen)
        OrbitConfig.saveAndLoad()
    }

    override fun repositionElements() {
        layout.arrangeElements()
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
    }
}