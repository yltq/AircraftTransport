package com.plot.evanescent.aircrafttransport.utils

import android.text.TextUtils
import android.util.Base64
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.github.shadowsocks.preference.DataStore
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.deadCache
import org.json.JSONObject
import java.util.Locale

class AircraftAdUtils {
    companion object {
        const val LOCAL_AD_MOD2 = """
            ewogICAgInR1bjFhZCI6IDMwLAogICAgInR1bjJhZCI6IDUsCiAgICAiY3NlciI6IHsKICAgICAgICAiYW5uZXgiOiB7CiAgICAgICAgICAgICJ0dW4zYWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzkyNTczOTU5MjEiLAogICAgICAgICAgICAidHVuNGFkIjogImJhdCIKICAgICAgICB9LAogICAgICAgICJkaW1pbHkiOiB7CiAgICAgICAgICAgICJ0dW4zYWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0LzIyNDc2OTYxMTAiLAogICAgICAgICAgICAidHVuNGFkIjogInJpZyIKICAgICAgICB9LAogICAgICAgICJmb29leSI6IHsKICAgICAgICAgICAgInR1bjNhZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICAgICAgICJ0dW40YWQiOiAicmlnIgogICAgICAgIH0sCiAgICAgICAgImJ1aWxkIjogewogICAgICAgICAgICAidHVuM2FkIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC8xMDMzMTczNzEyIiwKICAgICAgICAgICAgInR1bjRhZCI6ICJjb3ciCiAgICAgICAgfSwKICAgICAgICAieW91bmciOiB7CiAgICAgICAgICAgICJ0dW4zYWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0Lzg2OTE2OTE0MzMiLAogICAgICAgICAgICAidHVuNGFkIjogImNvdyIKICAgICAgICB9CiAgICB9LAogICAgImNiYXNlIjogewogICAgICAgICJhbm5leCI6IHsKICAgICAgICAgICAgInR1bjNhZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvOTI1NzM5NTkyMSIsCiAgICAgICAgICAgICJ0dW40YWQiOiAiYmF0IgogICAgICAgIH0sCiAgICAgICAgImRpbWlseSI6IHsKICAgICAgICAgICAgInR1bjNhZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDQvMjI0NzY5NjExMCIsCiAgICAgICAgICAgICJ0dW40YWQiOiAicmlnIgogICAgICAgIH0sCiAgICAgICAgImZvb2V5IjogewogICAgICAgICAgICAidHVuM2FkIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NC8yMjQ3Njk2MTEwIiwKICAgICAgICAgICAgInR1bjRhZCI6ICJyaWciCiAgICAgICAgfSwKICAgICAgICAieW91bmciOiB7CiAgICAgICAgICAgICJ0dW4zYWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0Lzg2OTE2OTE0MzMiLAogICAgICAgICAgICAidHVuNGFkIjogImNvdyIKICAgICAgICB9CiAgICB9Cn0=
        """
        const val LOCAL_FB = """
            ewogICAgImRvd24iOiAxLAogICAgImJpZGUiOiAyLAogICAgIm9udG8iOiAyLAogICAgImludG8iOiAyLAogICAgInVudG8iOiAyLAogICAgInRpbGwiOiAyLAogICAgIm1vcmUiOiAyCn0=
        """
        const val LOCAL_EVEN = """
            ewogICAgImxlc3QiOiAiMiIsCiAgICAiYmVsbCI6ICIxIiwKICAgICJnb29kIjogIjEiLAogICAgInRtcyI6ICIxMiYxMiIKfQ==
        """
        val aircraftAdMax: AircraftAdMax = AircraftAdMax()
        val annex: MutableList<AircraftAd> = mutableListOf()
        val dimily: MutableList<AircraftAd> = mutableListOf()
        val fooey: MutableList<AircraftAd> = mutableListOf()
        val build: MutableList<AircraftAd> = mutableListOf()
        val young: MutableList<AircraftAd> = mutableListOf()

        val annexDis: MutableList<AircraftAd> = mutableListOf()
        val dimilyDis: MutableList<AircraftAd> = mutableListOf()
        val fooeyDis: MutableList<AircraftAd> = mutableListOf()
        val buildDis: MutableList<AircraftAd> = mutableListOf()
        val youngDis: MutableList<AircraftAd> = mutableListOf()

        val annexLoad: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = true)
        val dimilyLoad: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = true)
        val fooeyLoad: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = true)
        val buildLoad: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = true)
        val youngLoad: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = true)

        val annexLoadDis: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = false)
        val dimilyLoadDis: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = false)
        val fooeyLoadDis: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = false)
        val buildLoadDis: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = false)
        val youngLoadDis: AircraftLoadAd = AircraftLoadAd(loadVpnConnected = false)

