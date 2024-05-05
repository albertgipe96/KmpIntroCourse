package data.local

import domain.MongoDbRepository
import domain.model.Currency
import domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoDbRepositoryImpl : MongoDbRepository {

    private var realm: Realm? = null

    init {
        configureRealm()
    }

    override fun configureRealm() {
        if (realm == null || realm?.isClosed() == true) {
            val realmConfig = RealmConfiguration.Builder(schema = setOf(Currency::class))
                .compactOnLaunch()
                .build()
            realm = Realm.open(realmConfig)
        }
    }

    override suspend fun insertCurrencyData(currency: Currency) {
        realm?.write { copyToRealm(currency) }
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
        return realm?.query<Currency>()
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(data = result.list)
            } ?: flow { RequestState.Error("Realm not configured") }
    }

    override suspend fun cleanDb() {
        realm?.write {
            val collection = this.query<Currency>()
            delete(collection)
        }
    }

}