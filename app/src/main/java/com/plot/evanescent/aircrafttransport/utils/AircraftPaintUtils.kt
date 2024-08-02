package com.plot.evanescent.aircrafttransport.utils

import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.databinding.ViewHouseTopAdBinding
import com.plot.evanescent.aircrafttransport.databinding.ViewObligeBottomAdBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AircraftPaintUtils {

    fun paintOpenOrIn(name: String, activity: AppCompatActivity, listener: AircraftDisplayListener) {
        if (AircraftFindUtils.enable(name).not()) {
            listener.beRefused("config")
            return
        }
        if (AircraftAdUtils.aircraftAdMax.nS >= AircraftAdUtils.aircraftAdMax.tun1ad ||
            AircraftAdUtils.aircraftAdMax.nC >= AircraftAdUtils.aircraftAdMax.tun2ad) {
            listener.beRefused("over")
            return
        }
        if (!AircraftFindUtils.adValid(name)) {
            listener.beRefused("ad_not_valid")
            return
        }
        if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            listener.beRefused("not_resume")
            return
        }
        AircraftFindUtils.getLoadAd(name).also { loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            listener.startDisplay()
            val callback: FullScreenContentCallback by lazy {
                object : FullScreenContentCallback() {
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

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        AircraftUtils.print("$name----start close ad----")
                        listener.closed()
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        loadAd.ad = null
                        listener.displayFailed()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                        AircraftUtils.print("$name----start show ad----")
                        loadAd.ad = null
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                            Date()
                        )
                        if (date == AircraftAdUtils.aircraftAdMax.dt) {
                            AircraftAdUtils.aircraftAdMax.nS += 1
                        } else {
                            AircraftAdUtils.aircraftAdMax.dt = date
                            AircraftAdUtils.aircraftAdMax.nS = 1
                            AircraftAdUtils.aircraftAdMax.nC = 0
                        }
                        App.myApplication.updateNC(AircraftAdUtils.aircraftAdMax.nC)
                        App.myApplication.updateNS(AircraftAdUtils.aircraftAdMax.nS)
                        App.myApplication.updateDT(AircraftAdUtils.aircraftAdMax.dt)
                        listener.displaySuccess()
                    }
                }
            }
            if (loadAd.ad is AppOpenAd) {
                (loadAd.ad as AppOpenAd).fullScreenContentCallback = callback
                (loadAd.ad as AppOpenAd).show(activity)
            } else if (loadAd.ad is InterstitialAd) {
                (loadAd.ad as InterstitialAd).fullScreenContentCallback = callback
                (loadAd.ad as InterstitialAd).show(activity)
            }
        }
    }

    fun paintNative(name: String, activity: AppCompatActivity,
                    group: ViewGroup,
                    listener: AircraftDisplayListener) {
        if (AircraftFindUtils.enable(name).not()) {
            listener.beRefused("config")
            return
        }
        if (AircraftAdUtils.aircraftAdMax.nS >= AircraftAdUtils.aircraftAdMax.tun1ad ||
            AircraftAdUtils.aircraftAdMax.nC >= AircraftAdUtils.aircraftAdMax.tun2ad) {
            listener.beRefused("over")
            return
        }
        if (!AircraftFindUtils.adValid(name)) {
            listener.beRefused("ad_not_valid")
            return
        }
        if (activity.lifecycle.currentState != Lifecycle.State.RESUMED) {
            listener.beRefused("not_resume")
            return
        }
        AircraftFindUtils.getLoadAd(name).also { loadAd ->
            if (loadAd == null) {
                listener.beRefused("no_load_ad")
                return
            }
            AircraftUtils.print("$name----start show ad----")
            listener.startDisplay()
            if (name == "dimily" && loadAd.ad is NativeAd) {
                val view = LayoutInflater.from(activity).inflate(R.layout.view_house_top_ad, null)
                val binding = ViewHouseTopAdBinding.bind(view)
                val adView = binding.vNative
                adView.also {
                    it.iconView = binding.vAdIcon
                    it.headlineView = binding.vAdTitle
                    it.bodyView = binding.vAdSubtitle
                    it.callToActionView = binding.vAdAction
                    (loadAd.ad as NativeAd).icon?.drawable?.also {
                        binding.vAdIcon.setImageDrawable(it)
                    }
                    binding.vAdTitle.text = (loadAd.ad as NativeAd).headline?:""
                    binding.vAdSubtitle.text = (loadAd.ad as NativeAd).body?:""
                    binding.vAdInstall.text = (loadAd.ad as NativeAd).callToAction?:""
                    it.setNativeAd(loadAd.ad as NativeAd)
                    group.visibility = View.VISIBLE
                    group.removeAllViews()
                    group.addView(binding.root)
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        Date()
                    )
                    if (date == AircraftAdUtils.aircraftAdMax.dt) {
                        AircraftAdUtils.aircraftAdMax.nS += 1
                    } else {
                        AircraftAdUtils.aircraftAdMax.dt = date
                        AircraftAdUtils.aircraftAdMax.nS = 1
                        AircraftAdUtils.aircraftAdMax.nC = 0
                    }
                    App.myApplication.updateNS(AircraftAdUtils.aircraftAdMax.nS)
                    App.myApplication.updateNC(AircraftAdUtils.aircraftAdMax.nC)
                    App.myApplication.updateDT(AircraftAdUtils.aircraftAdMax.dt)
                    loadAd.ad = null
                    listener.displaySuccess()
                }
            } else if (name == "fooey" && loadAd.ad is NativeAd) {
                val view = LayoutInflater.from(activity).inflate(R.layout.view_oblige_bottom_ad, null)
                val binding = ViewObligeBottomAdBinding.bind(view)
                val adView = binding.vNative
                adView.also {
                    it.mediaView = binding.vAdMedia
                    it.mediaView?.mediaContent = (loadAd.ad as NativeAd).mediaContent
                    it.mediaView?.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
                            outline?.setRoundRect(0, 0, view!!.width, view.height, 12f)
                        }
                    }
                    it.iconView = binding.vAdIcon
                    it.headlineView = binding.vAdTitle
                    it.bodyView = binding.vAdSubtitle
                    it.callToActionView = binding.vAdAction
                    binding.vAdMedia.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    (loadAd.ad as NativeAd).icon?.drawable?.also {
                        binding.vAdIcon.setImageDrawable(it)
                    }
                    binding.vAdTitle.text = (loadAd.ad as NativeAd).headline?:""
                    binding.vAdSubtitle.text = (loadAd.ad as NativeAd).body?:""
                    binding.vAdInstall.text = (loadAd.ad as NativeAd).callToAction?:""
                    it.setNativeAd(loadAd.ad as NativeAd)
                    group.visibility = View.VISIBLE
                    group.removeAllViews()
                    group.addView(binding.root)
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        Date()
                    )
                    if (date == AircraftAdUtils.aircraftAdMax.dt) {
                        AircraftAdUtils.aircraftAdMax.nS += 1
                    } else {
                        AircraftAdUtils.aircraftAdMax.dt = date
                        AircraftAdUtils.aircraftAdMax.nS = 1
                        AircraftAdUtils.aircraftAdMax.nC = 0
                    }
                    App.myApplication.updateNS(AircraftAdUtils.aircraftAdMax.nS)
                    App.myApplication.updateNC(AircraftAdUtils.aircraftAdMax.nC)
                    App.myApplication.updateDT(AircraftAdUtils.aircraftAdMax.dt)
                    loadAd.ad = null
                    listener.displaySuccess()
                }
            }
        }
    }
}