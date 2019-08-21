package cz.applifting.humansis.ui.main.distribution.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.model.api.Project
import kotlinx.android.synthetic.main.item_project.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsAdapter(val onItemClick: (project: Project) -> Unit) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>(){

    private val projects: MutableList<Project> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false) as LinearLayout
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.layout.tv_name.text = projects[position].name
        holder.layout.setOnClickListener { onItemClick(projects[position]) }
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

    class ProjectViewHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout)
}