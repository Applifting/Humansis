package cz.applifting.humansis.ui.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.tintedDrawable
import cz.applifting.humansis.extensions.visible
import kotlinx.android.synthetic.main.titled_text_view.view.*

class TitledTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var isBookled = false

    init {
        LayoutInflater.from(context).inflate(R.layout.titled_text_view, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TitledTextView, 0, 0)
            val title = typedArray.getString(R.styleable.TitledTextView_titled_text_view_title)
            val value = typedArray.getString(R.styleable.TitledTextView_titled_text_view_value)

            title?.let {
                tv_title.text = title
            }

            value?.let {
                tv_value.text = value
            }

            typedArray.recycle()
        }
    }

    fun setValue(value: String?) {
        if (value == null) {
            tv_value.text = "?"

            if (isBookled) {
                btn_action.visibility = View.GONE
            }
        } else {
            tv_value.text = value

            if (isBookled) {
                btn_action.visibility = View.VISIBLE
            }
        }

    }

    fun setStatus(distributed: Boolean) {
        val color = if (distributed) R.color.positiveColor else R.color.negativeColor
        iv_status.tintedDrawable(R.drawable.ic_circle, color)
        iv_status.visible(true)
    }

    fun setAsBooklet(rescanAction: () -> Unit) {
        isBookled = true
        tv_value.typeface = Typeface.DEFAULT_BOLD
        btn_action.setOnClickListener { rescanAction() }
    }
}