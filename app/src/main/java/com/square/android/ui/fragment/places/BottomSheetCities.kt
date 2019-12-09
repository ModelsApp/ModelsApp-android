package com.square.android.ui.fragment.places

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.City
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.bottom_sheet_cities.*
import android.app.Dialog
import android.os.Build
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import android.graphics.Color
import com.square.android.R

class BottomSheetCities(var cities: List<City>, var selectedCity: City?, private val handler: Handler?, var mCancelable: Boolean = true ) : BottomSheetDialogFragment(), CitiesAdapter.Handler {

    private var selectedCityIndex = 0
    private var adapter: CitiesAdapter? = null

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {

        isCancelable = mCancelable

        return inflater.inflate(R.layout.bottom_sheet_cities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CitiesAdapter(cities, this)
        citiesRv.adapter = adapter
        citiesRv.layoutManager = LinearLayoutManager(citiesRv.context, RecyclerView.HORIZONTAL,false)
        citiesRv.addItemDecoration(MarginItemDecorator(citiesRv.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt(), false))

        selectedCity?.let {
            val city: City? = cities.firstOrNull{it.name == selectedCity!!.name}
            city?.let {
                selectedCityIndex = cities.indexOf(it)
                adapter?.setSelectedItem(selectedCityIndex)
            }
        }
    }

    override fun itemClicked(position: Int) {
        selectedCityIndex = position
        adapter?.setSelectedItem(selectedCityIndex)

        handler?.cityClicked(cities[selectedCityIndex])

        dialog.dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setWhiteNavigationBar(dialog)
        }

        return dialog
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window = dialog.window
        if (window != null) {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)

            val dimDrawable = GradientDrawable()

            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)

            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)

            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)

            window.setBackgroundDrawable(windowBackground)
        }
    }

    interface Handler {
        fun cityClicked(selectedCity: City)
    }

    companion object {
        const val TAG = "BottomSheetCities"
    }

}