package com.plot.evanescent.aircrafttransport.module

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftDisplayListener
import com.plot.evanescent.aircrafttransport.utils.AircraftFindUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftLoadListener
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetermineActivity : AppCompatActivity() {
    companion object {
        var backEnable: Boolean = kotlin.run {
            false
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()
        App.myApplication.aircraftAdUtils.fabulousLoadNative("dimly") {}
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("build", false)
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("annex", true)
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
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
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
            }
            AircraftUtils.aircraftCloak.also {
                if (TextUtils.isEmpty(it)) {
                    App.myApplication.getNetViewModel().aircraftGetCloak()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        App.myApplication.getNetViewModel().aircraftGetHost()
        App.myApplication.getNetViewModel().aircraftGetServer()
        App.myApplication.getNetViewModel().aircraftMyRefer()
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("annex", true)
    }

}