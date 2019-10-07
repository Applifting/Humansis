package cz.applifting.humansis.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.managers.LoginManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.net.HttpURLConnection
import javax.inject.Singleton


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Module
class AppModule {

    @Provides
    @Reusable
    fun retrofitProvider(baseUrl: String, loginManager: LoginManager, context: Context): HumansisService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->

                val oldRequest = chain.request()

                if (context.isNetworkConnected()) {
                    runBlocking {
                        val headersBuilder = oldRequest.headers().newBuilder()
                            .add("country", "KHM")

                        loginManager.getAuthHeader()?.let {
                            headersBuilder.add("x-wsse", it)
                        }

                        val request = oldRequest.newBuilder().headers(headersBuilder.build()).build()
                        chain.proceed(request)
                    }
                } else {
                    Response.Builder()
                        .protocol(Protocol.HTTP_2)
                        .request(oldRequest)
                        .code(HttpURLConnection.HTTP_UNAVAILABLE)
                        .message("No internet connection")
                        .body(ResponseBody.create(MediaType.parse("text/plain"), "No internet connection"))
                        .build()
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