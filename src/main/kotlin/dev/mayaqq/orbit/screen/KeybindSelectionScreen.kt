package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.config.OrbitConfig
import java.awt.Color
import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.utils.McClient
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIConstants
import kotlin.math.min

class KeybindSelectionScreen(orbitButton: OrbitButton) : OrbitBaseScreen(orbitButton, Text.trans("orbit.screen.keybindselect")) {

    private var categoryMap = mutableMapOf<String, MutableList<Button>>()
    val list = ListWidget(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)

    override fun populate() {
        list.setSize(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)

        McClient.options.keyMappings.forEach {
            val button = Widgets.button()
            button.setSize(min(layout.width, 200), 20)
            button.withTexture(UIConstants.BUTTON)
            button.withRenderer(WidgetRenderers.text(Text.trans(it.name)))
            button.withCallback {
                orbitButton.actionString = it.name
                this.onClose()
            }

            val mapList = categoryMap.getOrDefault(it.category, mutableListOf())
            mapList.add(button)
            categoryMap[it.category] = mapList
        }

        categoryMap.forEach { (category, buttons) ->
            val seperatorWidget = Widgets.text(Text.trans(category).withColor(Color.decode("#D0D1D4").rgb))
            seperatorWidget.setSize(min(layout.width, 200), 15)
            seperatorWidget.withCenterAlignment()
            list.add(seperatorWidget)
            buttons.forEach { list.add(it) }
        }

        layout.addToContents(list)
    }

    override fun repositionElements() {
        super.repositionElements()
        list.setSize(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)
    }

    override fun onClose() {
        OrbitConfig.save()
        McClient.setScreen(ConfigurationScreen(orbitButton))
    }
}