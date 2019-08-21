package cz.applifting.humansis.di

import android.app.Application
import androidx.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.google.gson.GsonBuilder
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.HumansisDB
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create







/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Module
class AppModule {

    @Provides
    fun retrofitProvider(baseUrl: String): HumansisService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val oldRequest = chain.request()
                val headers = oldRequest.headers().newBuilder().add("country", "KHM").build()
                val request = oldRequest.newBuilder().headers(headers).build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(client)
            .build().create()
    }

    @Provides
    fun dbProvider(applicationContext: Application): HumansisDB {
        // TODO load this password from keychain
        val factory = SafeHelperFactory(charArrayOf('p','a','s','s','w','o','r','d'))

        return Room.databaseBuilder(
            applicationContext,
            HumansisDB::class.java, "humansis-db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }
}