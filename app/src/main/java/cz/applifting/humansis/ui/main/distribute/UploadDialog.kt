package cz.applifting.humansis.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.format
import cz.applifting.humansis.extensions.visible
import java.util.*


class UploadStatusDialogFragment : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_dialog_upload_status, container)
        val ivCloseDialog = rootView.findViewById<ImageView>(R.id.iv_cross)
        val tvChanges = rootView.findViewById<TextView>(R.id.tv_changes)
        val tvCurrentDataDate = rootView.findViewById<TextView>(R.id.tv_current_data_date)
        val btnUpload = rootView.findViewById<Button>(R.id.btn_upload)

        // todo use conditions from network manager and db
        val offline = true
        val localChanges = true


        context?.let {
            tvChanges.setTextColor(ContextCompat.getColor(it, if (offline) R.color.negativeColor else R.color.positiveColor))
        }

        tvChanges.text = getString(if (localChanges) R.string.pending_local_changes else R.string.no_pending_changes)

        //todo get value from db
        tvCurrentDataDate.text = Date().format()

        btnUpload.visible(localChanges)
        btnUpload.setOnClickListener({
            // todo upload data
        })

        ivCloseDialog.setOnClickListener { dismiss() }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return rootView
    }


}