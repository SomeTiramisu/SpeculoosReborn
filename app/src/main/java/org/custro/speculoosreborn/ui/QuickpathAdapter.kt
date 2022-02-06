package org.custro.speculoosreborn.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.databinding.ItemFileListBinding
import org.custro.speculoosreborn.databinding.ItemQuickpathBinding
import java.io.File

class QuickpathAdapter(private val onPathClickListener: (File) -> Unit): ListAdapter<String, QuickpathAdapter.ViewHolder>(StringDiffCallback) {
    //TODO fix user submited list, only allow submitpath
    private var path = File("")

    class ViewHolder(itemViewBinding: ItemQuickpathBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
        val arrow = itemViewBinding.arrow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuickpathBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.textView.text = item
        if(position == itemCount) {
            holder.arrow.visibility = GONE
        }
        holder.itemView.setOnClickListener {
            var newPath = path
            while(newPath.name != item) {
                newPath = newPath.parentFile!!
            }
            onPathClickListener(newPath)
        }
    }

    fun submitDir(dir: File) {
        path = dir
        val newPath =
            dir.toString()
                .split("/")
                .filter { it.isNotEmpty() }
        submitList(newPath)
    }

    companion object {
        object StringDiffCallback: DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }
}