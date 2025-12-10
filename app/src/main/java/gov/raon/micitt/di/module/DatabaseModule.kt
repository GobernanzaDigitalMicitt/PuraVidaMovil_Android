package gov.raon.micitt.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gov.raon.micitt.di.local.LocalRepoImpl
import gov.raon.micitt.di.local.LocalRepository
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRealmDatabase(
        @ApplicationContext context: Context
    ): Realm {
        Realm.init(context)
        val realmConfiguration = RealmConfiguration.Builder()
            .name("${context.packageName}.db")
            .schemaVersion(1)
//            .migration()  //for release
            .deleteRealmIfMigrationNeeded()  //for dev
            .allowWritesOnUiThread(true)
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        return Realm.getDefaultInstance()
    }

    @Provides
    @Singleton
    fun provideLocalRepository(realm:Realm?) : LocalRepository {
        return LocalRepoImpl(realm)
    }
}