package com.gagak.farmshields.core.modules

import com.gagak.farmshields.core.domain.model.viewmodel.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModule = module {
    viewModelOf(::AuthViewModel)
}