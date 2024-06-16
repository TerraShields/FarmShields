package com.gagak.farmshields.core.domain.model.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gagak.farmshields.core.domain.repository.main.MainRepository

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    fun getReports() = repository.getReports().cachedIn(viewModelScope)
}
