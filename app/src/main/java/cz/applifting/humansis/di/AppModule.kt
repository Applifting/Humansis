package cz.applifting.humansis.di

import android.content.Context
import cz.applifting.humansis.api.HumansisService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */

@Module
class AppModule {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-demo.humansis.org/api/wsse/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create<HumansisService>()

    @Provides
    fun retrofitProvider(): HumansisService {
        return retrofit
    }
}