package com.square.android.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.profile.*
import com.square.android.presentation.view.profile.ProfileBusinessView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.page_profile_business.*
import org.jetbrains.anko.bundleOf

class ProfileBusinessFragment: BaseFragment(), ProfileBusinessView, BusinessAdapter.Handler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): ProfileBusinessFragment {
            val fragment = ProfileBusinessFragment()

            val args = bundleOf(EXTRA_USER to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ProfileBusinessPresenter

    @ProvidePresenter
    fun providePresenter() = ProfileBusinessPresenter(arguments?.getParcelable(EXTRA_USER) as Profile.User)

    private var businessAdapter: BusinessAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_business, container, false)
    }

    override fun showData(user: Profile.User) {
//      TODO get details
        val details = ProfileItem(TYPE_DROPDOWN, getString(R.string.details), null, R.drawable.r_shop, listOf(
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.height_), getString(R.string.height_format, "179"), getString(R.string.bust_), "90"),
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.typology_), "Curvy", getString(R.string.waist_), "60"),
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.skin_), "Caucasian", getString(R.string.hip_), "90"),
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.eyes_), "Blue", getString(R.string.bra_), "B Cup"),
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.hair_), "Brown", getString(R.string.size_), "S"),
                ProfileSubItems.Detail(DETAIL_TYPE_DOUBLE, getString(R.string.length_), "Medium", getString(R.string.shoes_), "37"),
                ProfileSubItems.Detail(DETAIL_TYPE_FULL, getString(R.string.particular_signs_),"2 tattoos on the back")
        ))

        businessAdapter = BusinessAdapter(listOf(details), this)

        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvItems.adapter = businessAdapter
    }

    override fun clickViewClicked(position: Int) {
        businessAdapter?.setOpenedItem(position)
    }

// from old "invite your friends" button
//    private fun share(referralCode: String) {
//        val text = getString(R.string.shareContent, referralCode)
//
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.type = "text/plain"
//        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
//
//        startActivity(shareIntent)
//    }

}