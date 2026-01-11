package com.ipca.socialstore

import android.content.Context
import androidx.room.Room
import com.ipca.socialstore.data.room.AppDatabase
import com.ipca.socialstore.data.room.interfaces.AcademicInterface
import com.ipca.socialstore.data.room.interfaces.BeneficiaryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.cio.CIO
import okhttp3.OkHttp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY,
        ) {
            httpEngine = io.ktor.client.engine.okhttp.OkHttp.create()

            install(Postgrest) // Enable Database
            install(Auth)
            install(Storage)
            install(Realtime)
            install(Functions)
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "social_store_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBeneficiaryInterface(database: AppDatabase): BeneficiaryInterface {
        return database.beneficiaryInterface()
    }

    @Provides
    fun provideAcademicInterface(database: AppDatabase): AcademicInterface {
        return database.academicInterface()
    }
}