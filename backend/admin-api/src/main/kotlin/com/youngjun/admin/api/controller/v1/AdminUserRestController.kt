package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.api.controller.v1.response.DailyUserStatsListResponse
import com.youngjun.admin.api.controller.v1.response.UserCountResponse
import com.youngjun.admin.api.controller.v1.response.UsersPageResponse
import com.youngjun.admin.application.AdminUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminUserRestController(
    private val adminUserService: AdminUserService,
) {
    @GetMapping("/users")
    fun findAll(
        @RequestParam(defaultValue = "20") pageSize: Int,
        @RequestParam(defaultValue = "1") nextId: Long,
    ): ResponseEntity<UsersPageResponse> {
        val (users, lastId) = adminUserService.findAllPagedByCreatedAtDesc(pageSize, nextId)
        return ResponseEntity.ok(UsersPageResponse.of(users, lastId))
    }

    @GetMapping("/users/count")
    fun count(): ResponseEntity<UserCountResponse> {
        val count = adminUserService.count()
        return ResponseEntity.ok(UserCountResponse.from(count))
    }

    @GetMapping("/stats/daily-user-stats")
    fun getDailyUserStats(): ResponseEntity<DailyUserStatsListResponse> {
        val dailyUserStats = adminUserService.getDailyUserStats()
        return ResponseEntity.ok(DailyUserStatsListResponse.of(dailyUserStats))
    }
}
