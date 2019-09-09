package cz.applifting.humansis.model.api

import com.google.gson.annotations.SerializedName

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

//todo finish model
data class Beneficiary(
    @SerializedName("id") val id: Int,
    @SerializedName("local_given_name") val givenName: String?,
    @SerializedName("local_family_name") val familyName: String?,
    @SerializedName("status") val distributed: Boolean,
    @SerializedName("vulnerability_criteria") val vulnerabilities: List<Vulnerability>
)