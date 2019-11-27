package cz.applifting.humansis.ui.main.distribute.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_dialog_upload_status_error_info.*


/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 27, November, 2019
 */
class ErrorListFragment : BaseFragment() {

    private lateinit var uploadDialogViewModel: UploadDialogViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_upload_status_error_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        uploadDialogViewModel = ViewModelProviders.of(parentFragment!!, viewModelFactory)[UploadDialogViewModel::class.java]

        val adapter = ErrorListAdapter()
        rl_erros.adapter = adapter
        rl_erros.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(
            rl_erros.context,
            (rl_erros.layoutManager as LinearLayoutManager).orientation
        )
        rl_erros.addItemDecoration(dividerItemDecoration)

        uploadDialogViewModel.syncErrorListLD.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
        })

        btn_back.setOnClickListener {
            uploadDialogViewModel.changeScreen(Screen.MAIN)
        }
    }
}