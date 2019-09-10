package cz.applifting.humansis.ui.main.distribute.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.db.ProjectLocal
import kotlinx.android.synthetic.main.item_project.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsAdapter(
    private val onItemClick: (project: ProjectLocal) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    private val projects: MutableList<ProjectLocal> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false) as ConstraintLayout
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    fun updateProjects(newProjects: List<ProjectLocal>) {
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

    inner class ProjectViewHolder(val layout: ConstraintLayout): RecyclerView.ViewHolder(layout) {
        val context = layout.context
        val tvName = layout.tv_name
        val tvHouseHolds = layout.tv_households

        fun bind(project: ProjectLocal) {
            tvName.text = project.name
            tvHouseHolds.text = context.getString(R.string.households, project.numberOfHouseholds)
            layout.setOnClickListener { onItemClick(project) }
        }
    }
}