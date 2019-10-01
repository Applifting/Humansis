package cz.applifting.humansis.api

import cz.applifting.humansis.model.api.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
interface HumansisService {

    @GET("salt/{username}")
    suspend fun getSalt(@Path("username") username: String): GetSaltResponse

    @POST("login")
    suspend fun postLogin(@Body loginReqRes: LoginReqRes): LoginReqRes

    @GET("projects")
    suspend fun getProjects(): List<Project>

    @GET("distributions/projects/{projectId}")
    suspend fun getDistributions(@Path("projectId") projectId: Int): List<Distribution>

    @GET("distributions/{distributionId}/beneficiaries")
    suspend fun getDistributionBeneficiaries(@Path("distributionId") distributionId: Int): List<DistributionBeneficiary>

    @POST("distributions/generalrelief/distributed")
    suspend fun setDistributedRelief(@Body distributedReliefRequest: DistributedReliefRequest)

}
