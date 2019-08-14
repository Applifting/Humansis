package cz.applifting.humansis.api

import cz.applifting.humansis.model.GetSaltResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
interface HumansisService {
    @GET("salt/{email}")
    suspend fun getSalt(@Path("email") email: String): GetSaltResponse
}