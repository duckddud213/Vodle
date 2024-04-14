package com.tes.vodle.di

import com.tes.domain.repository.UserRepository
import com.tes.domain.repository.VodleRepository
import com.tes.vodle.repository.UserRepositoryImpl
import com.tes.vodle.repository.VodleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindVodleRepository(vodleRepositoryImpl: VodleRepositoryImpl): VodleRepository
}
