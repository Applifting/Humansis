package cz.applifting.humansis.repositories

import cz.applifting.humansis.db.DbProvider
import cz.applifting.humansis.model.db.SyncError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 27, November, 2019
 */
@Singleton
class ErrorsRepository @Inject constructor(dbProvider: DbProvider) {

    private val db by lazy { dbProvider.get() }

    fun getAll(): Flow<List<SyncError>> {
        return db.errorsDao().getAll()
    }

    suspend fun insert(syncError: SyncError) {
        db.errorsDao().insert(syncError)
    }

    suspend fun clearAll() {
        db.errorsDao().deleteAll()
    }
}