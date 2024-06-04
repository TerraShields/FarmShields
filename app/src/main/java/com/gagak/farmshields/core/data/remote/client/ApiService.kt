package com.gagak.farmshields.core.data.remote.client

import retrofit2.http.GET


interface ApiService {

    // OAuth Google Sign
    @GET("auth/google")
    fun loginWithGoogle()
}