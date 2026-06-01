package com.albionradar.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.albionradar.R
import com.albionradar.util.Constants
import com.albionradar.util.PreferenceKeys

class MobFilterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MobFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mob_filter)
        
        supportActionBar?.apply {
            setTitle(R.string.mob_filter_title)
            setDisplayHomeAsUpEnabled(true)
        }
        
        setupViews()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recycler_view)
        
        adapter = MobFilterAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        findViewById<View>(R.id.btn_select_all)?.setOnClickListener {
            adapter.selectAll()
        }
        
        findViewById<View>(R.id.btn_deselect_all)?.setOnClickListener {
            adapter.deselectAll()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Adapter for mob filter items
     */
    class MobFilterAdapter(private val context: Context) : 
        RecyclerView.Adapter<MobFilterAdapter.ViewHolder>() {
        
        private val prefs = context.getSharedPreferences("mob_filters", Context.MODE_PRIVATE)
        
        private val mobTypes = listOf(
            MobFilterItem(Constants.MobType.LIVING_HARVESTABLE, "Living Harvestable", R.color.mob_normal),
            MobFilterItem(Constants.MobType.LIVING_SKINNABLE, "Living Skinnable", R.color.mob_normal),
            MobFilterItem(Constants.MobType.ENEMY, "Enemy", R.color.mob_normal),
            MobFilterItem(Constants.MobType.ENCHANTED_ENEMY, "Enchanted Enemy", R.color.mob_elite),
            MobFilterItem(Constants.MobType.MINI_BOSS, "Mini Boss", R.color.mob_mini_boss),
            MobFilterItem(Constants.MobType.BOSS, "Boss", R.color.mob_boss),
            MobFilterItem(Constants.MobType.DRONE, "Drone", R.color.mob_normal),
            MobFilterItem(Constants.MobType.MIST_BOSS, "Mist Boss", R.color.mob_elite),
            MobFilterItem(Constants.MobType.EVENTS, "Events", R.color.mob_normal)
        )

        fun selectAll() {
            mobTypes.forEach { item ->
                saveFilterState(item.type, true)
            }
            notifyDataSetChanged()
        }
        
        fun deselectAll() {
            mobTypes.forEach { item ->
                saveFilterState(item.type, false)
            }
            notifyDataSetChanged()
        }
        
        private fun saveFilterState(mobType: Int, isChecked: Boolean) {
            val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
            prefs.edit().putBoolean(key, isChecked).apply()
        }
        
        private fun loadFilterState(mobType: Int): Boolean {
            val key = "${PreferenceKeys.MOB_FILTER_PREFIX}$mobType"
            // Default: show bosses and elites
            return prefs.getBoolean(key, mobType in listOf(
                Constants.MobType.BOSS,
                Constants.MobType.MINI_BOSS,
                Constants.MobType.MIST_BOSS,
                Constants.MobType.ENCHANTED_ENEMY
            ))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_mob_filter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mobTypes[position]
            
            holder.nameText.text = item.name
            holder.nameText.setTextColor(ContextCompat.getColor(context, item.colorRes))
            
            val isChecked = loadFilterState(item.type)
            holder.checkBox.isChecked = isChecked
            
            holder.checkBox.setOnCheckedChangeListener { _, checked ->
                saveFilterState(item.type, checked)
            }
            
            holder.itemView.setOnClickListener {
                holder.checkBox.toggle()
            }
        }

        override fun getItemCount(): Int = mobTypes.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameText: TextView = itemView.findViewById(R.id.text_mob_name)
            val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        }
    }

    data class MobFilterItem(
        val type: Int,
        val name: String,
        val colorRes: Int
    )
}
