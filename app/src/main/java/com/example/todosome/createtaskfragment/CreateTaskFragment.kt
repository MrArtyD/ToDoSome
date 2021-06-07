package com.example.todosome.createtaskfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todosome.R
import com.example.todosome.database.TasksDatabase
import com.example.todosome.databinding.FragmentCreateTaskBinding
import com.google.android.material.snackbar.Snackbar


class CreateTaskFragment : Fragment() {
    private lateinit var binding: FragmentCreateTaskBinding
    private lateinit var viewModel: CreateTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val databaseDao = TasksDatabase.getInstance(application).databaseDao

        val viewModelFactory = CreateTaskViewModelFactory(databaseDao)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateTaskViewModel::class.java)

        initViewModelEvents()

        setHasOptionsMenu(true)
        Log.d("Create Task Fragment", "Create task - created")
        return binding.root
    }

    private fun initViewModelEvents() {
        viewModel.isEditTextFilled.observe(viewLifecycleOwner, {
            if (it == false) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.fill_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.editTextFilled()
            }
        })

        viewModel.taskCreated.observe(viewLifecycleOwner, {
            if (it == true) {
                this.findNavController().navigate(
                    CreateTaskFragmentDirections.actionCreateTaskFragmentToToDoFragment()
                )
                viewModel.doneWithCreation()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.create_task_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_create -> {
                createTask()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun createTask() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        viewModel.createTask(title, description)
    }
}