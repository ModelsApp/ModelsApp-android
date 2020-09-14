package com.square.android.ui.fragment.dOfferInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.data.newPojo.OfferInfo
import com.square.android.ui.fragment.BaseNoMvpFragment
import kotlinx.android.synthetic.main.fragment_d_info.*

class DinnerBackClickedEvent()

class DInfoFragment(private val offerInfo: OfferInfo): BaseNoMvpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_d_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dInfoBack.setOnClickListener {
            eventBus.post(DinnerBackClickedEvent())
        }

        dInfoTitle.text = offerInfo.name

        //TODO where to get this text from
        dInfoText.text = "TODO"
    }

}