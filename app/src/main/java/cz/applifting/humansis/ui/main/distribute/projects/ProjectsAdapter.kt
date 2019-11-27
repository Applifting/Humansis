package cz.applifting.humansis.ui.main.distribute.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.ui.ProjectModel
import kotlinx.android.synthetic.main.item_project.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsAdapter(
    private val onItemClick: (project: ProjectModel) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    private val projects: MutableList<ProjectModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false) as CardView
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    fun updateProjects(newProjects: List<ProjectModel>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = newProjects[newItemPosition].id == projects[oldItemPosition].id
            override fun getOldListSize(): Int = projects.size
            override fun getNewListSize(): Int = newProjects.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = false
        })

        this.projects.clear()
        this.projects.addAll(newProjects)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ProjectViewHolder(val layout: CardView) : RecyclerView.ViewHolder(layout) {
        val context = layout.context
        val tvName = layout.tv_location
        val tvHouseHolds = layout.tv_households
        val flCompleted = layout.fl_completed

        fun bind(project: ProjectModel) {
            tvName.text = project.name
            tvHouseHolds.text = context.getString(R.string.households, project.numberOfHouseholds)
            layout.setOnClickListener { onItemClick(project) }
            flCompleted.setBackgroundColor(ContextCompat.getColor(context, if (project.completed) R.color.green else R.color.darkBlue))
        }
    }
}