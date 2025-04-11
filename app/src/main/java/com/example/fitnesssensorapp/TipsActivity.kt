package com.example.fitnesssensorapp

// TipsActivity.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TipsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val tipsList = listOf(
        Tip("Stay Hydrated", "Drink at least 8 glasses of water daily"),
        Tip("Balanced Diet", "Include fruits, vegetables, and proteins in every meal"),
        Tip("Regular Exercise", "Aim for 30 minutes of physical activity daily"),
        Tip("Adequate Sleep", "Get 7-8 hours of quality sleep each night"),
        Tip("Stress Management", "Practice meditation or deep breathing exercises")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        recyclerView = findViewById(R.id.tipsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TipsAdapter(tipsList)
    }
}

data class Tip(val title: String, val description: String)

class TipsAdapter(private val tips: List<Tip>) :
    RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tipTitle)
        val description: TextView = itemView.findViewById(R.id.tipDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        holder.title.text = tips[position].title
        holder.description.text = tips[position].description
    }

    override fun getItemCount() = tips.size
}