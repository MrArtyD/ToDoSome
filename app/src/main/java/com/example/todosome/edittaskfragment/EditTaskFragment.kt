package com.example.todosome.edittaskfragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todosome.R
import com.example.todosome.database.TasksDatabase
import com.example.todosome.databinding.EditTaskFragmentBinding
import com.google.android.material.snackbar.Snackbar

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



        initEvents()
        initFields()
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun initEvents() {
        viewModel.returnBack.observe(viewLifecycleOwner, {
            if (it == true) {
                findNavController()
                    .navigate(EditTaskFragmentDirections.actionEditTaskFragmentToToDoFragment())
                viewModel.doneEditing()
            }
        })

        viewModel.isEditTextFilled.observe(viewLifecycleOwner, {
            if (it == false) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.fill_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
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

        hideKeyboard()
        viewModel.updateTask(args.taskId, newTitle, newDescription)
    }

    private fun deleteTask() {
        val args = EditTaskFragmentArgs.fromBundle(requireArguments())
        viewModel.deleteTask(args.taskId)
    }

    private fun hideKeyboard() {
        this.requireActivity().currentFocus?.let { view ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}