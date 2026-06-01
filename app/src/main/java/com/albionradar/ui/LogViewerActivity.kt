package com.albionradar.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.albionradar.R
import com.albionradar.data.EventLog
import com.albionradar.data.EventLogManager
import com.albionradar.data.EventType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogViewerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    private val logManager = EventLogManager.getInstance()
    
    private var currentFilter: EventType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)
        
        supportActionBar?.apply {
            setTitle(R.string.log_viewer_title)
            setDisplayHomeAsUpEnabled(true)
        }
        
        setupViews()
        loadLogs()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recycler_view)
        
        adapter = LogAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        
        // Filter button
        findViewById<View>(R.id.btn_filter)?.setOnClickListener {
            showFilterDialog()
        }
        
        // Clear button
        findViewById<View>(R.id.btn_clear)?.setOnClickListener {
            showClearConfirmation()
        }
        
        // Export button
        findViewById<View>(R.id.btn_export)?.setOnClickListener {
            exportLogs()
        }
    }

    private fun loadLogs() {
        val logs = if (currentFilter != null) {
            logManager.getLogsByType(currentFilter!!)
        } else {
            logManager.logs
        }
        adapter.updateLogs(logs)
    }

    private fun showFilterDialog() {
        val eventTypes = EventType.values()
        val names = arrayOf("All") + eventTypes.map { it.name.replace("_", " ") }.toTypedArray()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter Logs")
            .setItems(names) { _, which ->
                currentFilter = if (which == 0) null else eventTypes[which - 1]
                loadLogs()
            }
            .show()
    }

    private fun showClearConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.clear_logs)
            .setMessage("Are you sure you want to clear all logs?")
            .setPositiveButton("Clear") { _, _ ->
                logManager.clearLogs()
                loadLogs()
                Snackbar.make(recyclerView, R.string.logs_cleared, Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun exportLogs() {
        val json = logManager.exportAsJson()
        val fileName = "radar_logs_${System.currentTimeMillis()}.json"
        
        // Save to Downloads or share
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_TEXT, json)
            putExtra(Intent.EXTRA_SUBJECT, "Radar Logs")
        }
        startActivity(Intent.createChooser(intent, "Export Logs"))
        
        Snackbar.make(recyclerView, R.string.logs_exported, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * Adapter for log items
     */
    class LogAdapter(private val context: Context) : 
        RecyclerView.Adapter<LogAdapter.ViewHolder>() {
        
        private val logs = mutableListOf<EventLog>()
        private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        fun updateLogs(newLogs: List<EventLog>) {
            logs.clear()
            logs.addAll(newLogs)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_log, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val log = logs[position]
            
            holder.timeText.text = timeFormat.format(Date(log.timestamp))
            holder.typeText.text = log.eventType.name.replace("_", " ")
            holder.nameText.text = log.entityName
            holder.detailsText.text = log.details
            
            // Set color based on event type
            val color = when (log.eventType) {
                EventType.HOSTILE_DETECTED -> context.getColor(R.color.player_hostile)
                EventType.RESOURCE_DETECTED -> context.getColor(R.color.success)
                EventType.MOB_DETECTED -> context.getColor(R.color.mob_normal)
                EventType.CHEST_DETECTED -> context.getColor(R.color.chest)
                EventType.SERVICE_STARTED, EventType.SERVICE_STOPPED -> context.getColor(R.color.info)
                else -> context.getColor(R.color.text_primary)
            }
            holder.typeText.setTextColor(color)
            
            // Position info
            if (log.posX != null && log.posY != null) {
                holder.positionText.text = "%.1f, %.1f".format(log.posX, log.posY)
                holder.positionText.visibility = View.VISIBLE
            } else {
                holder.positionText.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int = logs.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val timeText: TextView = itemView.findViewById(R.id.text_time)
            val typeText: TextView = itemView.findViewById(R.id.text_type)
            val nameText: TextView = itemView.findViewById(R.id.text_name)
            val detailsText: TextView = itemView.findViewById(R.id.text_details)
            val positionText: TextView = itemView.findViewById(R.id.text_position)
        }
    }
}
