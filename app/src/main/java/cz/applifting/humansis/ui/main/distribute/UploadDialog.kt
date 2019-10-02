package cz.applifting.humansis.ui.main.distribute

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.main.SharedViewModel
import javax.inject.Inject


class UploadDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as App).appComponent.inject(this)
        sharedViewModel = ViewModelProviders.of(activity as HumansisActivity, viewModelFactory)[SharedViewModel::class.java]

        val rootView = inflater.inflate(R.layout.fragment_dialog_upload_status, container)
        val ivCloseDialog = rootView.findViewById<ImageView>(R.id.iv_cross)
        val tvChanges = rootView.findViewById<TextView>(R.id.tv_changes)
        val tvCurrentDataDate = rootView.findViewById<TextView>(R.id.tv_current_data_date)
        val btnUpload = rootView.findViewById<Button>(R.id.btn_upload)

        // todo use conditions from network manager and db
        val localChanges = true

        if (sharedViewModel.pendingChangesLD.value == true) {
            tvChanges.setTextColor(ContextCompat.getColor(context!!, R.color.negativeColor))
            tvChanges.text = getString(R.string.pending_local_changes)
        } else {
            tvChanges.setTextColor(ContextCompat.getColor(context!!, R.color.positiveColor))
            tvChanges.text = getString(R.string.no_pending_changes)
        }

        //todo get value from db
        tvCurrentDataDate.text = sharedViewModel.lastDownloadLD.value

        btnUpload.visible(localChanges)
        btnUpload.setOnClickListener({
            // todo upload data
        })

        ivCloseDialog.setOnClickListener { dismiss() }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return rootView
    }


}