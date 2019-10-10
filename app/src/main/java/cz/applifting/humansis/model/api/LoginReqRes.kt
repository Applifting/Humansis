package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName
import cz.applifting.humansis.model.Role

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 17, August, 2019
 */
data class LoginReqRes(
    @SerializedName("change_password") val changePassword: Boolean,
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("password") val password: String,
    @SerializedName("roles") val roles: List<Role>?,
    @SerializedName("username") val username: String,
    @SerializedName("vendor") val vendor: String?,
    @SerializedName("projects") val projects: List<Project>?
)