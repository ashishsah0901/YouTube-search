package com.example.youtubesearch.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtubesearch.adapter.OnListItemClickListener
import com.example.youtubesearch.adapter.YoutubeAdapter
import com.example.youtubesearch.data.Item
import com.example.youtubesearch.databinding.ActivityMainBinding
import com.example.youtubesearch.util.Resource
import com.example.youtubesearch.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnListItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: YoutubeAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState != null) {
            binding.searchEditLayout.editText?.setText(viewModel.oldSearchQuery)
            binding.pageCountTextView.text = if(viewModel.searchVideoPage == 0) {
                "1"
            } else {
                viewModel.searchVideoPage.toString()
            }
        }
        adapter = YoutubeAdapter(this, this)
        setUpRecyclerView()
        subscribeToObservers()
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditLayout.editText?.text.toString()
            lifecycleScope.launch {
                viewModel.getSearchVideos(query, false)
            }
        }
        binding.nextButton.setOnClickListener {
            var query = binding.searchEditLayout.editText?.text.toString()
            if(query.isEmpty()) {
                if(viewModel.oldSearchQuery == null){
                    Toast.makeText(this, "Please Enter Something", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    query = viewModel.oldSearchQuery!!
                }
            }
            lifecycleScope.launch {
                viewModel.getSearchVideos(query, false)
            }
        }
        binding.previousButton.setOnClickListener {
            var query = binding.searchEditLayout.editText?.text.toString()
            if(query.isEmpty()) {
                if(viewModel.oldSearchQuery == null){
                    Toast.makeText(this, "Please Enter Something", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                } else {
                    query = viewModel.oldSearchQuery!!
                }
            }
            lifecycleScope.launch {
                viewModel.getSearchVideos(query, true)
            }
        }
    }

    private fun subscribeToObservers() = lifecycleScope.launch {
        viewModel.searchVideos.observe(this@MainActivity){
            when(it) {
                is Resource.Loading -> {
                    toggleLoading(true)
                }
                is Resource.Error -> {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                    toggleLoading(false)
                }
                is Resource.Success -> {
                    adapter.differ.submitList(it.data?.items)
                    binding.pageCountTextView.text = viewModel.searchVideoPage.toString()
                    toggleLoading(false)
                }
            }
        }
    }

    private fun toggleLoading(loading: Boolean) {
        binding.youtubeVideosRecyclerView.isVisible = !loading
        binding.loadingPB.isVisible = loading
    }

    private fun setUpRecyclerView() {
        binding.youtubeVideosRecyclerView.adapter = adapter
        binding.youtubeVideosRecyclerView.layoutManager  =LinearLayoutManager(this)
    }

    override fun onClick(video: Item) {
        Intent(this, YouTubePlayerActivity::class.java).apply {
            putExtra("videoId", video.id.videoId)
            putExtra("title", video.snippet.title)
            putExtra("description", video.snippet.description)
            putExtra("date", video.snippet.publishTime)
            startActivity(this)
        }
    }

    override fun onBackPressed() {
        if(viewModel.searchVideoPage == 0) {
            finish()
        } else {
            lifecycleScope.launch {
                viewModel.getSearchVideos(viewModel.oldSearchQuery!!, true)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outPersistentState.putString("searchQuery", binding.searchEditLayout.editText?.text.toString())
        outPersistentState.putString("pageCount", binding.pageCountTextView.text.toString())
    }
}