package cz.applifting.humansis.api

import cz.applifting.humansis.model.GetSaltResponse
import cz.applifting.humansis.model.LoginReqRes
import cz.applifting.humansis.model.Project
import retrofit2.http.*

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
interface HumansisService {

    @GET("salt/{username}")
    suspend fun getSalt(@Path("username") username: String): GetSaltResponse

    @POST("login")
    suspend fun postLogin(@Body loginReqRes: LoginReqRes): LoginReqRes

    @GET("projects")
    suspend fun getProjects(@Header("x-wsse") authHeader: String): List<Project>
}