package com.myproject.alexnews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentOfflineBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentOfflineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentOffline : Fragment() {

    private lateinit var binding: FragmentOfflineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfflineBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this)[FragmentOfflineViewModel::class.java]
        viewModel.loadNews(requireContext())
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            viewModel.loadNews(requireContext()).collectLatest {
                init(it)
            }
        }
        return binding.root
    }

    private fun init(dataLister: List<Article>) {
        lifecycleScope.launch {
            binding.apply {
                rcView.layoutManager = LinearLayoutManager(context)
                val adapter = RecyclerAdapter(
                    dataLister,
                    parentFragmentManager,
                    lifecycleScope
                )
                rcView.adapter = adapter
            }
        }
    }
}