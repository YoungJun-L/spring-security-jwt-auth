package com.youngjun.auth.core.domain.support

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toEpochSecond(): Long = this.atZone(ZoneId.systemDefault()).toEpochSecond()

fun LocalDateTime.toInstant(): Instant = this.atZone(ZoneId.systemDefault()).toInstant()

val EPOCH: LocalDateTime = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)

inline val Int.seconds: Duration get() = Duration.ofSeconds(this.toLong())

inline val Int.hours: Duration get() = Duration.ofHours(this.toLong())

inline val Int.days: Duration get() = Duration.ofDays(this.toLong())
