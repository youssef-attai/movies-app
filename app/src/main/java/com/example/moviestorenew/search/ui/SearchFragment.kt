package com.example.moviestorenew.search.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.moviestorenew.R
import com.example.moviestorenew.home.data.ApiInterface
import com.example.moviestorenew.home.data.Discover
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.search_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBar: EditText = view.findViewById(R.id.searchBar_EditText)
        view.findViewById<Button>(R.id.backButton).setOnClickListener {
            view.findNavController().navigateUp()
        }
        searchBar.requestFocus()
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            searchBar,
            0
        )

        val apiInterface = ApiInterface.create()
        var latestPage = 1

        val moviesSearchRecyclerView: RecyclerView =
            view.findViewById(R.id.moviesSearch_recyclerView)

        moviesSearchRecyclerView.adapter = MoviesSearchAdapter()

        searchBar.addTextChangedListener {
            latestPage = 1
            apiInterface.searchMovies(
                ApiInterface.API_KEY,
                query = it.toString(),
                page = latestPage
            )
                .enqueue(object : Callback<Discover?> {
                    override fun onResponse(
                        call: Call<Discover?>,
                        response: Response<Discover?>
                    ) {
                        if (response.body() != null) {
                            (moviesSearchRecyclerView.adapter as MoviesSearchAdapter)
                                .setMovies(response.body()!!)
                        }
                    }

                    override fun onFailure(
                        call: Call<Discover?>,
                        t: Throwable
                    ) {
                    }
                })
        }

        moviesSearchRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    latestPage++
                    apiInterface.searchMovies(
                        ApiInterface.API_KEY,
                        query = searchBar.text.toString(),
                        page = latestPage
                    )
                        .enqueue(object : Callback<Discover?> {
                            override fun onResponse(
                                call: Call<Discover?>,
                                response: Response<Discover?>
                            ) {
                                if (response.body() != null) {
                                    (moviesSearchRecyclerView.adapter as MoviesSearchAdapter)
                                        .addMovies(response.body()!!)
                                }
                            }

                            override fun onFailure(
                                call: Call<Discover?>,
                                t: Throwable
                            ) {
                            }
                        })
                }
            }
        })
    }
}