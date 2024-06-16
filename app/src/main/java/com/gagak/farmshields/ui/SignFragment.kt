package com.gagak.farmshields.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.adapter.SignAdapter
import com.gagak.farmshields.databinding.FragmentSignBinding

class SignFragment : Fragment() {

    private lateinit var signs: List<String>
    private var _binding: FragmentSignBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize signs from string array
        signs = resources.getStringArray(R.array.sign_array).toList()

        binding.rvSigns.layoutManager = LinearLayoutManager(context)
        binding.rvSigns.adapter = SignAdapter(signs) { sign ->
            // Handle click event
            // e.g., Pass the selected sign to ReportFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
