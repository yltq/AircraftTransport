package com.plot.evanescent.aircrafttransport.module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.github.shadowsocks.database.Profile
import com.plot.evanescent.aircrafttransport.R
import com.plot.evanescent.aircrafttransport.app.App
import com.plot.evanescent.aircrafttransport.databinding.FabulousLayoutServerContentBinding
import com.plot.evanescent.aircrafttransport.databinding.FabulousLayoutServerHeaderBinding

class ServerHeaderViewHolder(var binding: FabulousLayoutServerHeaderBinding)
class ServerContentViewHolder(var binding: FabulousLayoutServerContentBinding)

class FabulousServerAdapter(
    var context: Context,
    var now: Profile?,
    var pair: Pair<MutableList<Pair<String, String>>, MutableMap<String, MutableList<Profile>>>,
    var tap: (Profile) -> Unit
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return pair.first.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return pair.first[groupPosition].run {
            val children = pair.second[first]
            children?.size?:0
        }
    }

    override fun getGroup(groupPosition: Int): Any {
        return pair.first[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return pair.first[groupPosition].run {
            val children = pair.second[this.first]
            children?.get(childPosition)?:Profile()
        }
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition * 100L
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition * 10L
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        return pair.first[groupPosition].run {
            val header = ServerHeaderViewHolder(FabulousLayoutServerHeaderBinding.bind(
                LayoutInflater.from(context).inflate(R.layout.fabulous_layout_server_header, parent, false)))
            header.binding.vServerIcon.setImageResource(App.myApplication.getNetViewModel().getProfileNationId(first))
            header.binding.vServerName.text = second
            header.binding.root
        }
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        return pair.first[groupPosition].run {
            val list = pair.second[first]
            val header = ServerContentViewHolder(FabulousLayoutServerContentBinding.bind(
                LayoutInflater.from(context).inflate(R.layout.fabulous_layout_server_content, parent, false)))
            list?.run {
                val p = this[childPosition]
                header.binding.vServerName.text = p.cName
                header.binding.vServerSelect.isSelected = now != null && now!!.inSmart.not() &&
                        now!!.host == p.host && now!!.remotePort == p.remotePort
                header.binding.root.setOnClickListener {
                    tap.invoke(p)
                }
            }
            header.binding.root
        }
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }


}