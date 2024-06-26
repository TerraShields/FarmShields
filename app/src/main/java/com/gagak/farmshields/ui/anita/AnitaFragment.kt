package com.gagak.farmshields.ui.anita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gagak.farmshields.core.domain.adapter.anita.AnitaChatAdapter
import com.gagak.farmshields.core.domain.model.viewmodel.anita.AnitaViewModel
import com.gagak.farmshields.databinding.FragmentAnitaBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnitaFragment : Fragment() {

    private val anitaViewModel: AnitaViewModel by viewModel()
    private var _binding: FragmentAnitaBinding? = null
    private val binding get() = _binding!!
    private lateinit var anitaChatAdapter: AnitaChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anitaChatAdapter = AnitaChatAdapter(emptyList())
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = anitaChatAdapter

        binding.sendButton.setOnClickListener {
            val question = binding.inputEditText.text.toString()
            if (question.isNotEmpty()) {
                anitaViewModel.sendQuestion(question)
                binding.inputEditText.text?.clear()
                hideKeyboard()
            }
        }

        anitaViewModel.chatLiveData.observe(viewLifecycleOwner, Observer { chatMessages ->
            anitaChatAdapter = AnitaChatAdapter(chatMessages)
            binding.chatRecyclerView.adapter = anitaChatAdapter
            anitaChatAdapter.notifyDataSetChanged()
            binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
        })

        setupUI(view)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
