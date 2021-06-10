package com.example.todosome

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todosome.database.Task
import com.example.todosome.databinding.TaskListItemBinding
import java.text.SimpleDateFormat

class TaskAdapter(val clickDetails: TaskListener, val clickCompleteTask: TaskListener, val context: Context) :
    ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickDetails, clickCompleteTask)
    }

    class ViewHolder private constructor(private val binding: TaskListItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task, clickDetails: TaskListener, clickCompleteTask: TaskListener) {

            binding.tvTitleTaskItem.text = item.title

            if (item.isCompleted){
                binding.itemLayout.setOnClickListener {}

                binding.tvDateTaskItem.text = context
                    .getString(R.string.completed_date, convertDateToString(item.completionTime))

                binding.cbTaskItem.isChecked = true
                binding.cbTaskItem.setOnClickListener {
                    binding.cbTaskItem.isChecked = true
                }

            }else{
                binding.itemLayout.setOnClickListener {
                    clickDetails.onClick(item)
                }

                binding.tvDateTaskItem.text = context
                    .getString(R.string.created_date, convertDateToString(item.creationTime))

                binding.cbTaskItem.setOnClickListener { checkBox ->
                    if (checkBox is CheckBox){
                        item.isCompleted = checkBox.isChecked
                        item.completionTime = System.currentTimeMillis()
                        clickCompleteTask.onClick(item)
                    }
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        private fun convertDateToString(creationTime: Long): String {
            return SimpleDateFormat("dd/MM/yy - HH:mm").format(creationTime).toString()
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, context)

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

