package co.id.kadaluarsa.tapclient

import android.app.Application
import co.id.kadaluarsa.tapdetector.FraudDetectorClient

class SampleApp : Application() {

    private val userId: String = "aGI3Q8HGqS"
    lateinit var tap: FraudDetectorClient

    companion object {
        private lateinit var sInstance: SampleApp
        fun getAppContext(): SampleApp {
            return sInstance
        }

        @Synchronized
        private fun setInstance(app: SampleApp) {
            sInstance = app
        }
    }

    override fun onCreate() {
        super.onCreate()
        setInstance(this)
        tap = FraudDetectorClient.Config()
            .application(this@SampleApp)
            .enableDebug(BuildConfig.DEBUG)
            .setUserId(userId)
            .build()
    }

}