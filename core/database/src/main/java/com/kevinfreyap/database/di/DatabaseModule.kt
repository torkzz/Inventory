package com.kevinfreyap.database.di

import android.content.Context
import androidx.room.Room
import com.kevinfreyap.database.AppDatabase
import com.kevinfreyap.database.dao.BatchDao
import com.kevinfreyap.database.dao.CategoryDao
import com.kevinfreyap.database.dao.GroupDao
import com.kevinfreyap.database.dao.LocationDao
import com.kevinfreyap.database.dao.ProductDao
import com.kevinfreyap.database.dao.TransactionDao
import com.kevinfreyap.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "inventory.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideLocationDao(db: AppDatabase): LocationDao = db.locationDao()

    @Provides
    fun provideBatchDao(db: AppDatabase): BatchDao = db.batchDao()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideGroupDao(db: AppDatabase): GroupDao = db.groupDao()
}