package com.square.android.presentation.view.mainLists

import com.square.android.presentation.presenter.mainLists.LatestSearch
import com.square.android.presentation.presenter.mainLists.SearchResult
import com.square.android.presentation.view.ProgressView

interface SearchView : ProgressView {
    fun updateLatestSearch(latestSearch: LatestSearch)

    fun showData(latestSearch: LatestSearch, searchType: Int)

    fun updateSearchData(searchData: SearchResult)
}