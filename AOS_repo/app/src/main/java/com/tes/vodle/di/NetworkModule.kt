package com.tes.vodle.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tes.vodle.BuildConfig
import com.tes.vodle.util.AuthAuthenticator
import com.tes.vodle.util.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            setLevel(HttpLoggingInterceptor.Level.HEADERS)
        } else {
            setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class VodleClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SocialLoginClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthClient

    @Singleton
    @Provides
    @VodleClient
    fun provideVodleClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.apply {
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            addInterceptor(authInterceptor)
            addInterceptor(loggingInterceptor)
            authenticator(authAuthenticator)
        }
        return builder.build()
    }

    @Singleton
    @Provides
    @AuthClient
    fun provideAuthClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.apply {
            addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    @Singleton
    @Provides
    @SocialLoginClient
    fun provideSocialLoginClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.apply {
            addInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }
}
