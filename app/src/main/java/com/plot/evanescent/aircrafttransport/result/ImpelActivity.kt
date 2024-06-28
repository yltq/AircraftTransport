package com.plot.evanescent.aircrafttransport.result

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.databinding.ActivityImpelBinding
import com.plot.evanescent.aircrafttransport.databinding.ActivityObligeBinding
import com.plot.evanescent.aircrafttransport.databinding.FabulousLayoutAppProxyBinding
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils
import com.plot.evanescent.aircrafttransport.utils.AircraftUtils.Companion.toast

class ImpelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImpelBinding
    private var appsMap: MutableMap<String, MyImpelData> = mutableMapOf()
    private var apps: MutableList<MyImpelData> = mutableListOf()
    private var adapter: ImpelAdapter? = null
    private var appsByPassOpen: MutableList<String> = mutableListOf()

    inner class ImpelAdapter : RecyclerView.Adapter<ImpelAdapter.ImpelHolder>() {
        inner class ImpelHolder(var binding: FabulousLayoutAppProxyBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImpelHolder {
            return ImpelHolder(
                FabulousLayoutAppProxyBinding.bind(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.fabulous_layout_app_proxy, parent, false
                    )
                )
            )
        }

        override fun getItemCount(): Int {
            return apps.size
        }

        override fun onBindViewHolder(holder: ImpelHolder, position: Int) {
            apps[position].also {
                holder.binding.vProxyIcon.setImageDrawable(it.icon)
                holder.binding.vProxyName.text = it.name
                holder.binding.vProxySelect.isSelected = appsByPassOpen.contains(it.pName)
                holder.binding.root.setOnClickListener { v ->
                    holder.binding.vProxySelect.isSelected = !holder.binding.vProxySelect.isSelected
                    holder.binding.vProxySelect.isSelected.also { selected ->
                        if (selected) {
                            if (appsByPassOpen.isEmpty() || !appsByPassOpen.contains(it.pName)) {
                                appsByPassOpen.add(it.pName)
                            }
                        } else if (appsByPassOpen.isNotEmpty()
                            && appsByPassOpen.contains(it.pName)
                        ) {
                            appsByPassOpen.remove(it.pName)
                        }
                        binding.vImpelSwitch.setCardBackgroundColor(
                            Color.parseColor(
                                if (appsByPassOpen.size == apps.size)
                                    "#FFDAD9D9" else "#FF21D012"
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImpelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launchWhenCreated {
            impelProxyList()
            impelClick()
        }
    }

    private fun impelClick() {
        binding.vImpelReturn.setOnClickListener {
            finish()
        }
        binding.vImpelSwitch.setOnClickListener {
            if (appsByPassOpen.size == apps.size) return@setOnClickListener
            appsByPassOpen.clear()
            if (apps.isNotEmpty()) {
                apps.forEach {
                    appsByPassOpen.add(it.pName)
                }
                binding.vImpelSwitch.setCardBackgroundColor(Color.parseColor("#FFDAD9D9"))
            }
            adapter?.notifyDataSetChanged()
        }
        binding.vImpelSave.setOnClickListener {
            AircraftUtils.impelAppsByPassOpen = appsByPassOpen.joinToString(",")
            toast("A reconnection is required for the proxy to take effect.")
            finish()
        }
        binding.vImpelInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(it: Editable?) {
                if (it == null) {
                    apps.clear()
                    if (appsMap.isNotEmpty()) {
                        appsMap.forEach { t, u ->
                            apps.add(u)
                        }
                    }
                    if (apps.isEmpty()) {
                        binding.vImpelNoData.visibility = View.VISIBLE
                    } else {
                        binding.vImpelNoData.visibility = View.GONE
                    }
                    adapter?.notifyDataSetChanged()
                } else {
                    it.toString().also { string ->
                        if (TextUtils.isEmpty(string)) {
                            apps.clear()
                            if (appsMap.isNotEmpty()) {
                                appsMap.forEach { t, u ->
                                    apps.add(u)
                                }
                            }
                            if (apps.isEmpty()) {
                                binding.vImpelNoData.visibility = View.VISIBLE
                            } else {
                                binding.vImpelNoData.visibility = View.GONE
                            }
                            adapter?.notifyDataSetChanged()
                        } else {
                            apps = apps.filter { it.name.contains(string) } as MutableList<MyImpelData>
                            if (apps.isEmpty()) {
                                binding.vImpelNoData.visibility = View.VISIBLE
                            } else {
                                binding.vImpelNoData.visibility = View.GONE
                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
    }

    private fun impelProxyList() {
        if (!TextUtils.isEmpty(AircraftUtils.impelAppsByPassOpen)) {
            appsByPassOpen = AircraftUtils.impelAppsByPassOpen.split(",") as MutableList<String>
        }
        App.myApplication.getNetViewModel().loadingApps.observe(this) {
            appsMap = it
            apps.clear()
            if (it.isNotEmpty()) {
                it.forEach { t, u ->
                    apps.add(u)
                }
            }
            binding.vImpelSwitch.setCardBackgroundColor(
                Color.parseColor(
                    if (appsByPassOpen.size == apps.size)
                        "#FFDAD9D9" else "#FF21D012"
                )
            )
            if (apps.isEmpty()) {
                binding.vImpelNoData.visibility = View.VISIBLE
            } else {
                binding.vImpelNoData.visibility = View.GONE
            }
            if (apps.isEmpty()) return@observe
            adapter = ImpelAdapter()
            binding.vImpelRecycler.adapter = adapter
        }
        App.myApplication.getNetViewModel().aircraftResolveApps(this)
    }
}