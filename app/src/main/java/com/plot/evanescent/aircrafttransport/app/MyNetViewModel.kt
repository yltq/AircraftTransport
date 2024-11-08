package com.plot.evanescent.aircrafttransport.app

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import android.webkit.WebView
import androidx.annotation.Keep
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.facebook.appevents.AppEventsLogger
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.result.MyImpelData
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.aircraftAdminType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Currency
import java.util.Locale

class MyNetViewModel : ViewModel() {
    var code: String = Locale.getDefault().country
    var host: String = ""
    var loadingSever: Boolean = false
    var loadingApps: MutableLiveData<MutableMap<String, MyImpelData>> = MutableLiveData()
    val fb by lazy { AppEventsLogger.newLogger(App.myApplication) }
    var loadServerSuccess: Boolean = false //成功加载服务器配置信息
    var loadFirebaseDataSuccess: Boolean = false //成功加载firebase远端数据
    fun aircraftGetHost() {
        AircraftUtils.aircraftGetMethodInParam("https://ipinfo.io/json", null,
        mutableMapOf("User-Agent" to WebView(App.myApplication).settings.userAgentString)
        ) { body ->
            val result =
                body.string().takeIf { !TextUtils.isEmpty(it) } ?: return@aircraftGetMethodInParam
            val json = kotlin.runCatching {
                JSONObject(result)
            }.onSuccess {
                AircraftUtils.print("aircraftGetHost success:${it}")
                code = it.optString("country").ifEmpty { Locale.getDefault().country }
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
                Pair("casanova", BuildConfig.VERSION_NAME),
                Pair("calculi", "chimera"),
                Pair("claim", String(Base64.decode("Y29tLnNreXN0cmVhbS5mYXN0bGluay51bmxpbWl0ZWQuc3RhYmxl", Base64.DEFAULT)))
            ), mutableMapOf()
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
                loadServerSuccess = true //成功加载服务器列表信息
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
            App.myApplication.sharedPref.getString("aircraft_my_refer", "").also {
                if (!TextUtils.isEmpty(it)) {
                    App.myApplication.myAppRefer = it!!
                    App.myApplication.myAppRefer.aircraftAdminType(true)
                    cancel()
                    return@launch
                }
                InstallReferrerClient.newBuilder(App.myApplication).build().also {
                    it.startConnection(object : InstallReferrerStateListener {
                        override fun onInstallReferrerSetupFinished(p0: Int) {
                            (p0 == InstallReferrerClient.InstallReferrerResponse.OK).also { ok ->
                                if (ok) {
                                    kotlin.runCatching {
                                        it.installReferrer
                                    }.onSuccess {
                                        App.myApplication.myAppRefer = it?.installReferrer ?: ""
                                        App.myApplication.myAppRefer.aircraftAdminType(true)

                                        App.myApplication.sharedPref.edit().putString("aircraft_my_refer", App.myApplication.myAppRefer).apply()
                                        AircraftUtils.aircraftPostJsonMethod(
                                            if (BuildConfig.DEBUG)
                                                "https://test-peggy.stablefasttunnel.com/impelled/prom/nikko"
                                            else "https://peggy.stablefasttunnel.com/monetary/gazebo",
                                            "afflict", JSONObject().run {
                                                putOpt("karyatid", "build/" + Build.ID)
                                                putOpt("auxin", it?.installReferrer ?: "")
                                                putOpt("slice", it?.installVersion ?: "")
                                                putOpt(
                                                    "audible",
                                                    WebView(App.myApplication).settings.userAgentString
                                                )
                                                putOpt(
                                                    "vikram",
                                                    if (TextUtils.isEmpty(App.myApplication.launchLimited)) "script" else App.myApplication.launchLimited
                                                )
                                                putOpt("crete", it?.referrerClickTimestampSeconds ?: "")
                                                putOpt(
                                                    "envelope",
                                                    it?.installBeginTimestampSeconds ?: ""
                                                )
                                                putOpt(
                                                    "leaky",
                                                    it?.referrerClickTimestampServerSeconds ?: ""
                                                )
                                                putOpt(
                                                    "wu",
                                                    it?.installBeginTimestampServerSeconds ?: ""
                                                )
                                                putOpt(
                                                    "kaddish",
                                                    App.myApplication.packageManager.getPackageInfo(
                                                        App.myApplication.packageName,
                                                        0
                                                    ).firstInstallTime
                                                )
                                                putOpt(
                                                    "sci",
                                                    App.myApplication.packageManager.getPackageInfo(
                                                        App.myApplication.packageName,
                                                        0
                                                    ).lastUpdateTime
                                                )
                                                this
                                            }, false
                                        ) {}
                                    }.onFailure {
                                        AircraftUtils.print("installReferrer error ${it.message}")
                                    }
                                }
                            }
                        }

                        override fun onInstallReferrerServiceDisconnected() {

                        }

                    })
                }
            }
        }
    }

    fun aircraftFirebase(next: Boolean) {
        BuildConfig.DEBUG.also {
            if (it) return
            val remote = Firebase.remoteConfig
            remote.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful) {
                    remote.getString("dead").also {
                        if (it.isNotEmpty()) {
                            AircraftUtils.print("firebase dead is : $it")
                            App.myApplication.aircraftAdUtils.resolveEvenString(it)
                        }
                    }
                    remote.getString("even").also {
                        if (it.isNotEmpty()) {
                            App.myApplication.aircraftAdUtils.resolveFbString(it)
                        }
                    }
//                    remote.getString("sake").also {
//                        if (it.isNotEmpty()) {
//                            App.myApplication.aircraftAdUtils.resolveAdString(it)
//                        }
//                    }

                    remote.getString("sky_vas").also {
                        if (it.isNotEmpty()) {
                            App.myApplication.aircraftAdUtils.resolveAdMode2String(it)
                        }
                    }

                    loadFirebaseDataSuccess = true //成功获取firebase配置
                } else {
                    if (!next) {
                        AircraftAdUtils.initAircraftFacebook("")
                        return@addOnCompleteListener
                    }
                    aircraftFirebase(false)
                }
            }.addOnFailureListener {
                if (!next) {
                    AircraftAdUtils.initAircraftFacebook("")
                    return@addOnFailureListener
                }
                aircraftFirebase(false)
            }
        }
    }

    fun aircraftAdjust(context: Context) {
        val appToken = "ih2pm2dr3k74"
        val environment =
            if (!BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_PRODUCTION else AdjustConfig.ENVIRONMENT_SANDBOX
        val config = AdjustConfig(context, appToken, environment)
        config.setLogLevel(LogLevel.WARN)
        Adjust.addSessionCallbackParameter("customer_user_id", AircraftUtils.aircraftUUID)
        config.delayStart = 5.5
        Adjust.onCreate(config)
    }

    fun aircraftFireUpload(valueMicros: Long, currencyCode: String, adSourceName: String) {
        if (BuildConfig.DEBUG) return
        val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        adRevenue.setRevenue(
            valueMicros / 1000000.0,
            currencyCode
        )
        adRevenue.adRevenueNetwork = adSourceName
        Adjust.trackAdRevenue(adRevenue)

        fb.logPurchase((valueMicros / 1000000.0).toBigDecimal(), Currency.getInstance("USD"))
    }

}