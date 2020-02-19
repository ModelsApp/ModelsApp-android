package com.square.android.ui.fragment.explore

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.dialog_category.*

class CategoryDialog(private val context: Context) {

    var dialog: MaterialDialog? = null

    private var adapter: CategoriesAdapter? = null

    @SuppressLint("InflateParams")
    fun show(allCategories: List<String>, selectedCategories: MutableList<Int>, onClose: () -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_category, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(false)
                .cancelListener { onClose.invoke() }
                .canceledOnTouchOutside(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog!!.btnClearAll.isEnabled = selectedCategories.isNotEmpty()

        dialog!!.icClose.setOnClickListener {
            dialog?.cancel()
        }

        dialog!!.btnClearAll.setOnClickListener {
            adapter!!.clearSelections()

            dialog!!.btnClearAll.isEnabled = false
        }

        adapter = CategoriesAdapter(allCategories, selectedCategories, object: CategoriesAdapter.Handler{
            override fun itemClicked(position: Int) {
                adapter!!.changeSelection(position)

                dialog!!.btnClearAll.isEnabled = selectedCategories.isNotEmpty()
            }
        })

        dialog!!.categoriesRv.adapter = adapter
        dialog!!.categoriesRv.layoutManager = LinearLayoutManager(dialog!!.categoriesRv.context, RecyclerView.HORIZONTAL,false)
        dialog!!.categoriesRv.addItemDecoration(MarginItemDecorator(dialog!!.categoriesRv.context.resources.getDimension(R.dimen.v_8dp).toInt(), false))

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}