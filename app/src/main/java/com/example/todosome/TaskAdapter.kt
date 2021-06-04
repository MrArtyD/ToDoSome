package com.example.todosome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todosome.database.Task
import com.example.todosome.databinding.TaskListItemBinding
import java.text.SimpleDateFormat

class TaskAdapter(val clickListener: TaskListener) :
    ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ViewHolder private constructor(val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task, clickListener: TaskListener) {
            binding.itemLayout.setOnClickListener {
                clickListener.onClick(item)
            }

            binding.tvTitleTaskItem.text = item.title
            binding.tvDateTaskItem.text = convertDateToString(item.creationTime)
        }

        @SuppressLint("SimpleDateFormat")
        private fun convertDateToString(creationTime: Long): String {
            return SimpleDateFormat("dd/MMM/yy - HH:mm").format(creationTime).toString()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)

            }
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

class TaskListener(val clickListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = clickListener(task)
}

