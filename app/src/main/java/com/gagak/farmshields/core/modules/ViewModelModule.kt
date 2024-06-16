package com.gagak.farmshields.core.modules

import com.gagak.farmshields.core.domain.model.viewmodel.anita.AnitaViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.auth.AuthViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.main.MainViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::UserViewModel)
<<<<<<< HEAD
    viewModelOf(::AnitaViewModel)
=======
    viewModelOf(::MainViewModel)
>>>>>>> development-authenthication
}