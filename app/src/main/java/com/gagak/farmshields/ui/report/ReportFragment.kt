package com.gagak.farmshields.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.adapter.SignAdapter
import com.gagak.farmshields.core.domain.model.viewmodel.main.MainViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import com.gagak.farmshields.databinding.FragmentReportBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getUser()

        userViewModel.userResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.user?.let { userDetail ->
                binding.apply {
                    tvUserName.text = userDetail.name
                    tvUserName.visibility = View.VISIBLE
                    ivProfileImage.visibility = View.VISIBLE
                    Glide.with(this@ReportFragment)
                        .load(userDetail.image)
                        .into(ivProfileImage)
                }
            }
        })

        // Inisialisasi BottomSheet

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
