package com.example.blogwiser.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blogwiser.R
import com.example.blogwiser.blolg_details
import com.example.blogwiser.model.Blog

class BlogAdapter(private val context: Context, private val blogList: List<Blog>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBlogTitle: TextView = view.findViewById(R.id.text)
        val btnViewBlog: Button = view.findViewById(R.id.btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_view, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        holder.tvBlogTitle.text = blog.title

        holder.btnViewBlog.setOnClickListener {
            val intent = Intent(context, blolg_details::class.java)
            intent.putExtra("title", blog.title)
            intent.putExtra("description", blog.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }
}
