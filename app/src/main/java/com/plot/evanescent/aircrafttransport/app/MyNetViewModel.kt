package com.plot.evanescent.aircrafttransport.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.result.MyImpelData
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

class MyNetViewModel : ViewModel() {
    var code: String = Locale.getDefault().country
    var host: String = ""
    var loadingSever: Boolean = false
    var loadingApps: MutableLiveData<MutableMap<String, MyImpelData>> = MutableLiveData()
    fun aircraftGetHost() {
        AircraftUtils.aircraftGetMethodInParam("https://ip.seeip.org/geoip/", null) { body ->
            val result =
                body.string().takeIf { !TextUtils.isEmpty(it) } ?: return@aircraftGetMethodInParam
            val json = kotlin.runCatching {
                JSONObject(result)
            }.onSuccess {
                AircraftUtils.print("aircraftGetHost success:${it}")
                code = it.optString("country_code").ifEmpty { Locale.getDefault().country }
                host = it.optString("ip")
            }.onFailure {
                AircraftUtils.print("aircraftGetHost error:${it.message}")
            }
        }
    }

    fun aircraftGetCloak() {
        AircraftUtils.aircraftGetMethodInParam(
            "https://stole.stablefasttunnel.com/ubiquity/goodrich/swishy",
            mutableListOf(
                Pair("honoree", AircraftUtils.aircraftUUID),
                Pair("sweeten", App.myApplication.gaid),
                Pair("horny", Build.BRAND),
                Pair("slept", System.currentTimeMillis().toString()),
                Pair("casanova", BuildConfig.VERSION_NAME),
                Pair("calculi", "chimera"),
                Pair("opt", AircraftUtils.aircraftUUID),
                Pair("claim", BuildConfig.APPLICATION_ID)
            )
        ) { body ->
            AircraftUtils.aircraftCloak = body.string()
        }
    }

    fun aircraftGetServer() {
        loadingSever = true
        val url = BuildConfig.DEBUG.run {
            (if (this) "https://test.stablefasttunnel.com"
            else "https://api.stablefasttunnel.com") + "/KFNmgQXmR/jqYjErwUu/fBlugisrK/"
        }
        AircraftUtils.aircraftGetMethodInHeader(
            url, mutableMapOf(
                "QWXM" to code.ifEmpty { "ZZ" },
                "URMEM" to BuildConfig.APPLICATION_ID
            )
        ) { body ->
            loadingSever = false
            if (body == null) return@aircraftGetMethodInHeader
            val result = body.string().takeIf { !TextUtils.isEmpty(it) && it.length > 18 }
                ?: return@aircraftGetMethodInHeader

            val after = result.run {
                drop(18).reversed()
            }
            aircraftResolveServerJson(String(Base64.decode(after, 0)))
        }
    }

    private fun aircraftResolveServerJson(string: String) {
        val json = kotlin.runCatching {
            JSONObject(string)
        }.onSuccess {
            it.optJSONObject("data")?.run {
                optJSONArray("UdFpHB")?.also { fasts ->
                    if (fasts.length() > 0) {
                        ProfileManager.smartServers.clear()
                        for (i in 0 until fasts.length()) {
                            fasts.optJSONObject(i)?.also { p ->
                                Profile(
                                    host = p.optString("LWqASpeEb"),
                                    remotePort = p.optInt("CwuoRMraQo"),
                                    password = p.optString("sYLnXf"),
                                    method = p.optString("cSHKqjv"),
                                    cName = p.optString("vZIcCsb"),
                                    nName = p.optString("cNZkNj"),
                                    nCode = p.optString("hyFndmXoNW")
                                ).run {
                                    ProfileManager.smartServers.add(this)
                                }
                            }
                        }
                    }
                }
                optJSONArray("RXsWXll")?.also { servers ->
                    if (servers.length() > 0) {
                        ProfileManager.allServers.clear()
                        for (i in 0 until servers.length()) {
                            servers.optJSONObject(i)?.also { p ->
                                Profile(
                                    host = p.optString("LWqASpeEb"),
                                    remotePort = p.optInt("CwuoRMraQo"),
                                    password = p.optString("sYLnXf"),
                                    method = p.optString("cSHKqjv"),
                                    cName = p.optString("vZIcCsb"),
                                    nName = p.optString("cNZkNj"),
                                    nCode = p.optString("hyFndmXoNW")
                                ).run {
                                    ProfileManager.allServers.add(this)
                                }
                            }
                        }
                    }
                }

            }
        }.onFailure {
            AircraftUtils.print("aircraftResolveServerJson error:${it.message}")
        }
    }

    @Keep
    fun getProfileNationId(string: String): Int {
        val code = string.ifEmpty { "server_fast" }
        val resourceId = App.myApplication.resources.getIdentifier(
            code.toLowerCase(),
            "mipmap",
            App.myApplication.packageName
        )
        return if (resourceId != 0) {
            resourceId
        } else {
            R.mipmap.server_fast
        }
    }

    fun aircraftResolveApps(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            context.run {
                val intent = Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                val map: MutableMap<String, MyImpelData> = mutableMapOf()
                val list = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.queryIntentActivities(
                        intent,
                        PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
                    )
                } else {
                    packageManager.queryIntentActivities(intent, 0)
                }).also {
                    it.takeIf { !it.isNullOrEmpty() }?.also {
                        it.forEach { info ->
                            info.activityInfo.also {
                                if (it.packageName != BuildConfig.APPLICATION_ID) {
                                    map[it.packageName] = MyImpelData(
                                        it.loadIcon(packageManager), it.packageName,
                                        it.loadLabel(packageManager).toString()
                                    )
                                }
                            }
                        }
                    }
                }
                loadingApps.postValue(map)
            }
        }
    }

    fun aircraftMyRefer() {
        CoroutineScope(Dispatchers.IO).launch {
            InstallReferrerClient.newBuilder(App.myApplication).build().also {
                it.startConnection(object : InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(p0: Int) {
                        (p0 == InstallReferrerClient.InstallReferrerResponse.OK).also {ok ->
                            if (ok) {
                                App.myApplication.myAppRefer = it.installReferrer.installReferrer
                            }
                        }
                    }

                    override fun onInstallReferrerServiceDisconnected() {

                    }

                })
            }
        }
    }

    fun aircraftFirebase() {
        BuildConfig.DEBUG.also {
            if (it) return
            val remote = Firebase.remoteConfig
            remote.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    val dead = remote.getString("dead").also {
                        if (it.isNotEmpty()) {
                            App.myApplication.aircraftAdUtils.resolveEvenString(it)
                        }
                    }
                    remote.getString("even").also {
                        if (it.isNotEmpty()) {
                            App.myApplication.aircraftAdUtils.resolveFbString(it)
                        }
                    }
                    remote.getString("sake").also {
                        if (it.isNotEmpty()) {
                            App.myApplication.aircraftAdUtils.resolveAdString(it)
                        }
                    }

                }
            }
        }
    }
}