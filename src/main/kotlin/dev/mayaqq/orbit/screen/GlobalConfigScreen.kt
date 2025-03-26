package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.config.OrbitConfig
import dev.mayaqq.orbit.utils.McClient
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.components.textbox.TextBox
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import java.awt.Color
import kotlin.math.min

class GlobalConfigScreen(val previousScreen: Screen? = null) : Screen(Text.trans("config.orbit.global")) {
    private val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)
    private var buttonCount = OrbitConfig.CONFIG.buttonCount

    private val buttonCountState: State<Int> = State.of(buttonCount)
    private val buttonCountWidget: TextBox = Widgets.intInput(buttonCountState)

    override fun init() {
        layout.addTitleHeader(this.title, McClient.font)

        val configColumn = Layouts.column()

        val buttonCountRow = Layouts.row()

        buttonCountWidget.withPlaceholder("8")
        buttonCountWidget.withSize(20, 20)
        buttonCountWidget.withFilter { it.toIntOrNull() != null }

        buttonCountRow.withChild(
            Widgets.text(
                Text.trans("config.orbit.global.button_count").withColor(Color.WHITE.rgb)
            )
                .withLeftAlignment()
                .withShadow()
                .withSize(180, 20)
        )
        buttonCountRow.withChild(buttonCountWidget)

        configColumn.withChild(buttonCountRow)

        layout.addToContents(configColumn)

        layout.addToFooter(
            Widgets.button().withSize(min(layout.width, 200), 20).withTexture(UIConstants.BUTTON).withCallback { this.onClose() }.withRenderer(
                WidgetRenderers.text(CommonComponents.GUI_DONE)
            )
        )

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun onClose() {
        OrbitConfig.CONFIG.buttonCount = buttonCountWidget.value.toInt()
        McClient.setScreen(previousScreen)
        OrbitConfig.saveAndLoad()
    }

    override fun repositionElements() {
        layout.arrangeElements()
    }
}