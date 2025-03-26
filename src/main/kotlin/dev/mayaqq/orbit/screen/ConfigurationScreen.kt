package dev.mayaqq.orbit.screen

import dev.mayaqq.orbit.data.OrbitButton
import dev.mayaqq.orbit.data.OrbitButtonAction
import dev.mayaqq.orbit.utils.McClient
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

class ConfigurationScreen(button: OrbitButton) : OrbitBaseScreen(button, Text.trans("orbit.screen.configure")) {

    private var action = button.action
    private var icon = button.iconItem
    private var actionString = button.actionString

    private val iconState = State.of(icon)
    private val iconWidget = Widgets.textInput(iconState)

    private val actionStringState = State.of(actionString)
    private val actionStringWidget = Widgets.textInput(actionStringState)

    override fun populate() {
        val column = Layouts.column()

        val dropdownState = DropdownState.of(action)
        val mode = Widgets.dropdown(
            dropdownState,
            OrbitButtonAction.entries,
            { action ->
                Text.trans(action.transkey)
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
        iconWidget.withPlaceholder(Text.trans("orbit.option.item_id").string)
        column.withChild(iconWidget)

        actionStringWidget.withPlaceholder(Text.trans("orbit.option.action").string)
        actionStringWidget.withSize(200, 20)
        column.withChild(actionStringWidget)

        val keybindButton = button()
        keybindButton.setSize(200, 20)
        keybindButton.withTexture(UIConstants.BUTTON)
        keybindButton.withRenderer(WidgetRenderers.text<Button>(Text.trans("orbit.button.select_key")).withCenterAlignment())
        keybindButton.withCallback {
            save()
            McClient.setScreen(KeybindSelectionScreen(orbitButton))
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

    private fun save() {
        actionString = actionStringWidget.value
        icon = iconWidget.value
        orbitButton.action = action
        orbitButton.iconItem = icon
        orbitButton.actionString = actionString
    }
}