package com.example.youtubesearch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.youtubesearch.data.Item
import com.example.youtubesearch.databinding.ListItemVideoBinding

class YoutubeAdapter(
    private val context: Context,
    private val listener: OnListItemClickListener
): RecyclerView.Adapter<YoutubeAdapter.YoutubeViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id.videoId == newItem.id.videoId
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class YoutubeViewHolder(val binding: ListItemVideoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeViewHolder {
        return YoutubeViewHolder(ListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: YoutubeViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context)
                .load(differ.currentList[position].snippet.thumbnails.medium.url)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(videoThumbnail)
            videoTitleTV.text = differ.currentList[position].snippet.title
            publishTime.text = differ.currentList[position].snippet.publishTime.substring(0, 10) + " " + differ.currentList[position].snippet.publishTime.substring(11, 16)
            videoDetailTV.text = differ.currentList[position].snippet.description
            videoCL.setOnClickListener {
                listener.onClick(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
interface OnListItemClickListener{
    fun onClick(video: Item)
}