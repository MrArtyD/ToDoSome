package com.example.todosome.todofragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
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
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun initEvents() {

        initAdapter()
        initButtonsClick()

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
    }

    private fun initAdapter() {
        val detailClick = TaskListener {
            viewModel.onTaskClicked(it)
        }
        val completeTaskClick = TaskListener {
            viewModel.onTaskCompleted(it)
        }

        val adapter = TaskAdapter(detailClick, completeTaskClick, requireContext())

        binding.rvTasks.adapter = adapter

        viewModel.allTasks.observe(viewLifecycleOwner, {
            viewModel.filterTasks(it)
        })

        viewModel.filteredTasks.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    private fun initButtonsClick() {
        binding.btnActionAddTask.setOnClickListener {
            findNavController().navigate(ToDoFragmentDirections.actionToDoFragmentToCreateTaskFragment())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_screen_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.filter_all -> {
                showAllTasks()
                true
            }

            R.id.filter_active -> {
                showActiveTasks()
                true
            }

            R.id.filter_completed -> {
                showCompletedTasks()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showAllTasks() {
        viewModel.showAllTasks()
        binding.tvListTitle.text = getString(R.string.all_tasks)
    }

    private fun showActiveTasks(){
        viewModel.showActiveTasks()
        binding.tvListTitle.text = getString(R.string.active_tasks)
    }

    private fun showCompletedTasks(){
        viewModel.showCompletedTasks()
        binding.tvListTitle.text = getString(R.string.completed_tasks)
    }
}