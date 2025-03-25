package dev.mayaqq.orbit.screen

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import com.teamresourceful.resourcefullib.common.color.Color
import dev.mayaqq.orbit.config.OrbitConfig
import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.base.ListWidget
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.ui.UIConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.network.chat.CommonComponents
import kotlin.math.min

class KeybindSelectionScreen(private val orbitButton: OrbitButton) : BaseCursorScreen(Text.trans("titanomachy.screen.keybindselect")) {

    private val layout: HeaderAndFooterLayout = HeaderAndFooterLayout(this)

    private var categoryMap = mutableMapOf<String, MutableList<earth.terrarium.olympus.client.components.buttons.Button>>()
    val list = ListWidget(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)

    override fun init() {
        layout.addTitleHeader(this.title, Minecraft.getInstance().font)

        list.setSize(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)

        Minecraft.getInstance().options.keyMappings.forEach {
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
            val seperatorWidget = Widgets.text(Text.trans(category).withColor(Color.parse("#D0D1D4").value))
            seperatorWidget.setSize(min(layout.width, 200), 15)
            seperatorWidget.withCenterAlignment()
            list.add(seperatorWidget)
            buttons.forEach { list.add(it) }
        }

        layout.addToContents(list)

        layout.addToFooter(
            Button.builder(
                CommonComponents.GUI_DONE
            ) { button: Button? -> this.onClose() }.width(200).build()
        )

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun onClose() {
        super.onClose()
        OrbitConfig.save()
    }

    override fun repositionElements() {
        layout.arrangeElements()
        list.setSize(min(layout.width, 200), layout.height - layout.headerHeight - layout.footerHeight)
    }
}