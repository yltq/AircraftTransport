package com.plot.evanescent.aircrafttransport.utils

import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.ftIngCache
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AircraftFindUtils {
    companion object {
        fun enable(name: String): Boolean {
            return when (name) {
                "fooey",
                "annex" -> true

                "dimily" -> (AircraftAdUtils.bell == "2" ||
                        AircraftAdUtils.bell == "1" && AircraftUtils.aircraftCloak == "there") &&
                        ftIngCache == "1"

                "build" -> (AircraftAdUtils.bell == "2" ||
                        AircraftAdUtils.bell == "1" && AircraftUtils.aircraftCloak == "there")

                "young" -> (AircraftAdUtils.bell == "2" ||
                        AircraftAdUtils.bell == "1" && AircraftUtils.aircraftCloak == "there") &&
                        ftIngCache == "1"

                else -> true
            }
        }

        fun getShowIp(): String {
            return if (App.myApplication.getViewModel().stateConnected) {
                if (ProfileManager.historyProfile != null && ProfileManager.isVpnStopping()) {
                    ProfileManager.historyProfile!!.host
                } else ProfileManager.getProfile(
                    DataStore.profileId
                )?.host ?: ""
            } else "aircraft_none"
        }

        fun getUploadIp(): String {
            return if (App.myApplication.getViewModel().stateConnected) {
                if (ProfileManager.historyProfile != null && ProfileManager.isVpnStopping()) {
                    ProfileManager.historyProfile!!.host
                } else ProfileManager.getProfile(
                    DataStore.profileId
                )?.host ?: ""
            } else App.myApplication.getNetViewModel().host
        }

        fun getShowCity(): String {
            return if (App.myApplication.getViewModel().stateConnected) {
                if (ProfileManager.historyProfile != null && ProfileManager.isVpnStopping()) {
                    ProfileManager.historyProfile!!.cName
                } else ProfileManager.getProfile(
                    DataStore.profileId
                )?.cName ?: ""
            } else "none"
        }

        fun adValid(name: String): Boolean {
            val aircraftLoadAd = getLoadAd(name) ?: return false
            val showIp = getShowIp()


            return aircraftLoadAd.ad != null &&
                    System.currentTimeMillis() - aircraftLoadAd.dt < 3600000 && aircraftLoadAd.loadIp == showIp
        }

        fun adLoading(name: String): Boolean {
            val connect = App.myApplication.getViewModel().stateConnected
            return when (name) {
                "annex" -> if (connect) AircraftAdUtils.annexLoad.loading else AircraftAdUtils.annexLoadDis.loading
                "dimily" -> if (connect) AircraftAdUtils.dimilyLoad.loading else AircraftAdUtils.dimilyLoadDis.loading
                "fooey" -> if (connect) AircraftAdUtils.fooeyLoad.loading else AircraftAdUtils.fooeyLoadDis.loading
                "build" -> if (connect) AircraftAdUtils.buildLoad.loading else AircraftAdUtils.buildLoadDis.loading
                "young" -> if (connect) AircraftAdUtils.youngLoad.loading else AircraftAdUtils.youngLoadDis.loading
                else -> false
            }
        }

        fun getLoadList(name: String): MutableList<AircraftAd> {
            val connect = App.myApplication.getViewModel().stateConnected
            return when (name) {
                "annex" -> if (connect) AircraftAdUtils.annex else AircraftAdUtils.annexDis
                "dimily" -> if (connect) AircraftAdUtils.dimily else AircraftAdUtils.dimilyDis
                "fooey" -> if (connect) AircraftAdUtils.fooey else AircraftAdUtils.fooeyDis
                "build" -> if (connect) AircraftAdUtils.build else AircraftAdUtils.buildDis
                "young" -> if (connect) AircraftAdUtils.young else AircraftAdUtils.youngDis
                else -> mutableListOf()
            }
        }

        fun getLoadAd(name: String): AircraftLoadAd? {
            val connect = App.myApplication.getViewModel().stateConnected
            return when (name) {
                "annex" -> if (connect) AircraftAdUtils.annexLoad else AircraftAdUtils.annexLoadDis
                "dimily" -> if (connect) AircraftAdUtils.dimilyLoad else AircraftAdUtils.dimilyLoadDis
                "fooey" -> if (connect) AircraftAdUtils.fooeyLoad else AircraftAdUtils.fooeyLoadDis
                "build" -> if (connect) AircraftAdUtils.buildLoad else AircraftAdUtils.buildLoadDis
                "young" -> if (connect) AircraftAdUtils.youngLoad else AircraftAdUtils.youngLoadDis
                else -> null
            }
        }

        var loadEventPaidList: MutableList<Pair<Any, Map<String, String>>> = mutableListOf()
    }

    fun loadOpenOrIn(
        name: String,
        ad: MutableList<AircraftAd>,
        twice: Boolean,
        listener: AircraftLoadListener
    ) {
        if (enable(name).not()) {
            listener.beRefused("config")
            return
        }
        if (AircraftAdUtils.aircraftAdMax.nS >= AircraftAdUtils.aircraftAdMax.tun1ad ||
            AircraftAdUtils.aircraftAdMax.nC >= AircraftAdUtils.aircraftAdMax.tun2ad) {
            listener.beRefused("over")
            return
        }
        if (adValid(name)) {
            listener.beRefused("ad_valid")
            return
        }
        if (adLoading(name)) {
            listener.beRefused("loading")
            return
        }
        if (ad.isEmpty()) {
            listener.beRefused("no_ad_id")
            return
        }
        getLoadAd(name).also { loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            listener.startLoad()
            AircraftUtils.print("$name startLoad-----connected=${loadAd.loadVpnConnected}----")
            loadAd.loading = true
            ad[loadAd.place].also {
                val uploadIp = getUploadIp()

                val showIp =  getShowIp()

                val city = getShowCity()

                if (it.tun4ad == "bat") {
                    AppOpenAd.load(
                        App.myApplication,
                        it.tun3ad,
                        AdRequest.Builder().build(),
                        object : AppOpenAd.AppOpenAdLoadCallback() {
                            override fun onAdLoaded(openAd: AppOpenAd) {
                                AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---success-------")
                                loadAd.loadIp = showIp
                                loadAd.place = 0
                                loadAd.ad = openAd
                                loadAd.dt = System.currentTimeMillis()
                                if (loadEventPaidList.isEmpty() || loadEventPaidList.count { it.first == openAd } == 0) {
                                    loadEventPaidList.add(
                                        Pair(
                                            openAd, mutableMapOf(
                                                "portage" to name,
                                                "disjunct" to uploadIp,
                                                "grotto" to openAd.adUnitId,
                                                "bame" to city,
                                            )
                                        )
                                    )
                                }
                                openAd.setOnPaidEventListener {
                                    if (loadEventPaidList.isNotEmpty() && loadEventPaidList.count { it.first == openAd } > 0) {
                                        val p000 =
                                            loadEventPaidList.find { it.first == openAd }
                                                ?: return@setOnPaidEventListener

                                        App.myApplication.getNetViewModel().aircraftFireUpload(
                                            it.valueMicros, it.currencyCode,
                                            openAd.responseInfo?.loadedAdapterResponseInfo?.adSourceName
                                                ?: ""
                                        )

                                        AircraftUtils.aircraftPostJsonMethod(
                                            if (BuildConfig.DEBUG)
                                                "https://test-peggy.stablefasttunnel.com/impelled/prom/nikko"
                                            else "https://peggy.stablefasttunnel.com/monetary/gazebo",
                                            "redound",
                                            JSONObject().run {
                                                putOpt("rinse", it.valueMicros)
                                                putOpt("emanuel", it.currencyCode)
                                                putOpt("chomsky", "admob")
                                                putOpt("neff", "admob")
                                                putOpt("grotto", p000.second["grotto"])
                                                putOpt("portage", p000.second["portage"])

                                                putOpt("bame", p000.second["bame"])

                                                val city111 = getShowCity()
                                                putOpt("kace", city111)

                                                putOpt("tobago", "open")
                                                putOpt(
                                                    "peach", arrayListOf(
                                                        "UNKNOWN",
                                                        "ESTIMATED",
                                                        "PUBLISHER_PROVIDED",
                                                        "PRECISE"
                                                    )[it.precisionType]
                                                )
                                                putOpt("disjunct", p000.second["disjunct"])
                                                val ip111 = getUploadIp()
                                                putOpt("kankakee", ip111)
                                                this
                                            }, false
                                        ) {}
                                    }
                                }
                                loadAd.loading = false
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---failed-------${loadAdError.message}")
                                loadAd.loading = false
                                if (loadAd.place < ad.size - 1) {
                                    loadAd.place++
                                    loadOpenOrIn(name, ad, twice, listener)
                                    return
                                }
                                loadAd.place = 0
                                if (twice) {
                                    loadOpenOrIn(name, ad, false, listener)
                                }
                            }
                        })
                } else if (it.tun4ad == "cow") {
                    InterstitialAd.load(
                        App.myApplication,
                        it.tun3ad,
                        AdRequest.Builder().build(),
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---success-------")
                                loadAd.loadIp = showIp
                                loadAd.place = 0
                                loadAd.ad = interstitialAd
                                loadAd.dt = System.currentTimeMillis()
                                if (loadEventPaidList.isEmpty() || loadEventPaidList.count { it.first == interstitialAd } == 0) {
                                    loadEventPaidList.add(
                                        Pair(
                                            interstitialAd, mutableMapOf(
                                                "portage" to name,
                                                "disjunct" to uploadIp,
                                                "grotto" to interstitialAd.adUnitId,
                                                "bame" to city,
                                            )
                                        )
                                    )
                                }
                                interstitialAd.setOnPaidEventListener {
                                    if (loadEventPaidList.isNotEmpty() && loadEventPaidList.count { it.first == interstitialAd } > 0) {
                                        val p000 =
                                            loadEventPaidList.find { it.first == interstitialAd }
                                                ?: return@setOnPaidEventListener

                                        App.myApplication.getNetViewModel().aircraftFireUpload(
                                            it.valueMicros, it.currencyCode,
                                            interstitialAd.responseInfo?.loadedAdapterResponseInfo?.adSourceName
                                                ?: ""
                                        )

                                        AircraftUtils.aircraftPostJsonMethod(
                                            if (BuildConfig.DEBUG)
                                                "https://test-peggy.stablefasttunnel.com/impelled/prom/nikko"
                                            else "https://peggy.stablefasttunnel.com/monetary/gazebo",
                                            "redound",
                                            JSONObject().run {
                                                putOpt("rinse", it.valueMicros)
                                                putOpt("emanuel", it.currencyCode)
                                                putOpt("chomsky", "admob")
                                                putOpt("neff", "admob")
                                                putOpt("grotto", p000.second["grotto"])
                                                putOpt("portage", p000.second["portage"])

                                                putOpt("bame", p000.second["bame"])

                                                val city111 = getShowCity()
                                                putOpt("kace", city111)

                                                putOpt("tobago", "inter")
                                                putOpt(
                                                    "peach", arrayListOf(
                                                        "UNKNOWN",
                                                        "ESTIMATED",
                                                        "PUBLISHER_PROVIDED",
                                                        "PRECISE"
                                                    )[it.precisionType]
                                                )
                                                putOpt("disjunct", p000.second["disjunct"])
                                                val ip111 = getUploadIp()
                                                putOpt("kankakee", ip111)
                                                this
                                            }, false
                                        ) {}
                                    }
                                }
                                loadAd.loading = false
                                listener.loadSuccess()
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---failed-------${loadAdError.message}")
                                listener.loadFailed()
                                loadAd.loading = false
                                if (loadAd.place < ad.size - 1) {
                                    loadAd.place++
                                    loadOpenOrIn(name, ad, twice, listener)
                                    return
                                }
                                loadAd.place = 0
                                if (twice) {
                                    loadOpenOrIn(name, ad, false, listener)
                                }
                            }
                        })
                }
            }
        }
    }

    fun loadNative(
        name: String,
        ad: MutableList<AircraftAd>,
        listener: AircraftLoadListener
    ) {
        if (enable(name).not()) {
            listener.beRefused("config")
            return
        }
        if (AircraftAdUtils.aircraftAdMax.nS >= AircraftAdUtils.aircraftAdMax.tun1ad ||
            AircraftAdUtils.aircraftAdMax.nC >= AircraftAdUtils.aircraftAdMax.tun2ad) {
            listener.beRefused("over")
            return
        }
        if (adValid(name)) {
            listener.beRefused("ad_valid")
            return
        }
        if (adLoading(name)) {
            listener.beRefused("loading")
            return
        }
        if (ad.isEmpty()) {
            listener.beRefused("no_ad_id")
            return
        }
        getLoadAd(name).also { loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            listener.startLoad()
            AircraftUtils.print("$name startLoad-----connected=${loadAd.loadVpnConnected}----")
            loadAd.loading = true
            ad[loadAd.place].also {
                val uploadIp = getUploadIp()

                val showIp = getShowIp()

                val city = if (App.myApplication.getViewModel().stateConnected)
                    ProfileManager.getProfile(DataStore.profileId)?.cName ?: "" else
                    "none"

                AdLoader.Builder(
                    App.myApplication,
                    it.tun3ad,
                )
                    .forNativeAd { native00 ->
                        AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---success-------")
                        loadAd.loadIp = showIp

                        loadAd.place = 0
                        loadAd.ad = native00
                        loadAd.dt = System.currentTimeMillis()
                        if (loadEventPaidList.isEmpty() || loadEventPaidList.count { it.first == native00 } == 0) {
                            loadEventPaidList.add(
                                Pair(
                                    native00, mutableMapOf(
                                        "portage" to name,
                                        "disjunct" to uploadIp,
                                        "grotto" to it.tun3ad,
                                        "bame" to city,
                                    )
                                )
                            )
                        }
                        native00.setOnPaidEventListener {
                            if (loadEventPaidList.isNotEmpty() && loadEventPaidList.count { it.first == native00 } > 0) {
                                val p000 =
                                    loadEventPaidList.find { it.first == native00 }
                                        ?: return@setOnPaidEventListener

                                App.myApplication.getNetViewModel().aircraftFireUpload(
                                    it.valueMicros, it.currencyCode,
                                    native00.responseInfo?.loadedAdapterResponseInfo?.adSourceName
                                        ?: ""
                                )

                                AircraftUtils.aircraftPostJsonMethod(
                                    if (BuildConfig.DEBUG)
                                        "https://test-peggy.stablefasttunnel.com/impelled/prom/nikko"
                                    else "https://peggy.stablefasttunnel.com/monetary/gazebo",
                                    "redound",
                                    JSONObject().run {
                                        putOpt("rinse", it.valueMicros)
                                        putOpt("emanuel", it.currencyCode)
                                        putOpt("chomsky", "admob")
                                        putOpt("neff", "admob")
                                        putOpt("grotto", p000.second["grotto"])
                                        putOpt("portage", p000.second["portage"])

                                        putOpt("bame", p000.second["bame"])

                                        val city111 = getShowCity()
                                        putOpt("kace", city111)

                                        putOpt("tobago", "native")
                                        putOpt(
                                            "peach", arrayListOf(
                                                "UNKNOWN",
                                                "ESTIMATED",
                                                "PUBLISHER_PROVIDED",
                                                "PRECISE"
                                            )[it.precisionType]
                                        )
                                        putOpt("disjunct", p000.second["disjunct"])
                                        val ip111 = getUploadIp()
                                        putOpt("kankakee", ip111)

                                        this
                                    }, false
                                ) {}
                            }
                        }
                        loadAd.loading = false
                        listener.loadSuccess()
                    }.withAdListener(object : AdListener() {
                        override fun onAdClicked() {
                            super.onAdClicked()
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                                Date()
                            )
                            if (date == AircraftAdUtils.aircraftAdMax.dt) {
                                AircraftAdUtils.aircraftAdMax.nC += 1
                            } else {
                                AircraftAdUtils.aircraftAdMax.dt = date
                                AircraftAdUtils.aircraftAdMax.nC = 1
                                AircraftAdUtils.aircraftAdMax.nS = 0
                            }
                            App.myApplication.updateNC(AircraftAdUtils.aircraftAdMax.nC)
                            App.myApplication.updateNS(AircraftAdUtils.aircraftAdMax.nS)
                            App.myApplication.updateDT(AircraftAdUtils.aircraftAdMax.dt)
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            AircraftUtils.print("$name----load--connected=${loadAd.loadVpnConnected}---failed-------${p0.message}")
                            listener.loadFailed()
                            loadAd.loading = false
                            if (loadAd.place < ad.size - 1) {
                                loadAd.place++
                                loadNative(name, ad, listener)
                                return
                            }
                            loadAd.place = 0
                        }
                    }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
    }
}

interface AircraftLoadListener {
    fun beRefused(reason: String)
    fun startLoad()
    fun loadSuccess()
    fun loadFailed()
}

interface AircraftDisplayListener {
    fun beRefused(reason: String)
    fun startDisplay()
    fun displayFailed()
    fun displaySuccess()
    fun closed()
}