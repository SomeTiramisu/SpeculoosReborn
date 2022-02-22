package org.custro.speculoosreborn.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.custro.speculoosreborn.databinding.ItemMangaCardBinding
import org.custro.speculoosreborn.room.MangaEntity
import org.custro.speculoosreborn.ui.model.MangaCardModel
import org.custro.speculoosreborn.utils.CacheUtils

typealias Item = MangaCardModel

class MangaCardAdapter(private val owner: LifecycleOwner, private val onCardClickListener: (Uri) -> Unit): ListAdapter<Item, MangaCardAdapter.ViewHolder>(MangaDiffCallback) {

    class ViewHolder(itemViewBinding: ItemMangaCardBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
        val imageView = itemViewBinding.imageView
        val card = itemViewBinding.card
        val root = itemViewBinding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMangaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item.name?.let {
            holder.textView.text = it
        }
        owner.lifecycleScope.launch {
            item.cover.collect {
                val bitmap = BitmapFactory.decodeFile(it.path)
                holder.imageView.setImageBitmap(bitmap)
            }
        }
        holder.card.setOnClickListener {
            //TODO: use cached file
            onCardClickListener(Uri.parse(item.entity.uri))
        }
    }

    companion object {
        object MangaDiffCallback : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.entity.uri == newItem.entity.uri
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.entity == newItem.entity
            }
        }
    }
}