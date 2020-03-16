package com.square.android.ui.dialogs

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
import com.square.android.utils.MultipleSelectionAdapter
import kotlinx.android.synthetic.main.dialog_multiple_selection.*

class MultipleSelectionDialog(private val context: Context) {

    var dialog: MaterialDialog? = null

    private var adapter: MultipleSelectionAdapter? = null

    @SuppressLint("InflateParams")
    fun show(items: List<String>, selectedItems: MutableList<String>, onClose: (selectedItems: List<String>) -> Unit) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_multiple_selection, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .cancelListener{
                    onClose.invoke(selectedItems)
                }
                .canceledOnTouchOutside(true)
                .build()

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter = MultipleSelectionAdapter(items, selectedItems, object: MultipleSelectionAdapter.Handler{
            override fun itemClicked(position: Int) {
                adapter!!.changeSelection(position)
            }
        })

        dialog!!.rvItems.adapter = adapter
        dialog!!.rvItems.layoutManager = LinearLayoutManager(dialog!!.rvItems.context, RecyclerView.VERTICAL,false)
        dialog!!.rvItems.addItemDecoration(MarginItemDecorator(dialog!!.rvItems.context.resources.getDimension(R.dimen.v_2dp).toInt(), true))

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}