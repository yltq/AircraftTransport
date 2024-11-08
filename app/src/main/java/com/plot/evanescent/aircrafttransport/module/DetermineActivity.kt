package com.plot.evanescent.aircrafttransport.module

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
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
    private var loadOpenEnable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadOpenEnable = true
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
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
    }

    override fun onResume() {
        super.onResume()
        determineLoadCmp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadOpenEnable = true
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()
    }

    override fun onRestart() {
        super.onRestart()
        loadOpenEnable = true
    }

    private fun determineLoadAd() {
        lifecycleScope.launch {
            if (!loadOpenEnable) return@launch
            var loadAdFinished: Boolean = false //已加载过广告配置
            repeat(100) {
                delay(100)

                if ((it in 10 until 40 && (App.myApplication.getNetViewModel().loadServerSuccess &&
                            App.myApplication.getNetViewModel().loadFirebaseDataSuccess &&
                            AircraftUtils.aircraftAdminLoadSuccess)) || it >= 40
                ) {
                    if (!loadAdFinished) {
                        loadAdFinished = true
                        App.myApplication.aircraftAdUtils.fabulousLoadNative("dimily") {}
                        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("annex", true)
                    } else if (AircraftFindUtils.adValid("annex")) {
                        cancel()
                        App.myApplication.aircraftPaintUtils.paintOpenOrIn("annex",
                            this@DetermineActivity, object : AircraftDisplayListener {
                                override fun beRefused(reason: String) {
                                    AircraftUtils.print("display annex beRefused: $reason")
                                    determineToFabulous()
                                }

                                override fun startDisplay() {
                                    AircraftUtils.print("annex startDisplay")
                                }

                                override fun displayFailed() {
                                    AircraftUtils.print("annex displayFailed")
                                    determineToFabulous()
                                }

                                override fun displaySuccess() {
                                    AircraftUtils.print("annex displaySuccess")
                                }

                                override fun closed() {
                                    AircraftUtils.print("annex closed")
                                    loadOpenEnable = false
                                    determineToFabulous()
                                }

                            })
                    }
                }
            }.run {
                determineToFabulous()
            }
        }
    }

    private fun determineToFabulous() {
        lifecycleScope.launch {
            delay(273)
            if (lifecycle.currentState != Lifecycle.State.RESUMED) return@launch
            startActivity(Intent(this@DetermineActivity, FabulousActivity::class.java))
            finish()
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
                            AircraftUtils.aircraftLoadCmp = true
                            if (!information.canRequestAds()) {
                                MobileAds.initialize(this)
                            }
                            determineLoadAd()
                        }
                    )
                },
                {
                    AircraftUtils.aircraftLoadCmp = true
                    determineLoadAd()
                })
        } else {
            determineLoadAd()
        }
    }

}