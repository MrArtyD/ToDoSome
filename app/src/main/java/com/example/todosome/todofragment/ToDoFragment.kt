package com.example.todosome.todofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todosome.R
import com.example.todosome.TaskAdapter
import com.example.todosome.TaskListener
import com.example.todosome.database.TasksDatabase
import com.example.todosome.databinding.FragmentToDoBinding
import com.google.android.material.snackbar.Snackbar

class ToDoFragment : Fragment() {

    private lateinit var binding: FragmentToDoBinding
    private lateinit var viewModel: ToDoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val databaseDao = TasksDatabase.getInstance(application).databaseDao

        val viewModelFactory = ToDoViewModelFactory(databaseDao)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ToDoViewModel::class.java)

        initEvents()

        return binding.root
    }

    private fun initEvents() {
        val detailClick = TaskListener {
            viewModel.onTaskClicked(it)
        }
        val completeTaskClick = TaskListener {
            viewModel.onTaskCompleted(it)
        }

        val adapter = TaskAdapter(detailClick, completeTaskClick, requireContext())

        binding.rvTasks.adapter = adapter

        binding.btnActionAddTask.setOnClickListener {
            findNavController().navigate(ToDoFragmentDirections.actionToDoFragmentToCreateTaskFragment())
        }

        binding.btnClear.setOnClickListener {
            viewModel.onClear()
        }

        viewModel.buttonsVisible.observe(viewLifecycleOwner, {
            binding.btnClear.isEnabled = it
        })

        viewModel.clickedTask.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(
                    ToDoFragmentDirections.actionToDoFragmentToEditTaskFragment(
                        it.taskId,
                        it.title,
                        it.description
                    )
                )
                viewModel.taskWasClicked()
            }
        })

        viewModel.isTaskFinished.observe(viewLifecycleOwner, {
            if (it == true){
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.congratulations),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.taskCompleted()
            }
        })

        viewModel.tasks.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
    }
}