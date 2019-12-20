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
import com.square.android.presentation.view.profile.ProfileWalletView
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.page_profile_wallet.*
import org.jetbrains.anko.bundleOf

class ProfileWalletFragment: BaseFragment(), ProfileWalletView, ProfileItemAdapter.Handler, ProfileItemAdapter.WalletHandler {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(user: Profile.User): ProfileWalletFragment {
            val fragment = ProfileWalletFragment()

            val args = bundleOf(EXTRA_USER to user)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: ProfileWalletPresenter

    @ProvidePresenter
    fun providePresenter() = ProfileWalletPresenter(arguments?.getParcelable(EXTRA_USER) as Profile.User)

    private var walletAdapter: ProfileItemAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_profile_wallet, container, false)
    }

    override fun showData(user: Profile.User) {
        // TODO in ProfileSubItems -> create models for: (profile_subitem_login_with : {show unlink/set as primary/save widgets, change textcolor of text} when connected = true else gone ), profile_subitem_balance

        // TODO add items to adapter

        // TODO bind items in adapter

        walletAdapter = ProfileItemAdapter(listOf(), this)

        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvItems.adapter = walletAdapter
    }

    override fun clickViewClicked(position: Int) {
        walletAdapter?.setOpenedItem(position)
    }

    // TODO handler clicks etc.

    override fun createClicked(clickedType: Int) {
        // TODO
    }

}