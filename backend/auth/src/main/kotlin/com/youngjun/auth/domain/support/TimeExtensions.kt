package com.youngjun.auth.domain.support

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun LocalDateTime.toEpochSecond(): Long = atZone(ZoneId.systemDefault()).toEpochSecond()

fun LocalDateTime.toInstant(): Instant = atZone(ZoneId.systemDefault()).toInstant()

fun Date.toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())

val EPOCH: LocalDateTime = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())

inline val Int.seconds: Duration get() = Duration.ofSeconds(toLong())

inline val Int.minutes: Duration get() = Duration.ofMinutes(toLong())

inline val Int.hours: Duration get() = Duration.ofHours(toLong())

inline val Int.days: Duration get() = Duration.ofDays(toLong())
