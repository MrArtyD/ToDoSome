package com.example.todosome.edittaskfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todosome.R
import com.example.todosome.database.TasksDatabase
import com.example.todosome.databinding.EditTaskFragmentBinding

class EditTaskFragment : Fragment() {

    private lateinit var binding: EditTaskFragmentBinding
    private lateinit var viewModel: EditTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditTaskFragmentBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val databaseDao = TasksDatabase.getInstance(application).databaseDao

        val viewModelFactory = EditTaskViewModelFactory(databaseDao)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(EditTaskViewModel::class.java)

        viewModel.returnBack.observe(viewLifecycleOwner, {
            if (it == true) {
                findNavController()
                    .navigate(EditTaskFragmentDirections.actionEditTaskFragmentToToDoFragment())
                viewModel.doneEditing()
            }
        })

        initFields()
        setHasOptionsMenu(true)
        Log.d("EditTaskFragment", "Edit created")
        return binding.root
    }

    private fun initFields() {
        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        binding.etTitle.setText(args.title)
        binding.etDescription.setText(args.description)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_task_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_save -> {
                updateTask()
                true
            }
            R.id.item_delete -> {
                deleteTask()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun updateTask() {
        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        val newTitle = binding.etTitle.text.toString()
        val newDescription = binding.etDescription.text.toString()
        viewModel.updateTask(args.taskId, newTitle, newDescription)
    }

    private fun deleteTask() {
        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        viewModel.deleteTask(args.taskId)
    }
}