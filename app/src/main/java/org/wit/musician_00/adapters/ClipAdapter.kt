package org.wit.musician_00.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.musician_00.databinding.CardClipBinding
import org.wit.musician_00.models.ClipModel

interface ClipListener {
    fun onClipClick(clip: ClipModel)
}

class ClipAdapter constructor(private var clips: List<ClipModel>,
                              private val listener: ClipListener ) :
                              RecyclerView.Adapter<ClipAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardClipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val clip = clips[holder.adapterPosition]
        holder.bind(clip, listener)
    }

    override fun getItemCount(): Int = clips.size

    class MainHolder(private val binding : CardClipBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clip: ClipModel, listener: ClipListener) {
            binding.clipTitle.text = clip.title
            binding.clipDescription.text = clip.description
            Picasso.get().load(clip.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onClipClick(clip) }
        }
    }
}