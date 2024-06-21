package com.gagak.farmshields.core.modules

import android.util.Log
import com.gagak.farmshields.BuildConfig
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.data.remote.client.ChatApiService
import com.gagak.farmshields.core.data.remote.client.ReportApiService
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { AuthPreferences(get()) }

    single {
        val authPreferences: AuthPreferences = get()
        Interceptor { chain ->
            val token = authPreferences.getToken()
            val original = chain.request()
            val request = if (!token.isNullOrEmpty()) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                Log.e("AuthInterceptor", "Error: Token is null or empty $token")
                original
            }
            chain.proceed(request)
        }
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(get<Interceptor>())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)  // Increased connect timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // Increased write timeout
            .readTimeout(60, TimeUnit.SECONDS)     // Increased read timeout
            .build()
    }
}


val retrofitModule = module {
    single(named("default")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    single(named("chat")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_CHAT)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    single(named("report")) {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_REPORT)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    single {
        get<Retrofit>(named("default")).create(ApiService::class.java)
    }

    single {
        get<Retrofit>(named("chat")).create(ChatApiService::class.java)
    }

    single {
        get<Retrofit>(named("report")).create(ReportApiService::class.java)
    }
}

