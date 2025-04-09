package com.youngjun.admin.application

import com.youngjun.admin.domain.administrator.Administrator
import com.youngjun.admin.domain.administrator.AdministratorRepository
import com.youngjun.admin.domain.administrator.NewAdministrator
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.support.error.AdminException
import com.youngjun.admin.support.error.ErrorType.ADMINISTRATOR_DUPLICATE
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdministratorService(
    private val administratorRepository: AdministratorRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(emailAddress: String): Administrator =
        administratorRepository.findByEmailAddress(EmailAddress(emailAddress))
            ?: throw UsernameNotFoundException("Cannot find the user. $emailAddress")

    fun signUp(newAdministrator: NewAdministrator): Administrator {
        if (administratorRepository.existsByEmailAddress(newAdministrator.emailAddress)) {
            throw AdminException(ADMINISTRATOR_DUPLICATE)
        }
        return administratorRepository.save(newAdministrator.encodedWith(passwordEncoder))
    }
}
