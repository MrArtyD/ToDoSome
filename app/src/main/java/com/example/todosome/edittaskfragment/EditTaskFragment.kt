package com.example.todosome.edittaskfragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
                alertTaskDeletion()
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

    private fun alertTaskDeletion() {
        AlertDialog.Builder(this.context)
            .setTitle(getString(R.string.delete_task_alert))
            .setMessage(getString(R.string.delete_alert_message))
            .setPositiveButton(getString(R.string.yes)) { dialogInterface: DialogInterface, i: Int ->
                deleteTask()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
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