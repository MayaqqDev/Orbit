package dev.mayaqq.orbit.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

object Text {
    val EMPTY = of("")

    fun of(text: String): MutableComponent {
        return Component.literal(text)
    }

    fun trans(text: String): MutableComponent {
        return Component.translatable(text)
    }
}