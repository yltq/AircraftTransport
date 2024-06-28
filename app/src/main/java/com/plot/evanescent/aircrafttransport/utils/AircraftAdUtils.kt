package com.plot.evanescent.aircrafttransport.utils

import android.util.Base64
import com.plot.evanescent.aircrafttransport.app.App
import org.json.JSONObject

class AircraftAdUtils {
    companion object {
        const val LOCAL_AD = """
            ewogICAgInR1bjFhZCI6ICIzMCIsCiAgICAidHVuMmFkIjogIjUiLAogICAgInRpbWQiOiB7CiAgICAgICAgImFubmV4IjogewogICAgICAgICAgICAidHVuM2FkIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NFwvOTI1NzM5NTkyMSUxMTExMSUxMTExMTEiLAogICAgICAgICAgICAidHVuNGFkIjogImJhdCViYXQlYmF0IgogICAgICAgIH0sCiAgICAgICAgImRpbWlseSI6IHsKICAgICAgICAgICAgInR1bjNhZCI6ICJjYS1hcHAtcHViLTM5NDAyNTYwOTk5NDI1NDRcLzkyNTczOTU5MjElMTExMTExJTExMTExMSIsCiAgICAgICAgICAgICJ0dW40YWQiOiAicmlnJXJpZyVyaWciCiAgICAgICAgfSwKICAgICAgICAiZm9vZXkiOiB7CiAgICAgICAgICAgICJ0dW4zYWQiOiAiY2EtYXBwLXB1Yi0zOTQwMjU2MDk5OTQyNTQ0XC8yMjQ3Njk2MTEwJTExMTExMSUxMTExMTEiLAogICAgICAgICAgICAidHVuNGFkIjogInJpZyVyaWclcmlnIgogICAgICAgIH0sCiAgICAgICAgImJ1aWxkIjogewogICAgICAgICAgICAidHVuM2FkIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NFwvMTAzMzE3MzcxMiUxMTExMSUxMTExMTEiLAogICAgICAgICAgICAidHVuNGFkIjogImNvdyVjb3clY293IgogICAgICAgIH0sCiAgICAgICAgInlvdW5nIjogewogICAgICAgICAgICAidHVuM2FkIjogImNhLWFwcC1wdWItMzk0MDI1NjA5OTk0MjU0NFwvMTAzMzE3MzcxMiUxMTExMSUxMTExMTEiLAogICAgICAgICAgICAidHVuNGFkIjogImNvdyVjb3clY293IgogICAgICAgIH0KICAgIH0KfQ==
        """
        const val LOCAL_FB = """
            ewogICAgImRvd24iOiAxLAogICAgImJpZGUiOiAyLAogICAgIm9udG8iOiAyLAogICAgImludG8iOiAyLAogICAgInVudG8iOiAyLAogICAgInRpbGwiOiAyLAogICAgIm1vcmUiOiAyCn0=
        """
        const val LOCAL_EVEN = """
            ewogICAgImxlc3QiOiAiMiIsCiAgICAiYmVsbCI6ICIxIiwKICAgICJnb29kIjogIjEiCn0=
        """
        val aircraftAdMax: AircraftAdMax = AircraftAdMax()
        val annex: MutableList<AircraftAd> = mutableListOf()
        val dimily: MutableList<AircraftAd> = mutableListOf()
        val fooey: MutableList<AircraftAd> = mutableListOf()
        val build: MutableList<AircraftAd> = mutableListOf()
        val young: MutableList<AircraftAd> = mutableListOf()
        val annexLoad: AircraftLoadAd = AircraftLoadAd()
        val dimilyLoad: AircraftLoadAd = AircraftLoadAd()
        val fooeyLoad: AircraftLoadAd = AircraftLoadAd()
        val buildLoad: AircraftLoadAd = AircraftLoadAd()
        val youngLoad: AircraftLoadAd = AircraftLoadAd()

        const val LOCAL_LEST = "2"
        const val LOCAL_BELL = "1"
        const val LOCAL_GOOD = "1"
        var lest: String = LOCAL_LEST
        var bell: String = LOCAL_BELL
        var good: String = LOCAL_GOOD
        var deadValidKey: MutableList<String> = mutableListOf("fb4a", "facebook")
    }

    fun resolveEvenString(string: String) {
        val encode = string.ifEmpty { LOCAL_EVEN }
        encode.also {
            val decode = String(Base64.decode(encode, 0))
            kotlin.runCatching {
                val json = JSONObject(decode)
                json
            }.onSuccess {
                lest = it.optString("lest").ifEmpty { LOCAL_LEST }
                bell = it.optString("bell").ifEmpty { LOCAL_BELL }
                good = it.optString("good").ifEmpty { LOCAL_GOOD }
            }.onFailure {
                AircraftUtils.print("resolveEvenString error:${it.message}")
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
                    AircraftUtils.print("load $name beRefused: $reason")
                }

                override fun startLoad() {
                    AircraftUtils.print("$name startLoad")
                }

                override fun loadSuccess() {
                    AircraftUtils.print("$name loadSuccess")
                }

                override fun loadFailed() {
                    AircraftUtils.print("$name loadFailed")
                }
            })
    }

    fun fabulousLoadNative(name: String, loadSuccess: () -> Unit) {
        App.myApplication.aircraftFindUtils.loadNative(name,
            AircraftFindUtils.getLoadList(name),
            object : AircraftLoadListener {
                override fun beRefused(reason: String) {
                    AircraftUtils.print("load $name beRefused: $reason")
                }

                override fun startLoad() {
                    AircraftUtils.print("$name startLoad")
                }

                override fun loadSuccess() {
                    AircraftUtils.print("$name loadSuccess")
                    loadSuccess()
                }

                override fun loadFailed() {
                    AircraftUtils.print("$name loadFailed")
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
    var place: Int = 0
)