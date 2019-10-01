package cz.applifting.humansis.ui.main.distribute.upload

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.format
import cz.applifting.humansis.extensions.isNetworkConnected
import cz.applifting.humansis.extensions.simpleDrawable
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.model.db.PendingChangeLocal
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.main.PENDING_CHANGES_ACTION
import javax.inject.Inject


class UploadDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: UploadViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_dialog_upload_status, container)
        (activity?.application as App).appComponent.inject(this)

        val ivCloseDialog = rootView.findViewById<ImageView>(R.id.iv_cross)
        val tvChanges = rootView.findViewById<TextView>(R.id.tv_changes)
        val tvCurrentDataDate = rootView.findViewById<TextView>(R.id.tv_current_data_date)
        val ivConnectionStatus = rootView.findViewById<ImageView>(R.id.iv_connection_status)
        val tvConnectionStatus = rootView.findViewById<TextView>(R.id.tv_connection_status)
        val btnUpload = rootView.findViewById<Button>(R.id.btn_upload)

        val online = context?.isNetworkConnected() ?: false

        ivConnectionStatus.simpleDrawable(if (online) R.drawable.ic_online else R.drawable.ic_offline)
        tvConnectionStatus.text = getString(if (online) R.string.online else R.string.offline)

        viewModel.pendingChangesLD.observe(viewLifecycleOwner,
            Observer<List<PendingChangeLocal>?> {
                it?.let {
                    val localChanges = it.isNotEmpty()
                    tvChanges.text = getString(if (localChanges) R.string.pending_local_changes else R.string.no_pending_changes)
                    context?.let { context ->
                        tvChanges.setTextColor(ContextCompat.getColor(context, if (localChanges) R.color.negativeColor else R.color.positiveColor))
                    }
                    btnUpload.visible(localChanges)
                    btnUpload.setOnClickListener {
                        viewModel.uploadChanges()
                    }
                }
            })


        viewModel.lastUpdate?.let {
            tvCurrentDataDate.text = it.format()
        }

        viewModel.changesUploadedLD.observe(viewLifecycleOwner, Observer<Boolean> {
            context?.let {
                LocalBroadcastManager.getInstance(it).sendBroadcast(Intent(PENDING_CHANGES_ACTION))
                // todo find better way how to redraw dialog
                dismiss()
                findNavController().navigate(R.id.uploadDialog)
            }
        })

        ivCloseDialog.setOnClickListener { dismiss() }

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return rootView
    }


}