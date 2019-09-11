package cz.applifting.humansis.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.managers.AuthManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Module
class AppModule {

    @Provides
    @Reusable
    fun retrofitProvider(baseUrl: String, authManager: AuthManager): HumansisService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                runBlocking {
                    val oldRequest = chain.request()

                    val headersBuilder = oldRequest.headers().newBuilder()
                        .add("country", "KHM")

                    authManager.getAuthHeader()?.let {
                        headersBuilder.add("x-wsse", it)
                    }

                    val request = oldRequest.newBuilder().headers(headersBuilder.build()).build()
                    chain.proceed(request)
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .client(client)
            .build().create()
    }

    @Provides
    @Singleton
    fun dbProviderProvider(context: Context): DbProvider {
        return DbProvider(context)
    }

    @Provides
    @Singleton
    fun spProvider(context: Context): SharedPreferences {
        return context.getSharedPreferences("HumansisSP", Context.MODE_PRIVATE)
    }
}