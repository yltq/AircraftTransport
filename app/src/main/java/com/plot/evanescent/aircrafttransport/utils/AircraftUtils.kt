package com.plot.evanescent.aircrafttransport.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.app.App.Companion.xor
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils.Companion.LOCAL_CS_ING
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils.Companion.LOCAL_EVEN
import com.plot.evanescent.aircrafttransport.utils.AircraftAdUtils.Companion.LOCAL_FT_ING
import fuel.FuelBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap
import java.io.IOException
import java.util.Locale
import java.util.UUID


interface ApiService {

    @POST("/impelled/prom/nikko")
    fun aircraftPostJsonMethod(
        @Header("ton") ton: String,
        @Header("biracial") biracial: String,
        @QueryMap params: MutableMap<String, String>,
        @Body body: RequestBody
    ): Call<ResponseBody>
}

class AircraftUtils {
    companion object {
        var impelAppsByPassOpen: String
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_pass", "") ?: ""
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_pass", value)
                    apply()
                }
            }
        var aircraftUUID: String
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_uuid", "") ?: ""
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_uuid", value)
                    apply()
                }
            }

        var aircraftCloak: String
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_cloak", "") ?: ""
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_cloak", value)
                    apply()
                }
            }

        var aircraftLoadCmp: Boolean
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getBoolean("aircraft_load_cmp", false)
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("aircraft_load_cmp", value)
                    apply()
                }
            }

        var ftIngCache: String //1表示展示结果页返回插屏+首页原生，2表示不展示结果页返回插屏+首页原生//本地默认写2，其他广告位默认都展示
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_ft_ing_cache", LOCAL_FT_ING) ?: LOCAL_FT_ING
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_ft_ing_cache", value)
                    apply()
                }
            }

        var csIngCache: String  //1表示买量身份，2表示非买量身份，默认2
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_cs_ing_cache", LOCAL_CS_ING) ?: LOCAL_CS_ING
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_cs_ing_cache", value)
                    apply()
                }
            }

        var deadCache: String  //总控参数缓存
            get() {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_dead_cache", LOCAL_EVEN) ?: LOCAL_EVEN
            }
            set(value) {
                val sharedPref =
                    App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_dead_cache", value)
                    apply()
                }
            }

        fun aircraftInMain(context: Context): Boolean {
            val currentProcessId = android.os.Process.myPid()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfo =
                activityManager.runningAppProcesses.find { it.pid == currentProcessId }
            return processInfo?.processName == context.packageName
        }

        fun aircraftGetMethodInParam(link: String, listP: List<Pair<String, String>>?, map: Map<String, String>, loadSuccess: (ResponseBody) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val fuel = FuelBuilder().build()
                    val response = fuel.get {
                        url = link
                        headers = map
                        parameters = listP?: arrayListOf()
                    }
                    response
                }.onSuccess {
                    loadSuccess.invoke(it.body)
                }.onFailure {
                    AircraftUtils.print("aircraftGetMethodInParam error:${it.message}")
                }
            }
        }

        fun aircraftGetMethodInHeader(link: String, listH: Map<String, String>?, loadSuccess: (ResponseBody?) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val fuel = FuelBuilder().build()
                    val response = fuel.get {
                        url = link
                        headers = listH?: mutableMapOf()
                    }
                    response
                }.onSuccess {
                    loadSuccess.invoke(it.body)
                }.onFailure {
                    loadSuccess.invoke(null)
                    AircraftUtils.print("aircraftGetMethodInHeader error:${it.message}")
                }
            }
        }

        private const val BASE_URL = "https://test-peggy.stablefasttunnel.com"

        val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: ApiService by lazy {
            instance.create(ApiService::class.java)
        }

        fun aircraftPostJsonMethod(
            link: String,
            key: String,
            jsonBody: JSONObject,
            upload: Boolean,
            loadSuccess: (String) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val h11: MutableMap<String, String> = mutableMapOf()
                    val uuid = UUID.randomUUID()
                    h11["content-type"] = "application/json"
                    h11["ton"] = Locale.getDefault().country
                    h11["biracial"] = uuid.toString()
                    val slept = System.currentTimeMillis()
                    val j000 = JSONObject()
                    j000.putOpt("gilead", AircraftAdUtils.gilead().run {
                        putOpt("biracial", uuid.toString())
                        putOpt("slept", slept.toString())
                        this
                    })
                    if (!upload && !TextUtils.isEmpty(key) && key != "redound") {
                        j000.putOpt(key, jsonBody)
                    } else if (!upload && key == "redound") {
                        j000.putOpt("emily", "redound")

                        val keys = jsonBody.keys()
                        var bame = ""
                        var kace = ""
                        while (keys.hasNext()) {
                            val k000 = keys.next()
                            val v000 = jsonBody.optString(k000)
                            when (k000) {
                                "bame" -> {
                                    bame = v000
                                }
                                "kace" -> {
                                    kace = v000
                                }
                                else -> {
                                    j000.putOpt(k000, v000)
                                }
                            }
                        }
                        j000.putOpt("nautilus", JSONObject().run {
                            putOpt("bame", bame)
                            putOpt("kace", kace)
                        })
                    } else if (upload) {
                        j000.putOpt("emily", key)

                        val keys = jsonBody.keys()
                        var bame = ""
                        var kace = ""
                        while (keys.hasNext()) {
                            val k000 = keys.next()
                            val v000 = jsonBody.optString(k000)
                            when (k000) {
                                "bame" -> {
                                    bame = v000
                                }
                                "kace" -> {
                                    kace = v000
                                }
                                else -> {
                                    j000.putOpt("triplet&$k000", v000)
                                }
                            }
                        }
                        j000.putOpt("nautilus", JSONObject().run {
                            putOpt("bame", bame)
                            putOpt("kace", kace)
                        })
                    }

                    val requestBody: RequestBody = RequestBody.create(
                        "Content-Type;application/json".toMediaTypeOrNull(),
                        j000.toString()
                    )

                    api.aircraftPostJsonMethod(Locale.getDefault().country, uuid.toString(),
                        mutableMapOf(
                        "slept" to slept.toString(),
                        "sweeten" to App.myApplication.gaid
                    ), requestBody).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                print("aircraftPostJsonMethod result--success---$key--: ${response.body()?.string()}")
                            } else {
                                print("aircraftPostJsonMethod result--err---$key----: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            print("aircraftPostJsonMethod result----error----: ${t.message}")
                        }

                    })

                } catch (e: Exception) {
                    print("aircraftPostJsonMethod result: error ----${e.message}----")
                }
            }
        }

        fun print(string: String) {
            if (BuildConfig.DEBUG)
                Log.e("aircraft", string)
        }

        fun Context.toast(string: String) {
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        }

        fun Context.displayNoNet() {
            val dialog = AlertDialog.Builder(this)
                .setMessage("Network request timed out. Please make sure your network is connected")
                .setCancelable(false)
                .setPositiveButton("OK") { p0, p1 ->

                }
            dialog.show()
        }

        fun Context.displayLocationLimited(limitedClick: () -> Unit) {
            val dialog = AlertDialog.Builder(this)
                .setMessage("Due to the policy reason , this service is not available in your country")
                .setCancelable(false)
                .setPositiveButton("Confirm") { p0, p1 ->
                    limitedClick.invoke()
                }
            dialog.show()
        }

        fun Context.displayChoose(tapYes: () -> Unit) {
            val dialog = AlertDialog.Builder(this)
                .setMessage("whether to disconnect the current connection")
                .setCancelable(false)
                .setPositiveButton("Yes") { p0, p1 ->
                    tapYes.invoke()
                }
                .setNegativeButton("No") { p0, p1 ->
                }
            dialog.show()
        }

        fun <T> Activity.go(cls: Class<T>) {
            startActivity(Intent(this, cls))
        }

        fun <T> Activity.go(cls: Class<T>, code: String) {
            startActivity(Intent(this, cls).run { putExtra("code", code) })
        }

        fun String.go(context: Context) {
            this.takeIf { !TextUtils.isEmpty(it) }?.also {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
        }

        fun Activity.share() {
            val share =
                "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            val shareIntent = Intent().run {
                type = "text/plain"
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, share)
            }
            startActivity(Intent.createChooser(shareIntent, "share this"))
        }

        fun Long.toDisplayString(): String {
            val hours = this / 3600
            val minutes = this % 3600 / 60
            val seconds = this % 3600 % 60
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        fun aircraftFb(): String {
            if (App.myApplication.myAppRefer.isEmpty()) {
                return "user0"
            }
            if (AircraftAdUtils.deadValidKey.count { App.myApplication.myAppRefer.contains(it) } > 0) {
                return "user1"
            }
            return "user0"
        }


        var aircraftAdminLoadSuccess: Boolean = false //成功获取admin配置信息

        fun String.aircraftAdminType(again: Boolean) {
            if (this.isEmpty()) return
            kotlin.runCatching {
                val json = JSONObject().apply {
                    putOpt("DSGXAsly", App.myApplication.packageManager.getPackageInfo(App.myApplication.packageName, 0).versionName)
                    putOpt("eEX", String(Base64.decode("Y29tLnNreXN0cmVhbS5mYXN0bGluay51bmxpbWl0ZWQuc3RhYmxl", Base64.DEFAULT)))
                    putOpt("jNKdieYYxy", AircraftUtils.aircraftUUID)
                    putOpt("dBiKf", this@aircraftAdminType)
                }
                val sys = System.currentTimeMillis().toString()

                val xor = sys.xor(json.toString())
                if (xor.isEmpty()) return
                val encode = String(Base64.encode(xor.toByteArray(), 0))

                val request = Request.Builder()
                    .url("https://sks.stablefasttunnel.com/api/buww/pwkjq/")
                    .addHeader("ts", sys)
                    .addHeader("Content-Type", "text/plain")
                    .post(encode.toRequestBody("application/json".toMediaType()))
                    .build()
                OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        print("aircraftAdminType load error-----${e.message}")
                        if (again) {
                            this@aircraftAdminType.aircraftAdminType(false)
                        }
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        if (response.isSuccessful) {
                            val headers = response.headers
                            val resultStr = response.body.string()
                            if (resultStr.isEmpty()) return
                            val decode = String(Base64.decode(resultStr, Base64.DEFAULT))
                            val datetime = headers["ts"]?:return
                            val xorResult = datetime.xor(decode)
                            if (xorResult.isEmpty()) return
                            val result = xorResult.replace(Regex("[\\r\\n]"), "")?:return
                            print("aircraftAdminType load success-------${result}")
                            kotlin.runCatching {
                                val json = JSONObject(result)
                                val detail = json.optJSONObject("PGX")?:return
                                val conf = detail.getString("conf")
                                val confJson = JSONObject(conf)
                                confJson.optString("ft_ing").apply {
                                    if (this.isNotEmpty()) {
                                        ftIngCache = this
                                    }
                                }
                                confJson.optString("cs_ing").apply {
                                    if (this.isNotEmpty()) {
                                        csIngCache = this
                                    }
                                }
                                aircraftAdminLoadSuccess = true //获取admin配置成功
                            }
                        } else {
                            print("aircraftAdminType load error--------${response.code}")
                        }
                    }
                })
            }
        }

    }
}