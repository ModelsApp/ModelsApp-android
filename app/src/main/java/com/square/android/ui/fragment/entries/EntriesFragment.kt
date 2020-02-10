package com.square.android.ui.fragment.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.entries.EntriesPresenter
import com.square.android.presentation.view.entries.EntriesView
import com.square.android.ui.fragment.BaseFragment

class EntriesFragment(private val campaign: Campaign?): BaseFragment(), EntriesView, SquareImagesAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: EntriesPresenter

    private var adapter: SquareImagesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_entries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO where to get participated user images?
//                adapter = SquareImagesAdapter( , this)
//
//                entriesRv.layoutManager = GridLayoutManager(context, 3)
//                entriesRv.adapter = adapter
//                entriesRv.addItemDecoration(GridItemDecoration(3,entriesRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))

    }

    override fun itemClicked(url: String) {
        //TODO show photo in dialog like in ApprovalFragment?
    }

}