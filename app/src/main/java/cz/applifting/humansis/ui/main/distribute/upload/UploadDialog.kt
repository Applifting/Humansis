package cz.applifting.humansis.ui.main.distribute.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.format
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_dialog_upload_status.*
import javax.inject.Inject


class UploadDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_dialog_upload_status, container)
        (activity?.application as App).appComponent.inject(this)
        sharedViewModel = ViewModelProviders.of(activity as HumansisActivity, viewModelFactory)[SharedViewModel::class.java]

        val ivCloseDialog = rootView.findViewById<ImageView>(R.id.iv_cross)
        val tvChanges = rootView.findViewById<TextView>(R.id.tv_changes)
        val tvCurrentDataDate = rootView.findViewById<TextView>(R.id.tv_current_data_date)
        val ivConnectionStatus = rootView.findViewById<ImageView>(R.id.iv_connection_status)
        val tvConnectionStatus = rootView.findViewById<TextView>(R.id.tv_connectoin_status)
        val btnSync = rootView.findViewById<Button>(R.id.btn_sync)
        val tvSyncFailed = rootView.findViewById<TextView>(R.id.tv_sync_failed)
        val tvSyncFailedDate = rootView.findViewById<TextView>(R.id.tv_sync_failed_date)

        val online = context?.isNetworkConnected() ?: false
        btnSync.isEnabled = online

        ivConnectionStatus.simpleDrawable(if (online) R.drawable.ic_online else R.drawable.ic_offline)
        tvConnectionStatus.text = getString(if (online) R.string.online else R.string.offline)

        sharedViewModel.pendingChangesLD.observe(viewLifecycleOwner, Observer {
            tvChanges.text = getString(if (it) R.string.pending_local_changes else R.string.no_pending_changes)
            tvChanges.setTextColor(ContextCompat.getColor(context!!, if (it) R.color.negativeColor else R.color.light_blue))
        })

        sharedViewModel.lastDownloadLD.observe(viewLifecycleOwner, Observer {
            tvCurrentDataDate.text = it?.format()
        })

        sharedViewModel.lastSyncFailedLD.observe(viewLifecycleOwner, Observer {
            tvSyncFailed.visible(it != null)
            tvSyncFailedDate.visible(it != null)
            tvSyncFailedDate.text = it?.format()
        })

        sharedViewModel.syncWorkerIsLoadingLD.observe(viewLifecycleOwner, Observer {
            btnSync.visibility = if (it) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            pb_upload.visible(it)
        })

        btnSync.setOnClickListener {
            sharedViewModel.synchronize()
        }

        ivCloseDialog.setOnClickListener { dismiss() }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return rootView
    }
}