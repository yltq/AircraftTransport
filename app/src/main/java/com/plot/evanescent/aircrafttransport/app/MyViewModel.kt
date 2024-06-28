package com.plot.evanescent.aircrafttransport.app

import android.content.Context
import android.net.ConnectivityManager
import android.text.format.Formatter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceDataStore
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.aidl.TrafficStats
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.OnPreferenceDataStoreChangeListener
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils

class MyViewModel : ViewModel(), OnPreferenceDataStoreChangeListener,
    ShadowsocksConnection.Callback {
    var byPassInMain = {
        AircraftAdUtils.good == "1" || (AircraftAdUtils.good == "3" && AircraftUtils.aircraftFb() == "user0")
    }
    var stateChanged: MutableLiveData<BaseService.State> = MutableLiveData()
    var trafficChanged: MutableLiveData<Map<String, String>> = MutableLiveData()
    var connectAccelerate: MutableLiveData<String> = MutableLiveData()
    var connectedCounting: MutableLiveData<String> = MutableLiveData()

    fun netEnable(): Boolean {
        val connectivityManager =
            App.myApplication.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun locationLimited(): Boolean {
        return mutableListOf<String>("IR", "HK", "CN", "MO").count { it == App.myApplication.getNetViewModel().code } > 0
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        ProfileManager.vpnState = state
        stateChanged.postValue(state)
    }

    override fun onServiceConnected(service: IShadowsocksService) {

    }

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {

    }

    override fun trafficUpdated(profileId: Long, stats: TrafficStats) {
        super.trafficUpdated(profileId, stats)
        val rx = String.format("%s/s", Formatter.formatFileSize(App.myApplication, stats.rxRate))
        val tx = String.format("%s/s", Formatter.formatFileSize(App.myApplication, stats.txRate))
        mutableMapOf<String, String>(
            "rx" to rx,
            "tx" to tx
        ).also {
            trafficChanged.postValue(it)
        }
    }

    fun startConnect() {
        ProfileManager.allServers.takeIf { !it.isNullOrEmpty() }?:return
        val config = ProfileManager.getCurrentProfileConfig()
        when(config == null) {
            true -> {
                val random = ProfileManager.putSmartProfileConfig()?.also {
                    it.byPassInMain = byPassInMain()
                    it.agencys = AircraftUtils.impelAppsByPassOpen
                    ProfileManager.updateProfile(it)
                    Core.switchProfile(it.id)
                    Core.startService()
                }
            }
            false -> {
                config.byPassInMain = byPassInMain()
                config.agencys = AircraftUtils.impelAppsByPassOpen
                ProfileManager.updateProfile(config)
                Core.switchProfile(config.id)
                Core.startService()
            }
        }
    }

    fun stopConnect() {
        Core.stopService()
    }

}