package com.youngjun.auth.core.domain.support

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toEpochSecond(): Long = this.atZone(ZoneId.systemDefault()).toEpochSecond()

fun LocalDateTime.toInstant(): Instant = this.atZone(ZoneId.systemDefault()).toInstant()

inline val Int.hours: Duration get() = Duration.ofHours(this.toLong())
