package com.gagak.farmshields.core.modules

import org.koin.dsl.module

val appModule = module {
    includes(
        viewModule,
        repoModule
    )
}