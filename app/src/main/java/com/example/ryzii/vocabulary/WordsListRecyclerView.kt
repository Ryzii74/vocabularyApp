package com.example.ryzii.vocabulary

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.ryzii.vocabulary.data.Word
import kotlinx.android.synthetic.main.words_list_item.view.*
import android.view.LayoutInflater

class WordsListAdapter(private val items: List<Word>, private val listener: (Word) -> Unit) : RecyclerView.Adapter<WordsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.words_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, listener)
        holder.itemView.tag = item.id
    }

    override fun getItemCount() = items.size

    fun removeItem(position: Int) {
        (items as MutableList<Word>).removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    fun changeItem(position: Int, ru: String, en: String) {
        items[position].ru = ru
        items[position].en = en
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Word, listener: (Word) -> Unit) = with(itemView) {
            words_list_item_text.text = "${item.ru} / ${item.en}"
            setOnClickListener { listener(item) }
        }
    }
}