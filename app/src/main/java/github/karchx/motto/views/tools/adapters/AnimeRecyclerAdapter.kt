package github.karchx.motto.views.tools.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import github.karchx.motto.R
import github.karchx.motto.search_engine.citaty_info_website.items.Anime

class AnimeRecyclerAdapter(
    fragment: Fragment,
    private val anime: ArrayList<Anime>
) : RecyclerView.Adapter<AnimeRecyclerAdapter.AnimeViewHolder>() {

    private val context = fragment.requireContext()
    private val activity = fragment.requireActivity()
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.motto_source_item, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val animeTitle = anime[position].title
        val animeUniqueID = anime[position].uniqueID

        holder.animeTitle.text = animeTitle
        setAnimeImage(holder, animeUniqueID)

        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            holder.animeImage.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
    }

    override fun getItemCount(): Int {
        return anime.size
    }

    private fun setAnimeImage(holder: AnimeViewHolder, animeUniqueID: String) {
        try {
            val imageId = getAnimeImageId(animeUniqueID)
            val image = ContextCompat.getDrawable(activity, imageId)
            holder.animeImage.setImageDrawable(image)
        } catch (ex: java.lang.Exception) {
        }
    }

    private fun getAnimeImageId(animeUniqueID: String): Int {
        val defType = "drawable"
        val packageName = context.packageName
        return context.resources.getIdentifier(animeUniqueID, defType, packageName)
    }

    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.findViewById(R.id.textview_motto_tab_title)
        val animeImage: ImageView = itemView.findViewById(R.id.imageview_motto_tab_image)
    }
}
