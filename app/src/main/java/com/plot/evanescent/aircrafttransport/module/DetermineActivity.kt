package com.plot.evanescent.aircrafttransport.module

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.utils.AircraftDisplayListener
import com.plot.evanescent.aircrafttransport.utils.AircraftFindUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetermineActivity : AppCompatActivity() {
    companion object {
        var backEnable: Boolean = kotlin.run {
            false
        }
    }

    private lateinit var information: ConsentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()

        onBackPressedDispatcher.addCallback( object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backEnable.run {
                    if (this) {
                        finish()
                    }
                }
            }
        })
        setContentView(R.layout.activity_determine)

        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                AdvertisingIdClient.getAdvertisingIdInfo(this@DetermineActivity).run {
                    App.myApplication.gaid = id ?: ""
                    App.myApplication.launchLimited = isLimitAdTrackingEnabled.run {
                        val key = if (this) {
                            "vengeful"
                        } else {
                            "script"
                        }
                        key
                    }
                }
            }.onFailure {
                AircraftUtils.print("getAdvertisingIdInfo error ${it.message}")
            }
        }
        AircraftUtils.aircraftCloak.also {
            if (TextUtils.isEmpty(it)) {
                App.myApplication.getNetViewModel().aircraftGetCloak()
            }
        }
        AircraftUtils.aircraftPostJsonMethod(
            if (BuildConfig.DEBUG)
                "https://test-peggy.stablefasttunnel.com/impelled/prom/nikko"
            else "https://peggy.stablefasttunnel.com/monetary/gazebo",
            "fusiform", JSONObject(), false
        ) {}

        determineLoadCmp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("annex", true)
    }

     private fun determineLoadAd() {
         App.myApplication.aircraftAdUtils.fabulousLoadNative("dimily") {}
         App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("build", false)
         App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("annex", true)

         lifecycleScope.launchWhenResumed {
             repeat(100) {
                 delay(100)
                 if (it >= 10 && AircraftFindUtils.adValid("annex")) {
                     cancel()
                     App.myApplication.aircraftPaintUtils.paintOpenOrIn("annex",
                         this@DetermineActivity, object : AircraftDisplayListener {
                             override fun beRefused(reason: String) {
                                 AircraftUtils.print("display annex beRefused: $reason")
                                 startActivity(Intent(this@DetermineActivity, FabulousActivity::class.java))
                                 finish()
                             }

                             override fun startDisplay() {
                                 AircraftUtils.print("annex startDisplay")
                             }

                             override fun displayFailed() {
                                 AircraftUtils.print("annex displayFailed")
                                 startActivity(Intent(this@DetermineActivity, FabulousActivity::class.java))
                                 finish()
                             }

                             override fun displaySuccess() {
                                 AircraftUtils.print("annex displaySuccess")
                             }

                             override fun closed() {
                                 AircraftUtils.print("annex closed")
                                 startActivity(Intent(this@DetermineActivity, FabulousActivity::class.java))
                                 finish()
                             }

                         })
                 }
             }.run {
                 startActivity(Intent(this@DetermineActivity, FabulousActivity::class.java))
                 finish()
             }
         }
     }

    private fun determineLoadCmp() {
        if (!AircraftUtils.aircraftLoadCmp) {
            val debugSettings = ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("0CD2AFA15CF44439BDB12AE7E8AF7CD9")
                .build()
            val params = ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(debugSettings)
                .build()
            information = UserMessagingPlatform.getConsentInformation(this)
            information.requestConsentInfoUpdate(
                this,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        this@DetermineActivity,
                        ConsentForm.OnConsentFormDismissedListener {
                                loadAndShowError ->
                            AircraftUtils.print("determineLoadCmp ----OnConsentFormDismissedListener loadAndShowError-------${loadAndShowError?.message}")
                            AircraftUtils.aircraftLoadCmp = true
                            if (!information.canRequestAds()) {
                                MobileAds.initialize(this)
                            }
                            determineLoadAd()
                        }
                    )
                },
                {
                        requestConsentError ->
                    AircraftUtils.print("determineLoadCmp ----requestConsentError-------${requestConsentError.message}")
                    AircraftUtils.aircraftLoadCmp = true
                    determineLoadAd()
                })
        } else {
            determineLoadAd()
        }
    }
    
}