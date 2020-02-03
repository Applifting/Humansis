package cz.applifting.humansis.ui

import android.app.Application
import android.content.Context
import cz.applifting.humansis.R
import cz.applifting.humansis.di.AppComponent
import cz.applifting.humansis.di.DaggerAppComponent
import cz.applifting.humansis.di.SP_GENERIC_NAME
import cz.applifting.humansis.managers.SP_DB_PASS_KEY
import cz.applifting.humansis.managers.SP_SALT_KEY
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraMailSender
import org.acra.annotation.AcraNotification
import org.acra.data.StringFormat

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
@AcraCore(
    sharedPreferencesName = "acraSp", // used for enable / disable reporting (by user choice)
    additionalSharedPreferences = [SP_GENERIC_NAME],
    excludeMatchingSharedPreferencesKeys = [SP_DB_PASS_KEY, SP_SALT_KEY],
    buildConfigClass = BuildConfig::class,
    reportFormat = StringFormat.JSON
)
@AcraMailSender(
    mailTo = "pavel.haluza+humansis.crash@applifting.cz" // TODO some other email pls :(
)
@AcraNotification(
    resTitle = R.string.acra_notif_title,
    resText = R.string.acra_notif_text,
    resChannelName = R.string.acra_notif_channel
)
class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .context(this)
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        ACRA.init(this)
    }
}