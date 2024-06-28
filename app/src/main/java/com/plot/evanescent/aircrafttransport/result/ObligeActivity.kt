package com.plot.evanescent.aircrafttransport.result

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.database.ProfileManager
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.databinding.ActivityFabulousBinding
import com.plot.evanescent.aircrafttransport.databinding.ActivityObligeBinding
import com.plot.evanescent.aircrafttransport.module.DetermineActivity
import com.plot.evanescent.aircrafttransport.utils.AircraftDisplayListener
import com.plot.evanescent.aircrafttransport.utils.AircraftFindUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay

class ObligeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityObligeBinding
    private var adDisplayEnable: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObligeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launchWhenCreated {
            adDisplayEnable = true
            obligeControlNative()
            obligeObserver()
            obligeControlState()
            obligeControlVpn()
            obligeClick()
        }
        lifecycleScope.launchWhenResumed {
            repeat(100) {
                delay(100)
                if (adDisplayEnable && AircraftFindUtils.adValid("fooey")) {
                    cancel()
                    obligeDisplayNative()
                }
            }
        }
        onBackPressedDispatcher.addCallback( object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (AircraftFindUtils.adValid("young")) {
                    App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this@ObligeActivity,
                        object : AircraftDisplayListener {
                            override fun beRefused(reason: String) {
                                AircraftUtils.print("display young beRefused:$reason")
                                finish()
                            }

                            override fun startDisplay() {

                            }

                            override fun displayFailed() {
                                AircraftUtils.print("young displayFailed")
                                finish()
                            }

                            override fun displaySuccess() {
                                AircraftUtils.print("young displaySuccess")
                            }

                            override fun closed() {
                                AircraftUtils.print("young closed")
                                finish()
                            }

                        })
                } else {
                    finish()
                }
            }
        })
    }

    override fun onRestart() {
        super.onRestart()
        adDisplayEnable = true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        adDisplayEnable = true
    }

    private fun obligeObserver() {
        App.myApplication.getViewModel().trafficChanged.observe(this) {
            val rx = it["rx"]
            val tx = it["tx"]
            binding.vObligeSpeed.vFabulousUpload.text = tx
            binding.vObligeSpeed.vFabulousDownload.text = rx
        }
        App.myApplication.getViewModel().connectedCounting.observe(this) {
            binding.vObligeCountingNo.text = it
            binding.vObligeCounting.text = it
        }
    }

    private fun obligeControlNative() {
        AircraftFindUtils.adValid("fooey").also {
            if (!it) {
                App.myApplication.aircraftAdUtils.fabulousLoadNative("fooey") {}
            }
        }
    }

    private fun obligeDisplayNative() {
        App.myApplication.aircraftPaintUtils.paintNative(
            "fooey",
            this,
            binding.vObligeAd,
            object : AircraftDisplayListener {
                override fun beRefused(reason: String) {
                    AircraftUtils.print("fooey dimily beRefused:$reason")
                }

                override fun startDisplay() {
                    binding.vObligeAd.visibility = View.VISIBLE
                    AircraftUtils.print("fooey startDisplay")
                }

                override fun displayFailed() {
                    AircraftUtils.print("fooey displayFailed")
                }

                override fun displaySuccess() {
                    adDisplayEnable = false
                    binding.vFabulousAdFlot.visibility = View.GONE
                    AircraftUtils.print("fooey displaySuccess")
                    App.myApplication.aircraftAdUtils.fabulousLoadNative("fooey") {}
                }

                override fun closed() {

                }

            }
        )
    }

    private fun obligeControlState() {
        binding.vObligeConnected.visibility =
            if (ProfileManager.isVpnConnected()) View.VISIBLE else View.GONE
        binding.vObligeDisconnected.visibility =
            if (ProfileManager.isVpnConnected()) View.GONE else View.VISIBLE
    }

    private fun obligeControlVpn() {
        val nCode = intent.getStringExtra("code") ?: "server_fast"
        binding.vObligeIconNo.setImageResource(
            App.myApplication.getNetViewModel().getProfileNationId(nCode)
        )
        binding.vObligeIcon.setImageResource(
            App.myApplication.getNetViewModel().getProfileNationId(nCode)
        )
    }

    private fun obligeClick() {
        binding.vObligeReturn.setOnClickListener {
            if (AircraftFindUtils.adValid("young")) {
                App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this,
                    object : AircraftDisplayListener {
                        override fun beRefused(reason: String) {
                            AircraftUtils.print("display young beRefused:$reason")
                            finish()
                        }

                        override fun startDisplay() {

                        }

                        override fun displayFailed() {
                            AircraftUtils.print("young displayFailed")
                            finish()
                        }

                        override fun displaySuccess() {
                            AircraftUtils.print("young displaySuccess")
                        }

                        override fun closed() {
                            AircraftUtils.print("young closed")
                            finish()
                        }

                    })
            } else {
                finish()
            }
        }
        binding.vObligeAccelerate.setOnClickListener {
            ProfileManager.getCurrentProfileConfig()?.also {
                ProfileManager.delProfile(it.id)
            }
            App.myApplication.getViewModel().connectAccelerate.postValue("accelerate")
            finish()
        }
    }
}