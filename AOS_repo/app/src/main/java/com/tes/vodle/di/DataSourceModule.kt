package com.tes.vodle.di

import com.tes.vodle.datasource.user.UserDataSource
import com.tes.vodle.datasource.user.UserDataSourceImpl
import com.tes.vodle.datasource.vodle.VodleDataSource
import com.tes.vodle.datasource.vodle.VodleDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Binds
    @Singleton
    abstract fun bindVodleDataSource(vodleDataSourceImpl: VodleDataSourceImpl): VodleDataSource
}
