package dev.mayaqq.orbit.data

import dev.mayaqq.orbit.Orbit
import dev.mayaqq.orbit.utils.press
import dev.mayaqq.orbit.utils.value
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

data class OrbitButton(
    var iconItem: String = "",
    var iconType: IconType = IconType.ITEM,
    var action: OrbitButtonAction = OrbitButtonAction.NONE,
    var actionString: String = "",
) {
    fun iconLocation(): Identifier? {
        return iconItem.takeIf { it.isNotBlank() }?.let(Identifier::tryParse)
    }

    fun item(): ItemStack {
        val iconId = iconLocation() ?: return Items.BARRIER.defaultInstance
        BuiltInRegistries.ITEM.getOptional(iconId).value()?.let {
            return ItemStack(it)
        }
        return Items.BARRIER.defaultInstance
    }

    fun execute() {
        when (action) {
            OrbitButtonAction.NONE -> {}
            OrbitButtonAction.RUN_COMMAND -> {
                val text = actionString.trim()
                val connection = Minecraft.getInstance().connection
                if (text.startsWith("/")) {
                    val command = text.removePrefix("/").trim()
                    if (command.isNotEmpty()) {
                        connection?.sendCommand(command)
                    }
                } else if (text.isNotEmpty()) {
                    connection?.sendChat(text)
                }
            }
            OrbitButtonAction.PRESS_KEY -> {
                Minecraft.getInstance().options.keyMappings.firstOrNull { it.name == actionString }?.let {
                    it.press()
                    Orbit.scheduled.add(Orbit.ScheduledTask(1) {
                        it.release()
                    })
                }
            }
        }
    }
}

enum class OrbitButtonAction(val transkey: String) {
    NONE("orbit.action.none"),
    RUN_COMMAND("orbit.action.run_command"),
    PRESS_KEY("orbit.action.press_key")
}

enum class IconType(val transkey: String) {
    ITEM("orbit.icontype.item"),
    TEXTURE("orbit.icontype.texture")
}
