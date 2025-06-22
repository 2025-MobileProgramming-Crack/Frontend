package com.tu.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tu.project.databinding.ItemTaskBinding
import com.tu.project.model.ScheduleItem

class TaskAdapter(
    private var taskList: List<ScheduleItem>,
    private val onDeleteClick: (ScheduleItem) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        with(holder.binding) {
            tvTitle.text = task.title
            tvDescription.text = task.description
            tvDate.text = task.date

            btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun updateTasks(newTasks: List<ScheduleItem>) {
        taskList = newTasks
        notifyDataSetChanged()
    }
}
