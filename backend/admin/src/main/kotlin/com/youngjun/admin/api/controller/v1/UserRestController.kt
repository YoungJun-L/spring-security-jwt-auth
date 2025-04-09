package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.api.controller.v1.response.UsersPageResponse
import com.youngjun.admin.application.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRestController(
    private val userService: UserService,
) {
    @GetMapping("/users")
    fun findAll(
        @RequestParam(required = false) nextId: Long?,
    ): ResponseEntity<UsersPageResponse> = ResponseEntity.ok(UsersPageResponse.of(userService.findAllPagedByCreatedAtDesc(nextId)))
}
