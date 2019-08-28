package cz.applifting.humansis.ui.main.distribute.projects

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.api.Project
import kotlinx.android.synthetic.main.item_project.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsAdapter(val context: Context, val onItemClick: (project: Project) -> Unit) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>(){

    private val projects: MutableList<Project> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false) as ConstraintLayout
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]

        holder.layout.tv_name.text = project.name
        holder.layout.tv_households.text = context.getString(R.string.households, project.numberOfHouseholds)
        holder.layout.setOnClickListener { onItemClick(project) }
    }

    fun updateProjects(newProjects: List<Project>) {
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

    class ProjectViewHolder(val layout: ConstraintLayout): RecyclerView.ViewHolder(layout)
}