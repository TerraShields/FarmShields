package com.gagak.farmshields.core.modules

import com.gagak.farmshields.core.domain.repository.auth.AuthRepository
import com.gagak.farmshields.core.domain.repository.main.MainRepository
import com.gagak.farmshields.core.domain.repository.user.UserRepository
import org.koin.dsl.module

val repoModule = module {
    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { MainRepository(get()) }
}