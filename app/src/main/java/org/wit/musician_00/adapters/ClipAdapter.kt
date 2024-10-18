package org.wit.musician_00.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.musician_00.databinding.CardClipBinding
import org.wit.musician_00.models.ClipModel

class ClipAdapter constructor(private var clips: List<ClipModel>) : RecyclerView.Adapter<ClipAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardClipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val placemark = clips[holder.adapterPosition]
        holder.bind(placemark)
    }

    override fun getItemCount(): Int = clips.size

    class MainHolder(private val binding : CardClipBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clip: ClipModel) {
            binding.clipTitle.text = clip.title
            binding.clipDescription.text = clip.description
        }
    }
}