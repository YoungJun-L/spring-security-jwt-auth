package com.youngjun.auth.domain.support

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun LocalDateTime.toEpochSecond(): Long = this.atZone(ZoneId.systemDefault()).toEpochSecond()

fun LocalDateTime.toInstant(): Instant = this.atZone(ZoneId.systemDefault()).toInstant()

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())

val EPOCH: LocalDateTime = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())

inline val Int.seconds: Duration get() = Duration.ofSeconds(this.toLong())

inline val Int.minutes: Duration get() = Duration.ofMinutes(this.toLong())

inline val Int.hours: Duration get() = Duration.ofHours(this.toLong())

inline val Int.days: Duration get() = Duration.ofDays(this.toLong())
