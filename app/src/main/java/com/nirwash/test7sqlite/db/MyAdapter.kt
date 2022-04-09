package com.nirwash.test7sqlite.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nirwash.test7sqlite.EditActivity
import com.nirwash.test7sqlite.databinding.RcItemBinding

class MyAdapter(listMain: ArrayList<ListItem>, contextMAct: Context) :
    RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextMAct

    inner class MyHolder(val binding: RcItemBinding, contextHolder: Context) :
        RecyclerView.ViewHolder(binding.root) {

        val context = contextHolder
        fun setData(item: ListItem) {
            binding.tvTitle.text = item.title
            binding.tvTime.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstants.I_TITLE_KET, item.title)
                    putExtra(MyIntentConstants.I_DESC_KET, item.desc)
                    putExtra(MyIntentConstants.I_URI_KET, item.uri)
                    putExtra(MyIntentConstants.I_ID_KET, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = RcItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(binding, context)
    }

    override fun getItemCount() = listArray.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])

    }

    fun updateAdapter(listItems: List<ListItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManager: MyDbManager) {
        dbManager.removeItemFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(position)

    }


}