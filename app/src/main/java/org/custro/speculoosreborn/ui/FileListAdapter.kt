package org.custro.speculoosreborn.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.App
import org.custro.speculoosreborn.R
import org.custro.speculoosreborn.databinding.ItemFileListBinding
import org.custro.speculoosreborn.databinding.ItemMangaCardBinding

class FileListAdapter(private val onMangaClickListener: (Uri) -> Unit): RecyclerView.Adapter<FileListAdapter.ViewHolder>() {
    class ViewHolder(itemViewBinding: ItemFileListBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
        val imageView = itemViewBinding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageDrawable(AppCompatResources.getDrawable(App.instance.baseContext, R.drawable.baseline_book_24))
        holder.textView.text = "file.cbz"
    }

    override fun getItemCount(): Int {
        return 10
    }

}