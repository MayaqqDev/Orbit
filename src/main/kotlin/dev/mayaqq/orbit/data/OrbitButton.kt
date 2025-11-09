package dev.mayaqq.orbit.data

import dev.mayaqq.orbit.Orbit
import dev.mayaqq.orbit.utils.value
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

data class OrbitButton(
    var iconItem: String = "",
    var iconType: IconType = IconType.ITEM,
    var action: OrbitButtonAction = OrbitButtonAction.NONE,
    var actionString: String = "",
) {
    fun item() : ItemStack {
        BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(iconItem))?.value()?.let {
            return ItemStack(it)
        } ?: run {

        }
        return Items.BARRIER.defaultInstance
    }

    fun execute() {
        when (action) {
            OrbitButtonAction.NONE -> {}
            OrbitButtonAction.RUN_COMMAND -> {
                if (actionString.startsWith("/")) {
                    actionString = actionString.substring(1)
                }
                Minecraft.getInstance().connection?.sendCommand(actionString)
            }
            OrbitButtonAction.PRESS_KEY -> {
                Minecraft.getInstance().options.keyMappings.map { it }.firstOrNull { it.name == actionString }?.let {
                    it.isDown = true
                    it.clickCount++
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