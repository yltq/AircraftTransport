package com.plot.evanescent.aircrafttransport.utils

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.AdActivity
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.module.DetermineActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyAppUtils: ActivityLifecycleCallbacks {
    companion object {
        private var myBeforeStops: Int = 0
        private var myBackJob: Job? = null
        private var myAdList: MutableList<AdActivity> = mutableListOf()
        private var myBackTime: Long = 0
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.also {
            if (it is AdActivity) {
                myAdList.add(it)
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
        myBeforeStops += 1
        (myBeforeStops == 1 && myBackTime >= 3).also {
            if (it) {
                App.myApplication.myHotLaunch = true
                activity.startActivity(Intent(activity, DetermineActivity::class.java))
            }
        }
        myBackTime = 0
    }

    override fun onActivityResumed(activity: Activity) {
        Adjust.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        Adjust.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        myBeforeStops -= 1
        myBeforeStops.also {
            if (it > 0) return@also
            myBackJob?.cancel()
            myBackTime = 0
            myBackJob = CoroutineScope(Dispatchers.IO).launch {
                while (myBeforeStops == 0) {
                    delay(1000)
                    myBackTime ++.also {
                        if (it >= 3) {
                            myAdList.also {
                                if (it.size > 0) {
                                    it.forEach {
                                        it.finish()
                                    }
                                }
                            }
                        }
                    }
                }
                cancel()
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activity.also {
            if (it is AdActivity) {
                myAdList.remove(it)
            }
        }
    }

}