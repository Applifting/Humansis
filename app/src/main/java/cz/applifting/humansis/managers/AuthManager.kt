package cz.applifting.humansis.managers

import cz.applifting.humansis.db.HumansisDB
import cz.applifting.humansis.misc.HumansisError
import cz.applifting.humansis.misc.generateXWSSEHeader
import cz.applifting.humansis.model.api.LoginReqRes
import cz.applifting.humansis.model.db.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class AuthManager @Inject constructor(private val humansisDB: HumansisDB) {

    suspend fun login(userResponse: LoginReqRes) {
        withContext(Dispatchers.IO) {
            val id = userResponse.id?.toInt() ?: throw HumansisError()
            val user = User(id, userResponse.username, userResponse.email, userResponse.password)
            humansisDB.userDao().insert(user)
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            // TODO clear all other tables
            humansisDB.userDao().deleteAll()
        }
    }

    suspend fun retrieveUser(): User? {
        return withContext(Dispatchers.IO) {
            humansisDB.userDao().getUser()
        }
    }

    suspend fun getAuthHeader(): String? {
        val user = retrieveUser()
        return user?.let {
            generateXWSSEHeader(user.username, user.saltedPassword)
        }
    }
}