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
import github.karchx.motto.search_engine.citaty_info_website.items.TVSeries

class SeriesRecyclerAdapter(
    fragment: Fragment,
    private val series: ArrayList<TVSeries>
) : RecyclerView.Adapter<SeriesRecyclerAdapter.SeriesViewHolder>() {

    private val context = fragment.requireContext()
    private val activity = fragment.requireActivity()
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.motto_source_item, parent, false)
        return SeriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val seriesTitle = series[position].title
        val seriesUniqueID = series[position].uniqueID

        holder.seriesTitle.text = seriesTitle
        setChannelImage(holder, seriesUniqueID)

        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            holder.seriesImage.startAnimation(animation)
            lastPosition = holder.adapterPosition
        }
    }

    override fun getItemCount(): Int {
        return series.size
    }

    private fun setChannelImage(holder: SeriesViewHolder, seriesUniqueID: String) {
        try {
            val imageId = getChannelImageId(seriesUniqueID)
            val image = ContextCompat.getDrawable(activity, imageId)
            holder.seriesImage.setImageDrawable(image)
        } catch (ex: java.lang.Exception) {
        }
    }

    private fun getChannelImageId(seriesUniqueID: String): Int {
        val defType = "drawable"
        val packageName = context.packageName
        return context.resources.getIdentifier(seriesUniqueID, defType, packageName)
    }

    class SeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seriesTitle: TextView = itemView.findViewById(R.id.textview_motto_tab_title)
        val seriesImage: ImageView = itemView.findViewById(R.id.imageview_motto_tab_image)
    }
}
