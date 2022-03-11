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
import com.myproject.alexnews.R
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.viewModels.FragmentMyNewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentMyNews : Fragment() {

    private lateinit var binding: FragmentMyNewsBinding
    private lateinit var viewModel: FragmentMyNewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().setTitle(R.string.app_name)
        binding = FragmentMyNewsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FragmentMyNewsViewModel::class.java]
        refreshInit()
        viewModel.loadNews(requireArguments(), requireContext())
        lifecycle.coroutineScope.launch(Dispatchers.IO) {
            viewModel.loadNews(requireArguments(), requireContext()).collectLatest {
                initRecyclerAdapter(it)
            }
        }
        return binding.root
    }

    private fun refreshInit() {
        binding.refresh.setColorSchemeResources(R.color.purple_200, R.color.purple_700)
        binding.refresh.setOnRefreshListener {
            viewModel.loadNews(requireArguments(), requireContext())
            binding.refresh.isRefreshing = false
        }
    }

    private fun initRecyclerAdapter(newsList: List<Article>) {
        lifecycleScope.launch {
            binding.rcView.layoutManager = LinearLayoutManager(context)
            val adapter = RecyclerAdapter(
                newsList,
                parentFragmentManager,
                lifecycleScope
            )
            binding.rcView.adapter = adapter
        }
    }
}
