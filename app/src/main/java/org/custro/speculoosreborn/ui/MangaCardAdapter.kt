package org.custro.speculoosreborn.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.databinding.ItemMangaCardBinding
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.utils.CacheUtils

typealias Item = Pair<MangaEntity, Bitmap>

class MangaCardAdapter(private val onCardClickListener: (Uri) -> Unit): ListAdapter<Item, MangaCardAdapter.ViewHolder>(MangaDiffCallback) {

    class ViewHolder(itemViewBinding: ItemMangaCardBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
        val imageView = itemViewBinding.imageView
        val card = itemViewBinding.card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMangaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        Uri.parse(item.first.uri).lastPathSegment?.let {
            holder.textView.text = it.split(":").last()
        }
        //TODO: un wrapper autour de Manga et MangaDao pour autocorrection et chargement cover ?
        holder.imageView.setImageBitmap(item.second)
        holder.card.setOnClickListener {
            //TODO: use cached file
            onCardClickListener(Uri.parse(item.first.uri))
        }
    }

    companion object {
        object MangaDiffCallback : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.first.uri == newItem.first.uri
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.first == newItem.first
            }
        }
    }
}