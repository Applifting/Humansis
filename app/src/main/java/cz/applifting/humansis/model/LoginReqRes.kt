package cz.applifting.humansis.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
data class LoginReqRes(
    @SerializedName("change_password") val changePassword: Boolean,
    val email: String,
    val id: String?,
    val language: String?,
    val password: String,
    val roles: String?,
    val username: String,
    val vendor: String?
)