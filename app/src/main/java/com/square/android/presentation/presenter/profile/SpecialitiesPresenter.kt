package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.SpecialitiesData
import com.square.android.data.pojo.SpecialitiesResult
import com.square.android.data.pojo.Speciality
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.SpecialitiesView

@InjectViewState
class SpecialitiesPresenter(): BasePresenter<SpecialitiesView>(){

    private var data: SpecialitiesResult? = null
    private var list: List<Speciality> = listOf()
    var selectedList: MutableList<Speciality> = mutableListOf()

    init {
        launch {
            viewState.showProgress()
            loadData()
            viewState.hideProgress()
        }
    }

    private fun loadData() = launch{
        viewState.showProgress()

        data = repository.getUserSpecialities().await()

        list = data!!.list

        selectedList = data!!.userSpecialities.map{ Speciality( null, it.main, it.name) }.toMutableList()
        selectedList.forEach{ item ->
            item.id = list.firstOrNull { it.name == item.name }?.id
        }

        val primaryPosition: Int = if(selectedList.firstOrNull {it.main} == null){
            -1
        } else{
            list.indexOf(list.first{ it.name == selectedList.first{ selcitem -> selcitem.main }.name})
        }

        viewState.showData(list, selectedList, primaryPosition)

        viewState.hideProgress()
    }

    fun itemClicked(position: Int){
        val item = list[position]

        var setToPrimary = false

        if(selectedList.isEmpty() || selectedList.firstOrNull { it.main } == null){

            if(selectedList.firstOrNull { it.name == list[position].name } != null){
                selectedList.removeAll { it.name == list[position].name }
            }

            selectedList.add(item.apply { main = true })

            setToPrimary = true
        } else{
            val selectedContainsItem = selectedList.firstOrNull { it.name == item.name } != null

            if(selectedContainsItem){
                selectedList.removeAll{ it.name == item.name }
            } else {
                selectedList.add(item.apply { main = false })
            }
        }

        viewState.changeSelection(position, setToPrimary)
    }

    fun saveData() = launch{
        viewState.showLoadingDialog()

        repository.addUserSpecialities(SpecialitiesData(selectedList)).await()

        viewState.hideLoadingDialog()
    }

}