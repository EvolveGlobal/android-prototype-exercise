package com.evolve.podcastsearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private val adapter = PodcastsRecyclerAdapter(null)
    private val requestQueue by lazy { Volley.newRequestQueue(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up recycler view
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set up search view
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                searchPodcasts(query)
                return true
            }
        })
    }

    private fun searchPodcasts(keyword: String) {
        val formattedKeyword = keyword
            .trim()
            .replace(" ", "+")
            .lowercase()
        if (formattedKeyword.isBlank()) { return }

        val url = "https://itunes.apple.com/search?term=$formattedKeyword&limit=10&entity=podcast"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { json ->
                adapter.podcasts = json["results"] as? JSONArray
                adapter.notifyDataSetChanged()
            },
            null
        )
        requestQueue.add(request)
    }

}

class PodcastsRecyclerAdapter(var podcasts: JSONArray?) : RecyclerView.Adapter<PodcastsRecyclerAdapter.PodcastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PodcastsRecyclerAdapter.PodcastViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.podcast_row, parent, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(holder: PodcastsRecyclerAdapter.PodcastViewHolder, position: Int) {
        val safePodcasts = podcasts ?: return
        holder.bindPodcast(safePodcasts.getJSONObject(position))
    }

    override fun getItemCount(): Int {
        return podcasts?.length() ?: 0
    }

    class PodcastViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)

        fun bindPodcast(podcast: JSONObject) {
            nameTextView.text = podcast.getString("trackName")
            Picasso.get().load(podcast.getString("artworkUrl60")).into(imageView)
        }

    }

}