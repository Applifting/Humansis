package cz.applifting.humansis.di

import com.google.gson.GsonBuilder
import cz.applifting.humansis.api.HumansisService
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
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val oldRequest = chain.request()
            val headers = oldRequest.headers().newBuilder().add("country", "KHM").build()
            val request = oldRequest.newBuilder().headers(headers).build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-demo.humansis.org/api/wsse/")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        .client(client)
        .build().create<HumansisService>()

    @Provides
    fun retrofitProvider(): HumansisService {
        return retrofit
    }
}