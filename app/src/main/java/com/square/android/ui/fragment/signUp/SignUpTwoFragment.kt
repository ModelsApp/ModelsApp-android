package com.square.android.ui.fragment.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.data.pojo.SignUpData
import com.square.android.extensions.content
import com.square.android.presentation.presenter.signUp.SignUpTwoPresenter
import com.square.android.presentation.view.signUp.SignUpTwoView
import com.square.android.ui.DROPDOWN_ITEM_TYPE_CHECKMARK
import com.square.android.ui.SimpleSpinnerAdapter
import com.square.android.ui.dialogs.MultipleSelectionDialog
import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_sign_up_2.*
import org.jetbrains.anko.bundleOf
import com.square.android.R

private const val EXTRA_MODEL = "EXTRA_MODEL"

enum class MultipleSelectionDialogType {
    TYPE_ADDITIONAL_SPECIALITIES,
    TYPE_CAPABILITIES,
    TYPE_PREFERENCES
}

class SignUpTwoFragment: BaseFragment(), SignUpTwoView {

    @InjectPresenter
    lateinit var presenter: SignUpTwoPresenter

    @ProvidePresenter
    fun providePresenter(): SignUpTwoPresenter = SignUpTwoPresenter(getModel())

    var imAitems: List<String> = listOf()

    var specialityItems: List<String> = listOf()

    var additionalSpecialitiesItems: MutableList<String> = mutableListOf()
    var selectedAdditionalSpecialities: List<String> = listOf()

    var capabilitiesItems: List<String> = listOf()
    var selectedCapabilities: List<String> = listOf()

    var preferencesItems: List<String> = listOf()
    var selectedPreferences: List<String> = listOf()

    var multipleSelectionDialog: MultipleSelectionDialog? = null

    var selectedimA: String = ""
    var selectedSpeciality: String = ""

    var initialLoad1 = true
    var initialLoad2 = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchVat.setOnCheckedChangeListener { buttonView, isChecked ->
            vatNumberRl.visibility = if(isChecked) View.VISIBLE else View.GONE
            if(et_vat_number.errorShowing){
                et_vat_number.setText("")
            }
        }

        switchRepresentation.setOnCheckedChangeListener { buttonView, isChecked ->
            representationRl.visibility = if(isChecked) View.VISIBLE else View.GONE
            if(et_representation.errorShowing){
                et_representation.setText("")
            }
        }

        imALl.setOnClickListener {
            spinnerimA.visibility = View.VISIBLE
            spinnerimA.isClickable = true
            spinnerimA.isFocusable = true

            spinnerimA.performClick()
            selectedimA = spinnerimA.selectedItem.toString()
        }

        mainSpecialityLl.setOnClickListener {
            spinnerMainSpeciality.visibility = View.VISIBLE
            spinnerMainSpeciality.isClickable = true
            spinnerMainSpeciality.isFocusable = true

            spinnerMainSpeciality.performClick()
            selectedSpeciality = spinnerMainSpeciality.selectedItem.toString()
            selectedAdditionalSpecialities = listOf()
            tvAdditionalSpecialities.text = ""
            additionalSpecialitiesItems = specialityItems.toMutableList()
            additionalSpecialitiesItems.removeAt(0)
        }

        additionalSpecialitiesLl.setOnClickListener {
            showSelectionDialog(MultipleSelectionDialogType.TYPE_ADDITIONAL_SPECIALITIES)
        }

        capabilitiesLl.setOnClickListener {
            showSelectionDialog(MultipleSelectionDialogType.TYPE_CAPABILITIES)
        }

