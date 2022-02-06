package org.custro.speculoosreborn.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.databinding.ItemFileListBinding
import org.custro.speculoosreborn.databinding.ItemQuickpathBinding
import java.io.File

class QuickpathAdapter(private val root: File): RecyclerView.Adapter<QuickpathAdapter.ViewHolder>() {
    class ViewHolder(itemViewBinding: ItemQuickpathBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val textView = itemViewBinding.textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuickpathBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = root.name
    }

    override fun getItemCount(): Int {
        return 1
    }


}