package com.square.android.ui.activity.profile

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Profession
import com.square.android.extensions.setVisible
import com.square.android.presentation.presenter.profile.ProfessionPresenter
import com.square.android.presentation.view.profile.ProfessionView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_profession.*
import ru.terrakok.cicerone.Navigator

class ProfessionActivity: BaseActivity(), ProfessionView {

    @InjectPresenter
    lateinit var presenter: ProfessionPresenter

    @ProvidePresenter
    fun providePresenter() = ProfessionPresenter()

    private var loadingDialog: LoadingDialog? = null

    lateinit var adapter: ProfessionAdapter

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_profession)

        saveTv.setOnClickListener {
            saveTv.isEnabled = false
            presenter.saveData()
        }

        professionsArrowBack.setOnClickListener { onBackPressed() }

        loadingDialog = LoadingDialog(this)
    }

    override fun showData(data: List<Profession>, selectedItems: MutableList<Profession>, primaryPosition: Int) {
        adapter = ProfessionAdapter(data, selectedItems, primaryPosition, object: ProfessionAdapter.Handler{
            override fun itemClicked(position: Int) {
                presenter.itemClicked(position)
            }
        })

        rvProfession.adapter = adapter
        rvProfession.layoutManager = LinearLayoutManager(rvProfession.context, RecyclerView.VERTICAL,false)
    }

    override fun changeSelection(position: Int, isSetToPrimary: Boolean) {
        saveTv.isEnabled = presenter.selectedList.size > 0 && presenter.selectedList.firstOrNull { it.main } != null

        adapter.notifyChanged(position, isSetToPrimary)
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() {
        rvProfession.setVisible(false)
        progressBar.setVisible(true)
    }

    override fun hideProgress() {
        progressBar.setVisible(false)
        rvProfession.setVisible(true)
    }

}