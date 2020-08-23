package com.square.android.presentation.presenter.profile

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.profile.ProfessionView

@InjectViewState
class ProfessionPresenter(): BasePresenter<ProfessionView>(){

    private var data: ProfessionsResult? = null

    private var list: List<Profession> = listOf()
    var selectedList: MutableList<Profession> = mutableListOf()

    lateinit var oldProfessions: List<Profession>

    init {
        launch {
            viewState.showProgress()
            loadData()
            viewState.hideProgress()
        }
    }

    private fun loadData() = launch{
        viewState.showProgress()

        data = repository.getUserProfessions().await()

        list = data!!.professions.distinct()

        oldProfessions = data!!.userProfessions.map { Profession(it.professionId, it.main) }.distinct()

        selectedList = data!!.userProfessions.map{ Profession( it.professionId, it.main) }.distinct().toMutableList()
        selectedList.forEach{ item ->
            item.id = list.firstOrNull { it.id == item.id }?.id
        }

        val primaryPosition: Int = if(selectedList.firstOrNull {it.main} == null){
            -1
        } else{
            list.indexOf(list.first{ it.id == selectedList.first{ selcitem -> selcitem.main }.id})
        }

        println("DSDSDSDDSSDD user professions: ${data!!.userProfessions.toString()}")

        println("DSDSDSDDSSDD List: ${list.toString()}")
        println("DSDSDSDDSSDD SelectedList: ${selectedList.toString()}")
        println("DSDSDSDDSSDD OldProfessions: ${oldProfessions.toString()}")

        viewState.showData(list, selectedList, primaryPosition)

        viewState.hideProgress()
    }

    fun itemClicked(position: Int){
        val item = list[position]

        var setToPrimary = false

        if(selectedList.isEmpty() || selectedList.firstOrNull { it.main } == null){

            if(selectedList.firstOrNull { it.id == list[position].id } != null){
                selectedList.removeAll { it.id == list[position].id }
            }

            selectedList.add(item.apply { main = true })

            setToPrimary = true
        } else{
            val selectedContainsItem = selectedList.firstOrNull { it.id == item.id } != null

            if(selectedContainsItem){
                selectedList.removeAll{ it.id == item.id }
            } else {
                selectedList.add(item.apply { main = false })
            }
        }

        viewState.changeSelection(position, setToPrimary)
    }

    fun saveData() = launch{
        viewState.showLoadingDialog()

        val toRemove = oldProfessions.distinct() - selectedList.distinct().map { Profession(it.id, it.main) }
        val toAdd = selectedList.distinct().map { Profession(it.id, it.main) } - oldProfessions.distinct()

        println("http DSDSDSDDSSDD toRemove: ${toRemove.toString()}")
        println("DSDSDSDDSSDD toAdd: ${toAdd.toString()}")

        toRemove.forEachIndexed { i , profession ->
            //TODO not working
            if(i == 1){
//                repository.deleteUserProfession1(profession.id!!.toInt()).await()
//                repository.deleteUserProfession2(profession.id!!).await()
//                repository.deleteUserProfession2(data!!.userProfessions.first {prof -> prof.professionId == profession.id }.id!!).await()
//                repository.deleteUserProfession3(ProfessionDelete1Data(profession.id!!)).await()
//                repository.deleteUserProfession3(ProfessionDelete1Data((data!!.userProfessions.first {prof -> prof.professionId == profession.id }.id!!))).await()
//                repository.deleteUserProfession4(ProfessionDelete2Data(profession.id!!.toInt())).await()
            }

        }

//        toAdd.forEach {
//            repository.addUserProfession(ProfessionData(it.id!!, it.main)).await()
//        }

        oldProfessions = selectedList

        viewState.hideLoadingDialog()
    }

}