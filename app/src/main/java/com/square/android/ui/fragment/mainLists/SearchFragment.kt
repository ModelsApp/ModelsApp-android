package com.square.android.ui.fragment.mainLists

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.extensions.hideKeyboard
import com.square.android.presentation.presenter.mainLists.*
import com.square.android.ui.fragment.BaseFragment
import com.square.android.presentation.view.mainLists.SearchView
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_item.view.*
import java.util.*

import com.square.android.extensions.setHighLightedText

class SearchFragment(private var searchType: Int): BaseFragment(), SearchView{

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    @ProvidePresenter
    fun providePresenter() = SearchPresenter(searchType)

    private var timer = Timer()
    private val DELAY_MS: Long = 500

    private var resultTv: TextView? = null
    private var searchTv: TextView? = null

    private var resultLl: LinearLayout? = null
    private var searchLl: LinearLayout? = null

    private var inflater: LayoutInflater? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflater = LayoutInflater.from(activity)

        cancelTv.setOnClickListener {
            activity?.hideKeyboard()
            search_et.setText("")
        }

        //TODO sometimes loads two times
        search_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()

                if (!TextUtils.isEmpty(str) && str.length > 2) {
                    showProgress()
                    timer.cancel()
                    timer = Timer()
                    timer.schedule( object : TimerTask() {
                                override fun run() {
                                    presenter.loadResults(str)
                                }
                            }, DELAY_MS)
                } else{
                    timer.cancel()
                    hideProgress()
                    resultTv!!.visibility = View.GONE
                    resultLl!!.removeAllViews()
                }

                cancelTv.isEnabled = !TextUtils.isEmpty(str)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun showData(latestSearch: LatestSearch, searchType: Int) {
        searchTv = inflater!!.inflate(R.layout.search_title, null, false) as TextView
        searchTv!!.text = getString(R.string.latest_search)
        searchTv!!.visibility = View.GONE

        searchLl = LinearLayout(activity)
        searchLl!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        searchLl!!.orientation = LinearLayout.VERTICAL

        resultTv = inflater!!.inflate(R.layout.search_title, null, false) as TextView
        resultTv!!.visibility = View.GONE

        resultLl = LinearLayout(activity)
        resultLl!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        resultLl!!.orientation = LinearLayout.VERTICAL

        searchContainer.addView(resultTv)
        searchContainer.addView(resultLl)
        searchContainer.addView(searchTv)
        searchContainer.addView(searchLl)

        addLatestSearches(latestSearch.searches, latestSearch.type)
    }

    private fun addLatestSearches(searches: MutableList<SearchItem>, type: Int){
        searchLl!!.removeAllViews()

        searchTv!!.visibility = if(searches.isEmpty()) View.GONE else View.VISIBLE

        for(searchItem in searches){
            val latest = inflater!!.inflate(R.layout.search_item, null, false) as ConstraintLayout
            latest.searchTopText.text = searchItem.text
            latest.searchBottomText.text = searchItem.secondText
            latest.searchBottomText.visibility = if(TextUtils.isEmpty(searchItem.secondText)) View.GONE else View.VISIBLE

            latest.setOnClickListener {
                when(type){
                    POSITION_PLACES -> {
                        presenter.onPlaceClicked(false, searchItem)
                    }
                    POSITION_EVENTS -> {
                        presenter.onEventClicked(false, searchItem)
                    }
                    POSITION_CAMPAIGNS ->{
                        presenter.onCampaignClicked(false, searchItem)
                    }
                }
            }

            searchLl!!.addView(latest)
        }
    }

    override fun updateSearchData(searchResult: SearchResult) {
        hideProgress()

        addResults(searchResult)
    }

    private fun addResults(searchResult: SearchResult){
        resultLl!!.removeAllViews()

        resultTv!!.text = getString(R.string.result_format, searchResult.searchItems.size)

        resultTv!!.visibility = if(searchResult.searchItems.isEmpty()) View.GONE else View.VISIBLE

        resultLl!!.removeAllViews()

        for(searchItem in searchResult.searchItems){
            val result = inflater!!.inflate(R.layout.search_item, null, false) as ConstraintLayout
            result.searchTopText.text = searchItem.text
            result.searchBottomText.text = searchItem.secondText
            result.searchBottomText.visibility = if(TextUtils.isEmpty(searchItem.secondText)) View.GONE else View.VISIBLE
            result.searchIcon.visibility = View.GONE
            result.searchTopText.setHighLightedText(searchResult.searchedText)

            result.setOnClickListener {
                when(searchResult.type){
                    POSITION_PLACES -> {
                        presenter.onPlaceClicked(true, searchItem)
                    }
                    POSITION_EVENTS -> {
                        presenter.onEventClicked(true, searchItem)
                    }
                    POSITION_CAMPAIGNS ->{
                        presenter.onCampaignClicked(true, searchItem)
                    }
                }
            }

            resultLl!!.addView(result)
        }
    }

    override fun updateLatestSearch(latestSearch: LatestSearch) {
        addLatestSearches(latestSearch.searches, latestSearch.type)
    }

    override fun showProgress() {
        cancelTv.visibility = View.INVISIBLE
        searchProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        searchProgress.visibility = View.GONE
        cancelTv.visibility = View.VISIBLE
    }

}