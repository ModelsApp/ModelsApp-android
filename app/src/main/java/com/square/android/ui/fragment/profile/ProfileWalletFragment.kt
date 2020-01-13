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
        //TODO
        val balance = ProfileItem(TYPE_CUSTOM, "€ 2.340,00", null,null, null, customType = CUSTOM_TYPE_BALANCE)

        //TODO
        val bankAccount = ProfileItem(TYPE_ADD, getString(R.string.bank_accounts), null, R.drawable.r_shop, listOf(
                ProfileSubItems.BankAccount(BANK_ACCOUNT_TYPE_MASTER_CARD, "*** 3453", false),
                ProfileSubItems.BankAccount(BANK_ACCOUNT_TYPE_MASTER_CARD, "*** 1100", true),
                ProfileSubItems.BankAccount(BANK_ACCOUNT_TYPE_VISA, "*** 4522", false)
        ), addType = ADD_TYPE_BANK_ACCOUNT)

        //TODO
        val paypalAccount = ProfileItem(TYPE_ADD, getString(R.string.paypal_account), null, R.drawable.r_shop, listOf(
                ProfileSubItems.PaypalAccount()
        ), addType = ADD_TYPE_PAYPAL_ACCOUNT)

        walletAdapter = ProfileItemAdapter(listOf(balance, bankAccount, paypalAccount), this)

        rvItems.itemAnimator = null
        rvItems.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        rvItems.adapter = walletAdapter
    }

    override fun clickViewClicked(position: Int) {
        walletAdapter?.setOpenedItem(position)
    }

    override fun createClicked(clickedType: Int) { }

    override fun addBankAccountClicked() {
        //TODO
    }

    override fun bankAccountCheckClicked(isChecked: Boolean) {
        //TODO
    }

    override fun addPaypalAccountClicked() {
        //TODO
    }

}