//        const val LOCAL_LEST = "2"
        const val LOCAL_BELL = "1"
        const val LOCAL_GOOD = "1"
        const val LOCAL_TMS = "12&12" //第一个12表示连接等待时间12s，第二个12是断开等待时间12s---配置多少则是多少，单位s，默认12s---配置不满足格式要求，则以默认12s来
//        var lest: String = LOCAL_LEST
        var bell: String = LOCAL_BELL
        var good: String = LOCAL_GOOD
        var kont: String = ""
        var deadValidKey: MutableList<String> = mutableListOf("fb4a", "facebook")
        var tms_connect: Int = 12 //第一个12表示连接等待时间12s，第二个12是断开等待时间12s---配置多少则是多少，单位s，默认12s---配置不满足格式要求，则以默认12s来
        var tms_disconnect: Int = 12  //第一个12表示连接等待时间12s，第二个12是断开等待时间12s---配置多少则是多少，单位s，默认12s---配置不满足格式要求，则以默认12s来

        const val LOCAL_FT_ING = "2"  //1表示展示结果页返回插屏+首页原生，2表示不展示结果页返回插屏+首页原生//本地默认写2，其他广告位默认都展示
        const val LOCAL_CS_ING = "2"  //1表示买量身份，2表示非买量身份，默认2

        fun gilead(): JSONObject {
            return JSONObject().run {
                putOpt("sweeten", App.myApplication.gaid)
                putOpt("filial", Locale.getDefault().language + "_" + Locale.getDefault().country)
                putOpt("familiar", "1234")
                putOpt("claim", BuildConfig.APPLICATION_ID)
                putOpt("casanova", BuildConfig.VERSION_NAME)
                putOpt("calculi", "chimera")
                putOpt("squat", "1")
                putOpt("honoree", AircraftUtils.aircraftUUID)
                putOpt("horny", "1")
                putOpt("opt", AircraftUtils.aircraftUUID)
                putOpt("median", "1234")
                this
            }
        }

        fun initAircraftFacebook(k: String) {
            val faceId = k.ifEmpty {
                DataStore.kont
            }
            if (TextUtils.isEmpty(faceId)) return
            FacebookSdk.setApplicationId(faceId)
            FacebookSdk.sdkInitialize(App.myApplication.applicationContext)
            AppEventsLogger.activateApp(App.myApplication)
        }
    }

    fun resolveEvenString(string: String) {
        if (string.isNotEmpty()) deadCache = string
        val encode = string.ifEmpty { deadCache }
        encode.also {
            val decode = String(Base64.decode(encode, 0))
            kotlin.runCatching {
                val json = JSONObject(decode)
                json
            }.onSuccess {
//                lest = it.optString("lest").ifEmpty { LOCAL_LEST }
                bell = it.optString("bell").ifEmpty { LOCAL_BELL }
                good = it.optString("good").ifEmpty { LOCAL_GOOD }
                kont = it.optString("kont").ifEmpty { "" }
                val tms = it.optString("tms").ifEmpty { LOCAL_TMS }
                kotlin.runCatching {
                    val tmsList = tms.split("&")
                    if (tmsList.size == 2) {
                        tms_connect = tmsList[0].toInt()
                        tms_disconnect = tmsList[1].toInt()
                    }
                }
                initAircraftFacebook(kont)
            }.onFailure {
                AircraftUtils.print("resolveEvenString error:${it.message}")
                initAircraftFacebook("")
            }
        }
    }

    fun resolveFbString(string: String) {
        val encode = string.ifEmpty { LOCAL_FB }
        encode.also {
            val decode = String(Base64.decode(encode, 0))
            kotlin.runCatching {
                val json = JSONObject(decode)
                json
            }.onSuccess {
                deadValidKey.clear()
                if (it.optInt("down") == 1) {
                    deadValidKey.add("fb4a")
                    deadValidKey.add("facebook")
                }
                if (it.optInt("bide") == 1) {
                    deadValidKey.add("gclid")
                }
                if (it.optInt("onto") == 1) {
                    deadValidKey.add("not%20set")
                }
                if (it.optInt("into") == 1) {
                    deadValidKey.add("youtubeads")
                }
                if (it.optInt("unto") == 1) {
                    deadValidKey.add("%7B%22")
                }
                if (it.optInt("till") == 1) {
                    deadValidKey.add("adjust")
                }
                if (it.optInt("more") == 1) {
                    deadValidKey.add("bytedance")
                }
            }.onFailure {
                AircraftUtils.print("resolveFbString error:${it.message}")
            }
        }
    }

    fun resolveAdMode2String(string: String) {
        val encode = string.ifEmpty { LOCAL_AD_MOD2 }
        encode.also {
            val decode = String(Base64.decode(encode, 0))
            kotlin.runCatching {
                val json = JSONObject(decode)
                json
            }.onSuccess {
                kotlin.runCatching {
                    aircraftAdMax.tun1ad = it.optInt("tun1ad")
                    aircraftAdMax.tun2ad = it.optInt("tun2ad")
                }
                val asver = it.optJSONObject("cser")
                val bloc = it.optJSONObject("cbase")
                asver?.let {
                    it.optJSONObject("annex")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            annex.clear()
                            tun3ad.forEachIndexed { index, s ->
                                annex.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("dimily")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            dimily.clear()
                            tun3ad.forEachIndexed { index, s ->
                                dimily.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("fooey")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            fooey.clear()
                            tun3ad.forEachIndexed { index, s ->
                                fooey.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("build")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            build.clear()
                            tun3ad.forEachIndexed { index, s ->
                                build.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("young")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            young.clear()
                            tun3ad.forEachIndexed { index, s ->
                                young.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                }

                bloc?.let {
                    it.optJSONObject("annex")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            annexDis.clear()
                            tun3ad.forEachIndexed { index, s ->
                                annexDis.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("dimily")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            dimilyDis.clear()
                            tun3ad.forEachIndexed { index, s ->
                                dimilyDis.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("fooey")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            fooeyDis.clear()
                            tun3ad.forEachIndexed { index, s ->
                                fooeyDis.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("build")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            buildDis.clear()
                            tun3ad.forEachIndexed { index, s ->
                                buildDis.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                    it.optJSONObject("young")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            youngDis.clear()
                            tun3ad.forEachIndexed { index, s ->
                                youngDis.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                }
            }.onFailure {
                AircraftUtils.print("resolveAdString error:${it.message}")
            }
        }
    }

    fun resolveAdString(string: String) {
        val encode = string.ifEmpty { LOCAL_AD }
        encode.also {
            val decode = String(Base64.decode(encode, 0))
            kotlin.runCatching {
                val json = JSONObject(decode)
                json
            }.onSuccess {
                aircraftAdMax.tun1ad = it.optInt("tun1ad")
                aircraftAdMax.tun2ad = it.optInt("tun2ad")
                val timd = it.optJSONObject("timd") ?: return
                timd.run {
                    optJSONObject("annex")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            annex.clear()
                            tun3ad.forEachIndexed { index, s ->
                                annex.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                    optJSONObject("dimily")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            dimily.clear()
                            tun3ad.forEachIndexed { index, s ->
                                dimily.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                    optJSONObject("fooey")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            fooey.clear()
                            tun3ad.forEachIndexed { index, s ->
                                fooey.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                    optJSONObject("build")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            build.clear()
                            tun3ad.forEachIndexed { index, s ->
                                build.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }
                    optJSONObject("young")?.also {
                        val tun3ad = it.optString("tun3ad").split("%")
                        val tun4ad = it.optString("tun4ad").split("%")
                        if (tun3ad.isNotEmpty() && tun3ad.size == tun4ad.size) {
                            young.clear()
                            tun3ad.forEachIndexed { index, s ->
                                young.add(
                                    AircraftAd(
                                        s, tun4ad[index]
                                    )
                                )
                            }
                        }
                    }

                }
            }.onFailure {
                AircraftUtils.print("resolveAdString error:${it.message}")
            }
        }
    }

    fun fabulousLoadOpenOrIn(name: String, twice: Boolean) {
        App.myApplication.aircraftFindUtils.loadOpenOrIn(name,
            AircraftFindUtils.getLoadList(name), twice,
            object : AircraftLoadListener {
                override fun beRefused(reason: String) {
//                    AircraftUtils.print("load $name beRefused: $reason")
                }

                override fun startLoad() {
//                    AircraftUtils.print("$name startLoad")
                }

                override fun loadSuccess() {
//                    AircraftUtils.print("$name loadSuccess")
                }

                override fun loadFailed() {
//                    AircraftUtils.print("$name loadFailed")
                }
            })
    }

    fun fabulousLoadNative(name: String, loadSuccess: () -> Unit) {
        App.myApplication.aircraftFindUtils.loadNative(name,
            AircraftFindUtils.getLoadList(name),
            object : AircraftLoadListener {
                override fun beRefused(reason: String) {
//                    AircraftUtils.print("load $name beRefused: $reason")
                }

                override fun startLoad() {
//                    AircraftUtils.print("$name startLoad")
                }

                override fun loadSuccess() {
//                    AircraftUtils.print("$name loadSuccess")
                    loadSuccess()
                }

                override fun loadFailed() {
//                    AircraftUtils.print("$name loadFailed")
                }
            })
    }

}

data class AircraftAdMax(
    var tun1ad: Int = 30,
    var tun2ad: Int = 5,
    var nS: Int = 0,
    var nC: Int = 0,
    var dt: String = ""
)

data class AircraftAd(
    var tun3ad: String,
    var tun4ad: String
)

data class AircraftLoadAd(
    var ad: Any? = null,
    var loading: Boolean = false,
    var dt: Long = 0,
    var place: Int = 0,
    var loadIp: String = "",
    var loadVpnConnected: Boolean = false
)