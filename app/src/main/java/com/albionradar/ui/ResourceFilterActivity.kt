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
import com.albionradartdar.util.PreferenceKeys
import com.google.android.material.tabs.TabLayout

class ResourceFilterActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResourceFilterAdapter
    
    private val resourceTypes = listOf("Fiber", "Hide", "Wood", "Ore", "Rock")
    private val tiers = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    private val enchants = listOf(0, 1, 2, 3, 4)
    
    private var currentResourceType = 0 // 0 = Fiber, etc.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_filter)
        
        supportActionBar?.apply {
            setTitle(R.string.resource_filter_title)
            setDisplayHomeAsUpEnabled(true)
        }
        
        setupViews()
    }

    private fun setupViews() {
        tabLayout = findViewById(R.id.tab_layout)
        recyclerView = findViewById(R.id.recycler_view)
        
        // Setup tabs for resource types
        resourceTypes.forEach { type ->
            tabLayout.addTab(tabLayout.newTab().setText(type))
        }
        
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentResourceType = tab?.position ?: 0
                adapter.updateResourceType(currentResourceType)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // Setup RecyclerView
        adapter = ResourceFilterAdapter(this, currentResourceType)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // Setup buttons
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
     * Adapter for resource filter items
     */
    inner class ResourceFilterAdapter(
        private val context: Context,
        private var resourceType: Int
    ) : RecyclerView.Adapter<ResourceFilterAdapter.ViewHolder>() {
        
        private val prefs = context.getSharedPreferences("resource_filters", Context.MODE_PRIVATE)
        private val items = mutableListOf<FilterItem>()
        
        init {
            updateResourceType(resourceType)
        }
        
        fun updateResourceType(type: Int) {
            resourceType = type
            items.clear()
            
            // Generate all combinations of tier and enchant
            for (tier in tiers) {
                for (enchant in enchants) {
                    items.add(FilterItem(tier, enchant))
                }
            }
            
            notifyDataSetChanged()
        }
        
        fun selectAll() {
            items.forEach { item ->
                item.isChecked = true
                saveFilterState(item)
            }
            notifyDataSetChanged()
        }
        
        fun deselectAll() {
            items.forEach { item ->
                item.isChecked = false
                saveFilterState(item)
            }
            notifyDataSetChanged()
        }
        
        private fun saveFilterState(item: FilterItem) {
            val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${resourceType}_${item.tier}_${item.enchant}"
            prefs.edit().putBoolean(key, item.isChecked).apply()
        }
        
        private fun loadFilterState(tier: Int, enchant: Int): Boolean {
            val key = "${PreferenceKeys.RESOURCE_FILTER_PREFIX}${resourceType}_${tier}_$enchant"
            return prefs.getBoolean(key, tier >= 4) // Default: show T4+
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_resource_filter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            // Set tier text
            holder.tierText.text = "T${item.tier}"
            
            // Set tier color
            val tierColor = when (item.tier) {
                1 -> R.color.tier_1
                2 -> R.color.tier_2
                3 -> R.color.tier_3
                4 -> R.color.tier_4
                5 -> R.color.tier_5
                6 -> R.color.tier_6
                7 -> R.color.tier_7
                8 -> R.color.tier_8
                else -> R.color.tier_1
            }
            holder.tierText.setTextColor(ContextCompat.getColor(context, tierColor))
            
            // Set enchant text
            holder.enchantText.text = if (item.enchant == 0) "" else ".${item.enchant}"
            
            // Set enchant color
            val enchantColor = when (item.enchant) {
                1 -> R.color.enchant_1
                2 -> R.color.enchant_2
                3 -> R.color.enchant_3
                4 -> R.color.enchant_4
                else -> R.color.enchant_0
            }
            holder.enchantText.setTextColor(ContextCompat.getColor(context, enchantColor))
            
            // Set checkbox state
            item.isChecked = loadFilterState(item.tier, item.enchant)
            holder.checkBox.isChecked = item.isChecked
            
            // Checkbox listener
            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                saveFilterState(item)
            }
            
            // Row click listener
            holder.itemView.setOnClickListener {
                holder.checkBox.toggle()
            }
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tierText: TextView = itemView.findViewById(R.id.text_tier)
            val enchantText: TextView = itemView.findViewById(R.id.text_enchant)
            val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        }
    }

    data class FilterItem(
        val tier: Int,
        val enchant: Int,
        var isChecked: Boolean = false
    )
}
