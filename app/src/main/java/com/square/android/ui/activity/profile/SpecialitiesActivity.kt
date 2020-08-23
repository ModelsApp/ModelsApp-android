package com.square.android.ui.activity.profile

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Speciality
import com.square.android.extensions.setVisible
import com.square.android.presentation.presenter.profile.SpecialitiesPresenter
import com.square.android.presentation.view.profile.SpecialitiesView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.dialogs.LoadingDialog
import com.square.android.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_specialities.*
import ru.terrakok.cicerone.Navigator

class SpecialitiesActivity: BaseActivity(), SpecialitiesView {

    @InjectPresenter
    lateinit var presenter: SpecialitiesPresenter

    @ProvidePresenter
    fun providePresenter() = SpecialitiesPresenter()

    private var loadingDialog: LoadingDialog? = null

    lateinit var adapter: SpecialityAdapter

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.setTransparentStatusAndDrawBehind(this)

        setContentView(R.layout.activity_specialities)

        saveTv.setOnClickListener {
            saveTv.isEnabled = false
            presenter.saveData()
        }

        specialitiesArrowBack.setOnClickListener { onBackPressed() }

        loadingDialog = LoadingDialog(this)
    }

    override fun showData(data: List<Speciality>, selectedItems: MutableList<Speciality>, primaryPosition: Int) {
        adapter = SpecialityAdapter(data, selectedItems, primaryPosition, object: SpecialityAdapter.Handler{
            override fun itemClicked(position: Int) {
                presenter.itemClicked(position)
            }
        })

        rvSpecialities.adapter = adapter
        rvSpecialities.layoutManager = LinearLayoutManager(rvSpecialities.context, RecyclerView.VERTICAL,false)
    }

    override fun changeSelection(position: Int, isSetToPrimary: Boolean) {
        saveTv.isEnabled = presenter.selectedList.size > 3 && presenter.selectedList.firstOrNull { it.main } != null

        adapter.notifyChanged(position, isSetToPrimary)
    }

    override fun showLoadingDialog() {
        loadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun showProgress() {
        rvSpecialities.setVisible(false)
        progressBar.setVisible(true)
    }

    override fun hideProgress() {
        progressBar.setVisible(false)
        rvSpecialities.setVisible(true)
    }

}