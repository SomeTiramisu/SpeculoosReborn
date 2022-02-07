package org.custro.speculoosreborn.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.ItemFileListBinding
import org.custro.speculoosreborn.renderer.MangaRenderer
import org.custro.speculoosreborn.renderer.PdfRenderer
import java.io.File

class FileListAdapter(private val onFileClickListener: (File) -> Unit, private val onDirClickListener: (File) -> Unit): ListAdapter<File, FileListAdapter.ViewHolder>(FileDiffCallback) {
    class ViewHolder(itemViewBinding: ItemFileListBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
        val imageView = itemViewBinding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val itemUri = Uri.fromFile(item)
        holder.textView.text = item.name

        if(MangaRenderer.isSupported(itemUri) || PdfRenderer.isSupported(itemUri)) {
            holder.imageView.setImageResource(R.drawable.baseline_book_24)
            holder.itemView.setOnClickListener {
                onFileClickListener(item)
            }
        } else if(item.isFile) {
            holder.imageView.setImageResource(R.drawable.baseline_file_24)
        } else if(item.isDirectory) {
            holder.imageView.setImageResource(R.drawable.baseline_folder_24)
            holder.itemView.setOnClickListener {
                onDirClickListener(item)
            }
        }
    }


    companion object {
        object FileDiffCallback : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem == newItem
            }

        }
    }

}