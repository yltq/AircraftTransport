package com.plot.evanescent.aircrafttransport.utils

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.plot.evanescent.aircrafttransport.app.App
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AircraftFindUtils {
    companion object {
        fun enable(name: String): Boolean {
            return when (name) {
                "fooey",
                "annex" -> true

                "dimily" -> AircraftAdUtils.lest == "1" || (AircraftAdUtils.lest == "2" &&
                        AircraftUtils.aircraftFb() == "user1")

                "build",
                "young" -> (AircraftAdUtils.lest == "1" || (AircraftAdUtils.lest == "2" &&
                        AircraftUtils.aircraftFb() == "user1")) && (AircraftAdUtils.bell == "2" ||
                        AircraftAdUtils.bell == "1" && AircraftUtils.aircraftCloak == "there")
                else -> true
            }
        }

        fun adValid(name: String): Boolean {
            return when(name) {
                "annex" -> AircraftAdUtils.annexLoad.ad != null &&
                        System.currentTimeMillis() - AircraftAdUtils.annexLoad.dt < 3600000
                "dimily" -> AircraftAdUtils.dimilyLoad.ad != null &&
                        System.currentTimeMillis() - AircraftAdUtils.dimilyLoad.dt < 3600000
                "fooey" -> AircraftAdUtils.fooeyLoad.ad != null &&
                        System.currentTimeMillis() - AircraftAdUtils.fooeyLoad.dt < 3600000
                "build" -> AircraftAdUtils.buildLoad.ad != null &&
                        System.currentTimeMillis() - AircraftAdUtils.buildLoad.dt < 3600000
                "young" -> AircraftAdUtils.youngLoad.ad != null &&
                        System.currentTimeMillis() - AircraftAdUtils.youngLoad.dt < 3600000
                else -> false
            }
        }

        fun adLoading(name: String): Boolean {
            return when(name) {
                "annex" -> AircraftAdUtils.annexLoad.loading
                "dimily" -> AircraftAdUtils.dimilyLoad.loading
                "fooey" -> AircraftAdUtils.fooeyLoad.loading
                "build" -> AircraftAdUtils.buildLoad.loading
                "young" -> AircraftAdUtils.youngLoad.loading
                else -> false
            }
        }

        fun getLoadList(name: String): MutableList<AircraftAd> {
            return when(name) {
                "annex" -> AircraftAdUtils.annex
                "dimily" -> AircraftAdUtils.dimily
                "fooey" -> AircraftAdUtils.fooey
                "build" -> AircraftAdUtils.build
                "young" -> AircraftAdUtils.young
                else -> mutableListOf()
            }
        }

        fun getLoadAd(name: String): AircraftLoadAd? {
            return when(name) {
                "annex" -> AircraftAdUtils.annexLoad
                "dimily" -> AircraftAdUtils.dimilyLoad
                "fooey" -> AircraftAdUtils.fooeyLoad
                "build" -> AircraftAdUtils.buildLoad
                "young" -> AircraftAdUtils.youngLoad
                else -> null
            }
        }
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
        getLoadAd(name).also {loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            listener.startLoad()
            loadAd.loading = true
            ad[loadAd.place].also {
                if (it.tun4ad == "bat") {
                    AppOpenAd.load(
                        App.myApplication,
                        it.tun3ad,
                        AdRequest.Builder().build(),
                        object : AppOpenAd.AppOpenAdLoadCallback() {
                            override fun onAdLoaded(openAd: AppOpenAd) {
                                loadAd.place = 0
                                loadAd.ad = openAd
                                loadAd.dt = System.currentTimeMillis()
                                loadAd.loading = false
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                loadAd.loading = false
                                if (loadAd.place < ad.size - 1) {
                                    loadAd.place ++
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
                                loadAd.place = 0
                                loadAd.ad = interstitialAd
                                loadAd.dt = System.currentTimeMillis()
                                loadAd.loading = false
                                listener.loadSuccess()
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                listener.loadFailed()
                                loadAd.loading = false
                                if (loadAd.place < ad.size - 1) {
                                    loadAd.place ++
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
        getLoadAd(name).also {loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            listener.startLoad()
            loadAd.loading = true
            ad[loadAd.place].also {
                AdLoader.Builder(
                    App.myApplication,
                    it.tun3ad,
                )
                    .forNativeAd {
                        loadAd.place = 0
                        loadAd.ad = it
                        loadAd.dt = System.currentTimeMillis()
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
                            listener.loadFailed()
                            loadAd.loading = false
                            if (loadAd.place < ad.size - 1) {
                                loadAd.place ++
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