package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>, private val itemClickListener: (Repository) -> Unit, private val btnShareLister: (Repository) -> Unit) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repositorios = repositories[position]

        holder.nomeRepositorio.text = repositorios.name

        holder.itemView.setOnClickListener {
            itemClickListener(repositorios)
        }

        holder.ivFavorite.setOnClickListener {
            btnShareLister(repositorios)
        }
    }

    override fun getItemCount(): Int = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nomeRepositorio: TextView = view.findViewById(R.id.tv_preco)
        var ivFavorite: ImageView = view.findViewById(R.id.iv_favorite)

        init {
            view.apply {
                nomeRepositorio = findViewById(R.id.tv_preco)
                ivFavorite = findViewById(R.id.iv_favorite)
            }

        }
    }
}


