package com.square.android.presentation.view.explore

import com.square.android.presentation.presenter.explore.LatestSearch
import com.square.android.presentation.presenter.explore.SearchResult
import com.square.android.presentation.view.ProgressView

interface SearchView : ProgressView {
    fun updateLatestSearch(latestSearch: LatestSearch)

    fun showData(latestSearch: LatestSearch, searchType: Int)

    fun updateSearchData(searchData: SearchResult)
}