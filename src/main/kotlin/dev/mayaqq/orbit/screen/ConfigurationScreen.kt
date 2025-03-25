package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.data.OrbitButtonAction
import dev.mayaqq.orbit.utils.Text
import earth.terrarium.olympus.client.components.Widgets
import earth.terrarium.olympus.client.components.Widgets.button
import earth.terrarium.olympus.client.components.buttons.Button
import earth.terrarium.olympus.client.components.dropdown.DropdownState
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers
import earth.terrarium.olympus.client.layouts.Layouts
import earth.terrarium.olympus.client.ui.OverlayAlignment
import earth.terrarium.olympus.client.ui.UIConstants
import earth.terrarium.olympus.client.utils.State
import net.minecraft.client.Minecraft

class ConfigurationScreen(button: OrbitButton) : OrbitBaseScreen(button, Text.trans("orbit.screen.configure")) {

    private var action = button.action
    private var icon = button.iconItem
    private var actionString = button.actionString

    private val iconState = State<String>.of(icon)
    private val iconWidget = Widgets.textInput(iconState)

    private val actionStringState = State<String>.of(actionString)
    private val actionStringWidget = Widgets.textInput(actionStringState)

    override fun populate() {
        val column = Layouts.column()

        val dropdownState = DropdownState<OrbitButtonAction>.of<OrbitButtonAction>(action)
        val mode = Widgets.dropdown(
            dropdownState,
            OrbitButtonAction.entries,
            { action ->
                Text.of(action.name)
            },
            { button -> button.withSize(200, 20) },
            { builder ->
                builder.withCallback {
                    action = it
                    this.rebuildWidgets()
                }
                builder.withAlignment(OverlayAlignment.TOP_LEFT)
            }
        )
        column.withChild(mode)

        iconWidget.withSize(200, 20)
        iconWidget.withPlaceholder("Item ID")
        column.withChild(iconWidget)

        actionStringWidget.withPlaceholder("Command / Keybind")
        actionStringWidget.withSize(200, 20)
        column.withChild(actionStringWidget)

        val keybindButton = button()
        keybindButton.setSize(200, 20)
        keybindButton.withTexture(UIConstants.BUTTON)
        keybindButton.withRenderer(WidgetRenderers.text<Button>(Text.of("Select Keybind")).withCenterAlignment())
        keybindButton.withCallback {
            save()
            Minecraft.getInstance().setScreen(KeybindSelectionScreen(orbitButton))
        }
        if (action != OrbitButtonAction.PRESS_KEY) {
            keybindButton.asDisabled()
        }
        column.withChild(keybindButton)
        column.arrangeElements()
        layout.addToContents(column)
    }

    override fun onClose() {
        save()
        super.onClose()
    }

    fun save() {
        actionString = actionStringWidget.value
        icon = iconWidget.value
        orbitButton.action = action
        orbitButton.iconItem = icon
        orbitButton.actionString = actionString
    }
}