package com.plot.evanescent.aircrafttransport.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.lifecycle.ViewModelProvider
import com.github.shadowsocks.Core
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.module.FabulousActivity
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftFindUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftPaintUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.toDisplayString
import com.plot.evanescent.aircrafttransport.utils.MyAppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.properties.Delegates

class App : Application() {
    private lateinit var myViewModel: MyViewModel
    private lateinit var myNetViewModel: MyNetViewModel
    var showTouch: Boolean = false
    var gaid: String = ""
    var launchLimited: String = ""
    var myAppRefer: String = ""
    var aircraftAdUtils: AircraftAdUtils = AircraftAdUtils()
    var aircraftFindUtils: AircraftFindUtils = AircraftFindUtils()
    var aircraftPaintUtils: AircraftPaintUtils = AircraftPaintUtils()
    var myHotLaunch: Boolean = false
    lateinit var sharedPref: SharedPreferences

    companion object {
        var myApplication: App by Delegates.notNull()
        var myCountingJob: Job? = null

        fun startCounting() {
            myCountingJob?.cancel()
            ProfileManager.vpnConnectedTime = 0
            myCountingJob = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    delay(1000)
                    ProfileManager.vpnConnectedTime++
                    App.myApplication.getViewModel().connectedCounting
                        .postValue(ProfileManager.vpnConnectedTime.toDisplayString())
                }
            }
        }

        fun stopCounting() {
            myCountingJob?.cancel()
            App.myApplication.getViewModel().connectedCounting
                .postValue(ProfileManager.vpnConnectedTime.toDisplayString())
        }
    }

    fun updateDT(dt: String) {
        sharedPref.edit().putString("aircraft_date", dt).apply()
    }

    fun updateNS(ns: Int) {
        sharedPref.edit().putInt("aircraft_number_show", ns).apply()
    }

    fun updateNC(nc: Int) {
        sharedPref.edit().putInt("aircraft_number_click", nc).apply()
    }

    override fun onCreate() {
        super.onCreate()
        myApplication = this
        sharedPref = getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
        myViewModel =
            ViewModelProvider.AndroidViewModelFactory(this).create(MyViewModel::class.java)
        myNetViewModel =
            ViewModelProvider.AndroidViewModelFactory(this).create(MyNetViewModel::class.java)
        registerActivityLifecycleCallbacks(MyAppUtils())
        Core.init(this, FabulousActivity::class)
        AircraftUtils.aircraftInMain(this).run {
            if (this) {
                initCache()
                Core.stopService()
                showTouch = true
                Firebase.initialize(this@App)
                MobileAds.initialize(this@App)
                aircraftAdUtils.resolveAdString("")
                aircraftAdUtils.resolveEvenString("")
                getNetViewModel().aircraftFirebase(true)
                AircraftUtils.aircraftUUID = AircraftUtils.aircraftUUID.run {
                    val id =
                    if (TextUtils.isEmpty(this)) {
                         UUID.randomUUID().toString()
                    } else {
                        this
                    }
                    id
                }
                getNetViewModel().aircraftAdjust(this@App)
            }
        }
    }

    fun initCache() {
        myAppRefer = sharedPref.getString("aircraft_my_refer", "")?:""
        AircraftAdUtils.aircraftAdMax.dt = sharedPref.getString("aircraft_date", "")?:""
        sharedPref.getInt("aircraft_number_show", 0).also {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            AircraftUtils.print("dt:${AircraftAdUtils.aircraftAdMax.dt}, date:$date")
            if (date == AircraftAdUtils.aircraftAdMax.dt) {
                AircraftAdUtils.aircraftAdMax.nS = it
            } else {
                AircraftAdUtils.aircraftAdMax.dt = date
                AircraftAdUtils.aircraftAdMax.nC = 0
                AircraftAdUtils.aircraftAdMax.nS = 0
                updateNS(AircraftAdUtils.aircraftAdMax.nS)
                updateNC(AircraftAdUtils.aircraftAdMax.nC)
                updateDT(AircraftAdUtils.aircraftAdMax.dt)
            }
        }
        sharedPref.getInt("aircraft_number_click", 0).also {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            if (date == AircraftAdUtils.aircraftAdMax.dt) {
                AircraftAdUtils.aircraftAdMax.nC = it
            } else {
                AircraftAdUtils.aircraftAdMax.dt = date
                AircraftAdUtils.aircraftAdMax.nS = 0
                AircraftAdUtils.aircraftAdMax.nC = 0
                updateNS(AircraftAdUtils.aircraftAdMax.nS)
                updateDT(AircraftAdUtils.aircraftAdMax.dt)
                updateNC(AircraftAdUtils.aircraftAdMax.nC)
            }
        }
    }

    fun getViewModel() = myViewModel
    fun getNetViewModel() = myNetViewModel
}