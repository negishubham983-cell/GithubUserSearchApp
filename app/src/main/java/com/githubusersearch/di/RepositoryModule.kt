package com.githubusersearch.di

import com.githubusersearch.data.repository.GithubRepository
import com.githubusersearch.data.repository.GithubRepositoryImpl
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
    abstract fun bindGithubRepository(
        impl: GithubRepositoryImpl
    ): GithubRepository
}
