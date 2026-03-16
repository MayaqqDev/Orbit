package dev.mayaqq.orbit.utils

import java.util.Optional

fun <T> Optional<T>.value(): T? = this.orElse(null)