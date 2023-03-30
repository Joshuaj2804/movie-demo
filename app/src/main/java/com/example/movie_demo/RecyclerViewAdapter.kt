package com.example.movie_demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(
    var contexts: Context,
    itemList: ArrayList<*>?,
    itemList2: ArrayList<*>,
    itemList3: ArrayList<*>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    var mItemList: List<*>?
    var mItemList2: List<*>
    var mItemList3: List<*>
    var intent: Intent? = null

    init {
        mItemList = itemList
        mItemList2 = itemList2
        mItemList3 = itemList3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_view_design, parent, false)
            ItemViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        viewHolder.itemView.setOnClickListener { view ->
            val sharedPreferences1 = contexts.getSharedPreferences("ids", Context.MODE_PRIVATE)
            @SuppressLint("CommitPrefEdits") val editor = sharedPreferences1.edit()
            editor.putString("ids", mItemList3[position] as String?)
            editor.apply()
            Log.d("recycid", (mItemList3[position] as String?)!!)
            val intent = Intent(view.context, DetailsPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            contexts.startActivity(intent)
        }
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mItemList == null) 0 else mItemList!!.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (mItemList!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onClick(view: View) {
        val sharedPreferences1 = contexts.getSharedPreferences("ids", Context.MODE_PRIVATE)
        val editor = sharedPreferences1.edit()

        //Adding values to editor

        //editor.putString("ids", mItemList3.get());
        //intent =  new Intent(contexts, DetailsPage.class);
        //contexts.startActivity(intent);
    }

    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvItem: TextView
        var imageItem: ImageView

        init {
            imageItem = itemView.findViewById(R.id.imageview)
            tvItem = itemView.findViewById(R.id.textView)
        }
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item = mItemList!![position] as String
        viewHolder.tvItem.text = item
        val item2 = mItemList2[position] as String
        if (item2 != "null") {
            try {
                Picasso.with(contexts).load(item2).fit().into(viewHolder.imageItem)
            } catch (ignored: IllegalArgumentException) {
            }
        } else {
            try {
                Picasso.with(contexts)
                    .load("https://m.media-amazon.com/images/M/MV5BZDI4MmJiMmMtMzQ3Mi00N2Y0LTlkYmUtYmQ0ZTQ1NzVlZmVjXkEyXkFqcGdeQXVyMDUyOTUyNQ@@._V1_.jpg")
                    .fit().into(viewHolder.imageItem)
            } catch (ignored: IllegalArgumentException) {
            }
        }
    }
}