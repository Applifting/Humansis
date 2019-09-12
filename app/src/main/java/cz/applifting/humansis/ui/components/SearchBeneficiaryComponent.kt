package cz.applifting.humansis.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import cz.applifting.humansis.R
import kotlinx.android.synthetic.main.component_search_beneficiary.view.*

class SearchBeneficiaryComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.component_search_beneficiary, this, true)
        orientation = HORIZONTAL
    }

    internal fun onTextChanged(search: (String) -> Unit?) {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s.toString())
            }

        })
    }

    internal fun onSort(sort: () -> Unit) {
        btn_sort.setOnClickListener { sort() }
    }

}
