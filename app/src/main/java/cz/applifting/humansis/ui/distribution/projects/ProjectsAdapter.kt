package cz.applifting.humansis.ui.distribution.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.applifting.humansis.R
import cz.applifting.humansis.db.Project
import kotlinx.android.synthetic.main.item_project.view.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 14, August, 2019
 */
class ProjectsAdapter(private val projects: List<Project>) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false) as LinearLayout
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.layout.txt_name.text = projects[position].name
    }

    class ProjectViewHolder(val layout: LinearLayout): RecyclerView.ViewHolder(layout)
}