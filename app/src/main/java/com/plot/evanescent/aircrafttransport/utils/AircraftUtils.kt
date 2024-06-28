package com.plot.evanescent.aircrafttransport.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.plot.evanescent.aircrafttransport.BuildConfig
import com.plot.evanescent.aircrafttransport.app.App
import fuel.FuelBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import timber.log.Timber

class AircraftUtils {
    companion object {
        var impelAppsByPassOpen: String
        get() {
            val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
            return sharedPref.getString("aircraft_pass", "")?:""
        }
        set(value) {
            val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("aircraft_pass", value)
                apply()
            }
        }
        var aircraftUUID: String
            get() {
                val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_uuid", "")?:""
            }
            set(value) {
                val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_uuid", value)
                    apply()
                }
            }

        var aircraftCloak: String
            get() {
                val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                return sharedPref.getString("aircraft_cloak", "")?:""
            }
            set(value) {
                val sharedPref = App.myApplication.getSharedPreferences("aircraft_share", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("aircraft_cloak", value)
                    apply()
                }
            }

        fun aircraftInMain(context: Context): Boolean {
            val currentProcessId = android.os.Process.myPid()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val processInfo = activityManager.runningAppProcesses.find { it.pid == currentProcessId }
            return processInfo?.processName == context.packageName
        }

        fun aircraftGetMethodInParam(link: String, listP: List<Pair<String, String>>?, loadSuccess: (ResponseBody) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val fuel = FuelBuilder().build()
                    val response = fuel.get {
                        url = link
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

        fun aircraftPostMethod(link: String, list: List<Pair<String, String>>?, loadSuccess: (ResponseBody) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                val fuel = FuelBuilder().build()
                val response = fuel.post {
                    url = link
                    parameters = list?: arrayListOf()
                }
                loadSuccess.invoke(response.body)
            }
        }

        fun print(string: String) {
            if (BuildConfig.DEBUG) Log.e("aircraft", string)
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
            this.takeIf { !TextUtils.isEmpty(it)}?.also {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
        }

        fun Activity.share() {
            val share = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
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
    }
}