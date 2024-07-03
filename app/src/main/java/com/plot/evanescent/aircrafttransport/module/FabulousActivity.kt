package com.plot.evanescent.aircrafttransport.module

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.utils.AircraftStartService
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.databinding.ActivityFabulousBinding
import com.plot.evanescent.aircrafttransport.result.ImpelActivity
import com.plot.evanescent.aircrafttransport.result.ObligeActivity
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftDisplayListener
import com.plot.evanescent.aircrafttransport.utils.AircraftFindUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftLoadListener
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.displayChoose
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.displayLocationLimited
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.displayNoNet
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.go
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.share
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FabulousActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFabulousBinding
    private lateinit var myConnection: ShadowsocksConnection
    private var connectingScope: Job? = null
    private var adDisplayEnable: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFabulousBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DataStore.publicStore.registerChangeListener(App.myApplication.getViewModel())
        myConnection = ShadowsocksConnection(true).also {
            it.bandwidthTimeout = 500
            it.connect(this, App.myApplication.getViewModel())
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (ProfileManager.isVpnConnecting() || binding.fabulousLayoutHouse.vFabulousConnecting.visibility == View.VISIBLE) {
                    toast("VPN is connecting. Please try again later.")
                } else if (ProfileManager.isVpnStopping()) {
                    cancelConnectionScope()
                } else if (binding.vFabulousAtTouch.visibility == View.VISIBLE) {
                    App.myApplication.showTouch = false
                    binding.vFabulousAtTouch.visibility = View.GONE
                } else {
                    finish()
                }
            }

        })
        lifecycleScope.launchWhenCreated {
            adDisplayEnable = true
            fabulous_bottom00()
            fabulousObserver()
            fabulousClick()
            App.myApplication.showTouch.also {
                if (it && !ProfileManager.isVpnConnected()) {
                    binding.vFabulousAtTouch.visibility = View.VISIBLE
                } else {
                    binding.vFabulousAtTouch.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fabulousResumeRefresh()
        fabulousServer()
    }

    private var launch = registerForActivityResult(AircraftStartService()) {
        if (it) return@registerForActivityResult
        fabulous_vpn010()
        ProfileManager.vpnState = BaseService.State.Connecting
        App.myApplication.getViewModel().startConnect()
    }

    override fun onRestart() {
        super.onRestart()
        adDisplayEnable = true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        adDisplayEnable = true
    }

    override fun onDestroy() {
        super.onDestroy()
        DataStore.publicStore.unregisterChangeListener(App.myApplication.getViewModel())
        myConnection.disconnect(this)
        App.myApplication.getViewModel().stateChanged.removeObservers(this)
        App.myApplication.getViewModel().connectAccelerate.removeObservers(this)
    }

    override fun onStop() {
        super.onStop()
        cancelConnectionScope()
    }

    private fun fabulousResumeRefresh() {
        binding.fabulousLayoutHouse.vFabulousIcon.setImageResource(R.mipmap.server_fast)
        binding.fabulousLayoutHouse.vFabulousIp.text = ""
        binding.fabulousLayoutHouse.vFabulousCountry.text = "Smart"
        ProfileManager.getCurrentProfileConfig()?.also {
            binding.fabulousLayoutHouse.vFabulousIp.text = it.host
            binding.fabulousLayoutHouse.vFabulousCountry.text = it.inSmart.run {
                if (this) {
                    "Smart"
                } else {
                    it.nName
                }
            }
            binding.fabulousLayoutHouse.vFabulousIcon.setImageResource(
                App.myApplication.getNetViewModel().getProfileNationId(it.inSmart.run {
                    if (this) {
                        ""
                    } else {
                        it.nCode
                    }
                })
            )
        }
        AircraftFindUtils.adValid("dimily").also {
            if (!it) {
                fabulousLoadNative()
            } else {
                lifecycleScope.launch {
                    delay(250)
                    fabulousDisplayNative()
                }
            }
        }
    }

    private fun fabulousLoadNative() {
        App.myApplication.aircraftAdUtils.fabulousLoadNative("dimily") {
            fabulousDisplayNative()
        }
    }

    private fun fabulousDisplayNative() {
        if (adDisplayEnable) {
            App.myApplication.aircraftPaintUtils.paintNative(
                "dimily",
                this,
                binding.fabulousLayoutHouse.vFabulousAd,
                object : AircraftDisplayListener {
                    override fun beRefused(reason: String) {
                        AircraftUtils.print("display dimily beRefused:$reason")
                    }

                    override fun startDisplay() {
                        binding.fabulousLayoutHouse.vFabulousAd.visibility = View.VISIBLE
                        AircraftUtils.print("dimily startDisplay")
                    }

                    override fun displayFailed() {
                        AircraftUtils.print("dimily displayFailed")
                    }

                    override fun displaySuccess() {
                        adDisplayEnable = false
                        binding.fabulousLayoutHouse.vFabulousAdFlot.visibility = View.GONE
                        AircraftUtils.print("dimily displaySuccess")
                        fabulousLoadNative()
                    }

                    override fun closed() {

                    }

                }
            )
        }
    }

    private fun fabulousStopConnect() {
        connectingScope?.cancel()
        connectingScope = lifecycleScope.launch {
            fabulous_vpn011()
            ProfileManager.vpnState = BaseService.State.Stopping
            repeat(AircraftFindUtils.enable("build").run {
                val n = if (this) 100 else 20
                fabulousLoadBuild()
                fabulousLoadYoung()
                fabulousLoadNative()
                App.myApplication.aircraftAdUtils.fabulousLoadNative("fooey") {}
                n
            }) {
                delay(100)
                if (it >= 10 && AircraftFindUtils.adValid("build")) {
                    cancel()
                    App.myApplication.aircraftPaintUtils.paintOpenOrIn("build", this@FabulousActivity,
                        object : AircraftDisplayListener {
                            override fun beRefused(reason: String) {
                                AircraftUtils.print("display build beRefused: $reason")
                                App.myApplication.getViewModel().stopConnect()
                            }

                            override fun startDisplay() {

                            }

                            override fun displayFailed() {
                                AircraftUtils.print("build displayFailed")
                                App.myApplication.getViewModel().stopConnect()
                            }

                            override fun displaySuccess() {
                                AircraftUtils.print("build displaySuccess")
                            }

                            override fun closed() {
                                AircraftUtils.print("build closed")
                                fabulousLoadBuild()
                                App.myApplication.getViewModel().stopConnect()
                            }

                        })
                }
            }.run {
                App.myApplication.getViewModel().stopConnect()
            }
        }
    }

    private fun cancelConnectionScope() {
        connectingScope?.cancel()
        ProfileManager.historyProfile?.takeIf { it != ProfileManager.getCurrentProfileConfig() }
            ?.also {
                it.id = DataStore.profileId
                ProfileManager.updateProfile(it)
                ProfileManager.historyProfile = null
            }
        if (ProfileManager.isVpnConnected() || ProfileManager.isVpnStopping()) {
            ProfileManager.vpnState = BaseService.State.Connected
            fabulous_vpn10()
        } else if (ProfileManager.isVpnStopped() || ProfileManager.isVpnConnecting()) {
            ProfileManager.vpnState = BaseService.State.Stopped
            fabulous_vpn00()
        }
    }

    private fun fabulousConnect() {
        if (!App.myApplication.getViewModel().netEnable()) {
            displayNoNet()
        } else if (App.myApplication.getViewModel().locationLimited()) {
            displayLocationLimited {
                finishAffinity()
            }
        } else if (ProfileManager.allServers.isEmpty()) {
            lifecycleScope.launch {
                App.myApplication.getNetViewModel().loadingSever.also {
                    if (!it) {
                        App.myApplication.getNetViewModel().aircraftGetServer()
                    }
                }
                repeat(200) {
                    if (it == 1) {
                        binding.vFabulousServerLoading.visibility = View.VISIBLE
                    }
                    delay(10)
                }.run {
                    binding.vFabulousServerLoading.visibility = View.GONE
                }
            }
        } else {
            launch.launch(null)
        }
    }

    fun fabulousClick() {
        binding.fabulousLayoutHouse.vFabulousStart.setOnClickListener {
            fabulousConnect()
        }
        binding.fabulousLayoutHouse.vFabulousOff.setOnClickListener {
            fabulousStopConnect()
        }
        binding.vHouse.setOnClickListener {
            if (ProfileManager.isVpnConnecting() ||
                binding.fabulousLayoutHouse.vFabulousConnecting.visibility == View.VISIBLE
            ) {
                toast("VPN is connecting. Please try again later.")
            } else if (ProfileManager.isVpnStopping()) {
                cancelConnectionScope()
                if (binding.fabulousLayoutUniverse.root.visibility == View.VISIBLE) {
                    AircraftFindUtils.adValid("young").also {
                        if (it) {
                            App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this,
                                object : AircraftDisplayListener {
                                    override fun beRefused(reason: String) {
                                        AircraftUtils.print("display young beRefused:$reason")
                                        fabulous_bottom00()
                                    }

                                    override fun startDisplay() {

                                    }

                                    override fun displayFailed() {
                                        AircraftUtils.print("young displayFailed")
                                        fabulous_bottom00()
                                    }

                                    override fun displaySuccess() {
                                        AircraftUtils.print("young displaySuccess")
                                    }

                                    override fun closed() {
                                        AircraftUtils.print("young closed")
                                        fabulous_bottom00()
                                    }

                                })
                        } else {
                            fabulous_bottom00()
                        }
                    }
                } else {
                    fabulous_bottom00()
                }
            } else {
                if (binding.fabulousLayoutUniverse.root.visibility == View.VISIBLE) {
                    AircraftFindUtils.adValid("young").also {
                        if (it) {
                            App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this,
                                object : AircraftDisplayListener {
                                    override fun beRefused(reason: String) {
                                        AircraftUtils.print("display young beRefused:$reason")
                                        fabulous_bottom00()
                                    }

                                    override fun startDisplay() {

                                    }

                                    override fun displayFailed() {
                                        AircraftUtils.print("young displayFailed")
                                        fabulous_bottom00()
                                    }

                                    override fun displaySuccess() {
                                        AircraftUtils.print("young displaySuccess")
                                    }

                                    override fun closed() {
                                        AircraftUtils.print("young closed")
                                        fabulous_bottom00()
                                    }

                                })
                        } else {
                            fabulous_bottom00()
                        }
                    }
                } else {
                    fabulous_bottom00()
                }
            }
        }
        binding.vUniverse.setOnClickListener {
            if (binding.vFabulousAtTouch.visibility == View.VISIBLE) {
                return@setOnClickListener
            }
            if (ProfileManager.isVpnConnecting() ||
                binding.fabulousLayoutHouse.vFabulousConnecting.visibility == View.VISIBLE) {
                toast("VPN is connecting. Please try again later.")
                return@setOnClickListener
            }
            if (ProfileManager.isVpnStopping()) {
                cancelConnectionScope()
            }
            if (ProfileManager.allServers.isEmpty()) {
                lifecycleScope.launch {
                    App.myApplication.getNetViewModel().loadingSever.also {
                        if (!it) {
                            App.myApplication.getNetViewModel().aircraftGetServer()
                        }
                    }
                    repeat(200) {
                        if (it == 1) {
                            binding.vFabulousServerLoading.visibility = View.VISIBLE
                        }
                        delay(10)
                    }.run {
                        binding.vFabulousServerLoading.visibility = View.GONE
                        fabulous_bottom01()
                    }
                }
            } else {
                fabulous_bottom01()
                fabulousServer()
            }
        }
        binding.vSet.setOnClickListener {
            if (binding.vFabulousAtTouch.visibility == View.VISIBLE) {
                return@setOnClickListener
            }
            if (ProfileManager.isVpnConnecting() ||
                binding.fabulousLayoutHouse.vFabulousConnecting.visibility == View.VISIBLE) {
                toast("VPN is connecting. Please try again later.")
            } else if (ProfileManager.isVpnStopping()) {
                cancelConnectionScope()
                if (binding.fabulousLayoutUniverse.root.visibility == View.VISIBLE) {
                    AircraftFindUtils.adValid("young").also {
                        if (it) {
                            App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this,
                                object : AircraftDisplayListener {
                                    override fun beRefused(reason: String) {
                                        AircraftUtils.print("display young beRefused:$reason")
                                        fabulous_bottom10()
                                    }

                                    override fun startDisplay() {

                                    }

                                    override fun displayFailed() {
                                        AircraftUtils.print("young displayFailed")
                                        fabulous_bottom10()
                                    }

                                    override fun displaySuccess() {
                                        AircraftUtils.print("young displaySuccess")
                                    }

                                    override fun closed() {
                                        AircraftUtils.print("young closed")
                                        fabulous_bottom10()
                                    }

                                })
                        } else {
                            fabulous_bottom10()
                        }
                    }
                } else {
                    fabulous_bottom10()
                }
            } else {
                if (binding.fabulousLayoutUniverse.root.visibility == View.VISIBLE) {
                    AircraftFindUtils.adValid("young").also {
                        if (it) {
                            App.myApplication.aircraftPaintUtils.paintOpenOrIn("young", this,
                                object : AircraftDisplayListener {
                                    override fun beRefused(reason: String) {
                                        AircraftUtils.print("display young beRefused:$reason")
                                        fabulous_bottom10()
                                    }

                                    override fun startDisplay() {

                                    }

                                    override fun displayFailed() {
                                        AircraftUtils.print("young displayFailed")
                                        fabulous_bottom10()
                                    }

                                    override fun displaySuccess() {
                                        AircraftUtils.print("young displaySuccess")
                                    }

                                    override fun closed() {
                                        AircraftUtils.print("young closed")
                                        fabulous_bottom10()
                                    }

                                })
                        } else {
                            fabulous_bottom10()
                        }
                    }
                } else {
                    fabulous_bottom10()
                }
            }
        }
        binding.fabulousLayoutSet.vFabulousSetProxy.setOnClickListener {
            go(ImpelActivity::class.java)
        }
        binding.fabulousLayoutSet.vFabulousSetServer.setOnClickListener {
            fabulous_bottom01()
        }
        binding.fabulousLayoutSet.vFabulousSetShare.setOnClickListener {
            share()
        }
        binding.fabulousLayoutSet.vFabulousSetPolicy.setOnClickListener {
            "https://www.google.com/".go(this)
        }
        binding.vFabulousGuide.setOnClickListener {
            App.myApplication.showTouch = false
            fabulousConnect()
            binding.vFabulousAtTouch.visibility = View.GONE
        }
    }

    fun fabulousObserver() {
        App.myApplication.getViewModel().stateChanged.observe(this) {
            if (binding.fabulousLayoutHouse.vFabulousConnecting.visibility != View.VISIBLE) {
                return@observe
            }
            when (it) {
                BaseService.State.Connected -> {
                    connectingScope?.cancel()
                    connectingScope = lifecycleScope.launch {
                        repeat(AircraftFindUtils.enable("build").run {
                            val n = if (this) 100 else 20
                            fabulousLoadBuild()
                            fabulousLoadYoung()
                            fabulousLoadNative()
                            App.myApplication.aircraftAdUtils.fabulousLoadNative("fooey") {}
                            n
                        }) {
                            delay(100)
                            if (it >= 10 && AircraftFindUtils.adValid("build")) {
                                cancel()
                                App.myApplication.aircraftPaintUtils.paintOpenOrIn("build", this@FabulousActivity,
                                object : AircraftDisplayListener {
                                    override fun beRefused(reason: String) {
                                        AircraftUtils.print("display build beRefused: $reason")
                                        fabulous_vpn10()
                                        App.startCounting()
                                        lifecycleScope.launch {
                                            delay(250)
                                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                                val code = ProfileManager.getCurrentProfileConfig()?.nCode ?: ""
                                                go(ObligeActivity::class.java, code)
                                            }
                                        }
                                    }

                                    override fun startDisplay() {

                                    }

                                    override fun displayFailed() {
                                        AircraftUtils.print("build displayFailed")
                                        fabulous_vpn10()
                                        App.startCounting()
                                        lifecycleScope.launch {
                                            delay(250)
                                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                                val config = ProfileManager.getCurrentProfileConfig()
                                                val code = if (config == null || config.inSmart) {
                                                     ""
                                                } else {
                                                    config.nCode?:""
                                                }
                                                go(ObligeActivity::class.java, code)
                                            }
                                        }
                                    }

                                    override fun displaySuccess() {
                                        AircraftUtils.print("build displaySuccess")
                                    }

                                    override fun closed() {
                                        AircraftUtils.print("build closed")
                                        fabulousLoadBuild()
                                        fabulous_vpn10()
                                        App.startCounting()
                                        lifecycleScope.launch {
                                            delay(250)
                                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                                val config = ProfileManager.getCurrentProfileConfig()
                                                val code = if (config == null || config.inSmart) {
                                                    ""
                                                } else {
                                                    config.nCode?:""
                                                }
                                                go(ObligeActivity::class.java, code)
                                            }
                                        }
                                    }

                                })
                            }
                        }.run {
                            fabulous_vpn10()
                            App.startCounting()
                            delay(250)
                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                val code = ProfileManager.getCurrentProfileConfig()?.nCode ?: ""
                                go(ObligeActivity::class.java, code)
                            }
                        }
                    }
                }

                BaseService.State.Stopped -> {
                    fabulous_vpn00()
                    App.stopCounting()
                    val code = if (ProfileManager.historyProfile != null) {
                        ProfileManager.historyProfile!!.nCode
                    } else {
                        ProfileManager.getCurrentProfileConfig()?.nCode ?: ""
                    }
                    go(ObligeActivity::class.java, code)
                    ProfileManager.historyProfile = null
                }

                else -> {}
            }
        }
        App.myApplication.getViewModel().trafficChanged.observe(this) {
            val rx = it["rx"]
            val tx = it["tx"]
            binding.fabulousLayoutHouse.vFabulousSpeed.vFabulousUpload.text = tx
            binding.fabulousLayoutHouse.vFabulousSpeed.vFabulousDownload.text = rx
            binding.fabulousLayoutSet.fabulousSetSpeed.vFabulousUpload.text = tx
            binding.fabulousLayoutSet.fabulousSetSpeed.vFabulousDownload.text = rx
        }
        App.myApplication.getViewModel().connectAccelerate.observe(this) {
            if (App.myApplication.myHotLaunch) {
                App.myApplication.myHotLaunch = false
                return@observe
            }
            fabulousConnect()
        }
        App.myApplication.getViewModel().connectedCounting.observe(this) {
            binding.fabulousLayoutHouse.vFabulousCounting.text = it
            binding.fabulousLayoutSet.vFabulousCounting.text = it
        }
    }

    fun fabulousLoadBuild() {
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("build", false)
    }

    fun fabulousLoadYoung() {
        App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("young", false)
    }

    fun fabulousIngAnimation() {
        val fadeOut = ScaleAnimation(
            1.0f, 1.5f, 1.0f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        fadeOut.duration = 1000
        fadeOut.repeatCount = Animation.INFINITE
        fadeOut.repeatMode = Animation.REVERSE
        binding.fabulousLayoutHouse.vConnectingCircle.startAnimation(fadeOut)
    }

    fun fabulous_vpn00() {
        binding.fabulousLayoutHouse.vFabulousDisconnect.visibility = View.VISIBLE
        binding.fabulousLayoutHouse.vFabulousConnecting.visibility = View.GONE
        binding.fabulousLayoutHouse.vFabulousConnected.visibility = View.GONE
    }

    fun fabulous_vpn010() {
        binding.fabulousLayoutHouse.vFabulousDisconnect.visibility = View.GONE
        binding.fabulousLayoutHouse.vFabulousConnecting.visibility = View.VISIBLE
        binding.fabulousLayoutHouse.vFabulousConnectingTag.text = "Connecting"
        binding.fabulousLayoutHouse.vFabulousConnected.visibility = View.GONE
        fabulousIngAnimation()
    }

    fun fabulous_vpn011() {
        binding.fabulousLayoutHouse.vFabulousDisconnect.visibility = View.GONE
        binding.fabulousLayoutHouse.vFabulousConnecting.visibility = View.VISIBLE
        binding.fabulousLayoutHouse.vFabulousConnectingTag.text = "Disconnecting"
        binding.fabulousLayoutHouse.vFabulousConnected.visibility = View.GONE
        fabulousIngAnimation()
    }

    fun fabulous_vpn10() {
        binding.fabulousLayoutHouse.vFabulousDisconnect.visibility = View.GONE
        binding.fabulousLayoutHouse.vFabulousConnecting.visibility = View.GONE
        binding.fabulousLayoutHouse.vFabulousConnected.visibility = View.VISIBLE
    }

    fun fabulous_bottom00() {
        binding.fabulousLayoutHouse.root.visibility = View.VISIBLE
        binding.fabulousLayoutUniverse.root.visibility = View.GONE
        binding.fabulousLayoutSet.root.visibility = View.GONE
        binding.vHouseRes.isSelected = true
        binding.vUniverseRes.isSelected = false
        binding.vSetRes.isSelected = false
        fabulousResumeRefresh()
        if (ProfileManager.isVpnConnected() || ProfileManager.isVpnStopping()) {
            ProfileManager.vpnState = BaseService.State.Connected
            fabulous_vpn10()
        } else if (ProfileManager.isVpnStopped() || ProfileManager.isVpnConnecting()) {
            ProfileManager.vpnState = BaseService.State.Stopped
            fabulous_vpn00()
        }
    }

    fun fabulous_bottom01() {
        binding.fabulousLayoutHouse.root.visibility = View.GONE
        binding.fabulousLayoutUniverse.root.visibility = View.VISIBLE
        binding.fabulousLayoutSet.root.visibility = View.GONE
        binding.vHouseRes.isSelected = false
        binding.vUniverseRes.isSelected = true
        binding.vSetRes.isSelected = false
        AircraftFindUtils.adValid("young").also {
            if (!it) {
                App.myApplication.aircraftAdUtils.fabulousLoadOpenOrIn("young", false)
            }
        }
    }

    fun fabulous_bottom10() {
        binding.fabulousLayoutHouse.root.visibility = View.GONE
        binding.fabulousLayoutUniverse.root.visibility = View.GONE
        binding.fabulousLayoutSet.root.visibility = View.VISIBLE
        binding.vHouseRes.isSelected = false
        binding.vUniverseRes.isSelected = false
        binding.vSetRes.isSelected = true
    }

    fun fabulousServer() {
        val empty = ProfileManager.allServers.isEmpty()
        empty.also {
            if (it) {
                binding.fabulousLayoutUniverse.vServerSmart.visibility = View.GONE
            } else {
                binding.fabulousLayoutUniverse.vServerSmart.visibility = View.VISIBLE
                val headers = mutableListOf<Pair<String, String>>()
                val children = mutableMapOf<String, MutableList<Profile>>()
                ProfileManager.allServers.also {
                    it.forEach { p ->
                        if (headers.contains(Pair(p.nCode, p.nName))) {
                            val list = children[p.nCode] ?: mutableListOf()
                            list.add(p)
                        } else {
                            headers.add(Pair(p.nCode, p.nName))
                            children.put(p.nCode, mutableListOf(p))
                        }
                    }
                }
                val profile = ProfileManager.getProfile(DataStore.profileId)
                binding.fabulousLayoutUniverse.vServerSmartInclude.vServerSelect.isSelected =
                    profile?.inSmart ?: true

                binding.fabulousLayoutUniverse.vServerSmart.setOnClickListener {
                    (profile?.inSmart ?: true).also {
                        if (it && ProfileManager.isVpnConnected()) {
                            return@setOnClickListener
                        } else if (it && !ProfileManager.isVpnConnected()) {
                            fabulous_bottom00()
                            fabulousConnect()
                        } else if (!it && !ProfileManager.isVpnConnected()) {
                            val new = ProfileManager.saveToHistoryProfile()
                            ProfileManager.delProfile(new.id)
                            fabulous_bottom00()
                            fabulousConnect()
                        } else if (!it && ProfileManager.isVpnConnected()) {
                            this.displayChoose {
                                val new = ProfileManager.saveToHistoryProfile()
                                ProfileManager.delProfile(new.id)
                                fabulous_bottom00()
                                fabulousStopConnect()
                            }
                        }
                    }
                }

                val expandAdapter = FabulousServerAdapter(
                    this,
                    ProfileManager.getCurrentProfileConfig(), Pair(headers, children)
                ) {
                    val profile = ProfileManager.getProfile(DataStore.profileId)
                    val selected =
                        profile != null && !profile.inSmart && profile.host == it.host && profile.remotePort == it.remotePort
                    if (selected && ProfileManager.isVpnConnected()) {
                        return@FabulousServerAdapter
                    } else if (selected && !ProfileManager.isVpnConnected()) {
                        fabulous_bottom00()
                        fabulousConnect()
                    } else if (!selected && !ProfileManager.isVpnConnected()) {
                        it.id = profile?.id ?: ProfileManager.createProfile().id
                        ProfileManager.updateProfile(it)
                        fabulous_bottom00()
                        fabulousConnect()
                    } else if (!selected && ProfileManager.isVpnConnected()) {
                        this.displayChoose {
                            val new = ProfileManager.saveToHistoryProfile()
                            it.id = new.id
                            ProfileManager.updateProfile(it)
                            fabulous_bottom00()
                            fabulousStopConnect()
                        }
                    }
                }
                binding.fabulousLayoutUniverse.vServerExpandView.setAdapter(expandAdapter)
            }
        }
    }
}