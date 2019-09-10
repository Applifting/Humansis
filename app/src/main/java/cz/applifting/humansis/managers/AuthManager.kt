package cz.applifting.humansis.managers

import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.misc.generateXWSSEHeader
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.model.db.User
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 *
 * This class is something like a repository, but it is not, as it is required by API service and can't depend on it
 */
class AuthManager @Inject constructor(private val humansisDB: HumansisDB) {

    suspend fun login(userResponse: LoginReqRes) {
        val id = userResponse.id?.toInt() ?: throw HumansisError()
        val user = User(id, userResponse.username, userResponse.email, userResponse.password)
        humansisDB.userDao().insert(user)
    }

    suspend fun logout() {
        // TODO clear all other data
        humansisDB.apply {
            userDao().deleteAll()
            distributionsDao().deleteAll()
            projectsDao().deleteAll()
            beneficiariesDao().deleteAll()
        }
    }

    suspend fun retrieveUser(): User? {
        return humansisDB.userDao().getUser()
    }

    suspend fun getAuthHeader(): String? {
        val user = retrieveUser()
        return user?.let {
            generateXWSSEHeader(user.username, user.saltedPassword)
        }
    }
}