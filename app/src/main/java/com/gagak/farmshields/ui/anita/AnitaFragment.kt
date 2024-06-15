package com.gagak.farmshields.ui.anita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.gagak.farmshields.core.domain.model.viewmodel.anita.AnitaViewModel
import com.gagak.farmshields.databinding.FragmentAnitaBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AnitaFragment : Fragment() {

    private val anitaViewModel: AnitaViewModel by viewModel()
    private var _binding: FragmentAnitaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendButton.setOnClickListener {
            val question = binding.inputEditText.text.toString()
            if (question.isNotEmpty()) {
                anitaViewModel.sendQuestion(question)
                binding.inputEditText.text?.clear()
            }
        }

        anitaViewModel.chatLiveData.observe(viewLifecycleOwner, Observer { response ->
            binding.chatTextView.text = response
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

