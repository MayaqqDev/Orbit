package dev.mayaqq.orbit.utils

import net.minecraft.client.KeyMapping

fun KeyMapping.press() {
    this.isDown = true
    this.clickCount++
}