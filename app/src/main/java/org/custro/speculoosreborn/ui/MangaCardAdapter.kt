package org.custro.speculoosreborn.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.custro.speculoosreborn.databinding.ItemMangaCardBinding
import org.custro.speculoosreborn.room.Manga

class MangaCardAdapter(private val onCardClickListener: (Uri) -> Unit): ListAdapter<Manga, MangaCardAdapter.ViewHolder>(MangaDiffCallback) {

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
        Uri.parse(item.uri).lastPathSegment?.let {
            holder.textView.text = it
        }
        //TODO: un wrapper autour de Manga et MangaDao pour autocorrection et chargement cover ?
        val coverUri = Uri.parse(item.cover)
        val coverImage = BitmapFactory.decodeFile(coverUri.toFile().path)
        holder.imageView.setImageBitmap(coverImage)
        holder.card.setOnClickListener {
            onCardClickListener(Uri.parse(item.localUri))
        }
    }

    companion object {
        object MangaDiffCallback : DiffUtil.ItemCallback<Manga>() {
            override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                return oldItem == newItem
            }
        }
    }
}