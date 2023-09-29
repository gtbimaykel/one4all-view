package com.mcp.sample

import androidx.recyclerview.widget.RecyclerView
import com.mcp.sample.databinding.ItemSampleBinding

internal class SampleViewHolder(private val binding: ItemSampleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SampleData) {
        binding.inputField.apply {
            label = item.label
            helperText = item.helperText
            isRequired = item.isRequired
            type = item.inputType
        }
    }

}