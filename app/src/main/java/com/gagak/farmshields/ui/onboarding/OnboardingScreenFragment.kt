package com.gagak.farmshields.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gagak.farmshields.R
import com.gagak.farmshields.databinding.FragmentOnboardingScreenBinding

class OnboardingScreenFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE = "image"

        fun newInstance(title: String, description: String, image: Int): OnboardingScreenFragment {
            val fragment = OnboardingScreenFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_DESCRIPTION, description)
            bundle.putInt(ARG_IMAGE, image)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentOnboardingScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE)
        val description = arguments?.getString(ARG_DESCRIPTION)
        val image = arguments?.getInt(ARG_IMAGE)

        binding.textViewTitle.text = title
        binding.textViewDescription.text = description
        binding.imageView.setImageResource(image ?: R.drawable.placeholder)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