        preferencesLl.setOnClickListener {
            showSelectionDialog(MultipleSelectionDialogType.TYPE_PREFERENCES)
        }
    }

    private fun showSelectionDialog(selectionType: MultipleSelectionDialogType) {
        multipleSelectionDialog = MultipleSelectionDialog(activity!!)

        when (selectionType) {
            MultipleSelectionDialogType.TYPE_ADDITIONAL_SPECIALITIES -> {
                multipleSelectionDialog?.show(additionalSpecialitiesItems, selectedAdditionalSpecialities.toMutableList()) {
                    selectedAdditionalSpecialities = it
                    if(selectedAdditionalSpecialities.isEmpty()){
                        tvAdditionalSpecialities.text = ""
                    } else{
                        tvAdditionalSpecialities.text = getString(R.string.selected_format, selectedAdditionalSpecialities.size)
                    }
                }
            }

            MultipleSelectionDialogType.TYPE_CAPABILITIES -> {
                multipleSelectionDialog?.show(capabilitiesItems, selectedCapabilities.toMutableList()) {
                    selectedCapabilities = it
                    if(selectedCapabilities.isEmpty()){
                        tvCapabilities.text = ""
                    } else{
                        tvCapabilities.text = getString(R.string.selected_format, selectedCapabilities.size)
                    }
                }
            }

            MultipleSelectionDialogType.TYPE_PREFERENCES -> {
                multipleSelectionDialog?.show(preferencesItems, selectedPreferences.toMutableList()) {
                    selectedPreferences = it
                    if(selectedPreferences.isEmpty()){
                        tvPreferences.text = ""
                    } else{
                        tvPreferences.text = getString(R.string.selected_format, selectedPreferences.size)
                    }
                }
            }
        }
    }

    override fun showData(signUpData: SignUpData) {

        //TODO get from api
        imAitems = resources.getStringArray(R.array.im_a_array).toList()

        specialityItems = presenter.info.specialitiesAndProfessionsLists!!.specialities.map { it.name }
        additionalSpecialitiesItems = specialityItems.toMutableList()
        capabilitiesItems = presenter.info.capabilitiesList!!.capabilities.map { it.name }
        preferencesItems = presenter.info.specialitiesAndProfessionsLists!!.professions.map { it.name }

        spinnerimA.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, imAitems)
        spinnerimA.visibility = View.INVISIBLE
        spinnerimA.isClickable = false
        spinnerimA.isFocusable = false
        spinnerimA.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(initialLoad1){
                    initialLoad1 = false
                } else{
                    selectedimA = imAitems[position]
                }
            }
        }

        spinnerMainSpeciality.adapter = SimpleSpinnerAdapter(activity!!, DROPDOWN_ITEM_TYPE_CHECKMARK, specialityItems)
        spinnerMainSpeciality.visibility = View.INVISIBLE
        spinnerMainSpeciality.isClickable = false
        spinnerMainSpeciality.isFocusable = false
        spinnerMainSpeciality.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(initialLoad2){
                    initialLoad2 = false
                } else{
                    selectedSpeciality = specialityItems[position]

                    selectedAdditionalSpecialities = listOf()
                    tvAdditionalSpecialities.text = ""
                    additionalSpecialitiesItems = specialityItems.toMutableList()
                    additionalSpecialitiesItems.removeAt(position)
                }
            }
        }
    }

    override fun validate(): Boolean {
        var allOk = true

        if(!isValid(selectedimA)){
            imAError.visibility = View.VISIBLE

            allOk = false
        } else{
            imAError.visibility = View.GONE
        }

        if(!isValid(selectedSpeciality)){
            specialityError.visibility = View.VISIBLE

            allOk = false
        } else{
            specialityError.visibility = View.GONE
        }

        if(selectedAdditionalSpecialities.isEmpty()){
            additionalSpecialitiesError.visibility = View.VISIBLE

            allOk = false
        } else{
            additionalSpecialitiesError.visibility = View.GONE
        }

        if(selectedCapabilities.isEmpty()){
            capabilitiesError.visibility = View.VISIBLE

            allOk = false
        } else{
            capabilitiesError.visibility = View.GONE
        }

        if(selectedPreferences.isEmpty()){
            preferencesError.visibility = View.VISIBLE

            allOk = false
        } else{
            preferencesError.visibility = View.GONE
        }

        if(switchRepresentation.isChecked && !isValid(et_representation.content)){

            et_representation.setText("")
            et_representation.showCustomError(getString(R.string.representation_error))

            allOk = false
        }

        if(switchVat.isChecked && !isValid(et_vat_number.content)){

            et_vat_number.setText("")
            et_vat_number.showCustomError(getString(R.string.vat_number_error))

            allOk = false
        }

        if(allOk){
            presenter.info.imA = selectedimA
            presenter.info.mainSpeciality = selectedSpeciality

            presenter.info.additionalSpecialities = selectedAdditionalSpecialities
            presenter.info.capabilities = selectedCapabilities
            presenter.info.preferences = selectedPreferences

            presenter.info.representation = if(switchRepresentation.isChecked) et_representation.text.toString() else ""
            presenter.info.vatNumber = if(switchVat.isChecked) et_vat_number.text.toString() else ""
        }

        return allOk
    }

    private fun isValid(item: CharSequence) = item.toString().trim().isNotEmpty()

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: SignUpData): SignUpTwoFragment {
            val fragment = SignUpTwoFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

    private fun getModel(): SignUpData {
        return arguments?.getParcelable(EXTRA_MODEL) as SignUpData
    }

}