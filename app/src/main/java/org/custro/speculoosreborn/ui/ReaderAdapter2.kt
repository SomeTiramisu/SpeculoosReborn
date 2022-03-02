package org.custro.speculoosreborn.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.custro.speculoosreborn.databinding.ViewReaderPageBinding
import org.custro.speculoosreborn.renderer.Renderer


class ReaderAdapter2(private val pageCount: Int, private val renderer: Renderer): RecyclerView.Adapter<ReaderAdapter2.ViewHolder>() {
    class ViewHolder(itemViewBinding: ViewReaderPageBinding): RecyclerView.ViewHolder(itemViewBinding.root) {
        val pageImageView = itemViewBinding.pageImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewReaderPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pageImageView.renderer = renderer
        holder.pageImageView.index = position
    }

    override fun getItemCount(): Int {
        return pageCount
    }
}