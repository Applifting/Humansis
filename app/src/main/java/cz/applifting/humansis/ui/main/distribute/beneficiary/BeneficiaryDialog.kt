package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import cz.applifting.humansis.R
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_beneficiary.*
import kotlinx.android.synthetic.main.fragment_beneficiary.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import javax.inject.Inject


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class BeneficiaryDialog : DialogFragment(), ZXingScannerView.ResultHandler {
    companion object {
        private const val CAMERA_REQUEST_CODE = 0
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: BeneficiaryViewModel by viewModels { viewModelFactory }
    private lateinit var sharedViewModel: SharedViewModel

    val args: BeneficiaryDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullscreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beneficiary, container, false)
        (activity?.application as App).appComponent.inject(this)
        sharedViewModel = ViewModelProviders.of(activity as HumansisActivity, viewModelFactory)[SharedViewModel::class.java]

        // Views
        view.apply {
            tv_status.setValue(getString(if (args.distributionStatus) R.string.distributed else R.string.not_distributed))
            tv_status.setStatus(args.distributionStatus)
            tv_beneficiary.setValue(args.beneficiaryName)
            tv_distribution.setValue(args.distributionName)
            tv_project.setValue(args.projectName)

            if (args.distributionStatus) {
                btn_action.text = context.getString(R.string.revert)
                btn_action.background = context.getDrawable(R.drawable.background_revert_btn)
            } else {
                btn_action.text = context.getString(R.string.confirm_distribution)
                btn_action.background = context.getDrawable(R.drawable.background_confirm_btn)
            }

            // Listeners
            btn_close.setOnClickListener { dismiss() }

            if (args.isQRVoucher) {
                tv_booklet.setAsBooklet {
                    viewModel.setScannedBooklet(null)
                    view?.qr_scanner_holder?.visibility = View.VISIBLE
                    startScanner(view)
                }

                if (!isCameraPermissionGranted()) {
                    requestCameraPermission()
                } else {
                    startScanner(view)
                }
            } else {
                tv_booklet.visibility = View.GONE
                qr_scanner_holder.visibility = View.GONE
            }

            btn_action.setOnClickListener {
                viewModel.editBeneficiary(!args.distributionStatus, args.beneficiaryId)
                sharedViewModel.markPendingChanges()
                view.btn_action.isEnabled = false
            }
        }


        // Observers
        viewModel.distributedLD.observe(viewLifecycleOwner, Observer { isDistributed ->
            sharedViewModel.forceOfflineReload(true)
            val text= if (isDistributed) {
                "Item was successfully distributed to ${args.beneficiaryName}"
            } else {
                "Distribution was successfully reverted."
            }
            sharedViewModel.showSnackbar(text)
            dismiss()
        })

        viewModel.qrBookletIdLD.observe(viewLifecycleOwner, Observer {
            view.btn_action.isEnabled = !(it == null && args.isQRVoucher)
            view?.tv_booklet?.setValue(it)
        })

        return view
    }

    override fun handleResult(rawResult: Result?) {
        val scannedId = rawResult.toString()
        qr_scanner_holder?.visibility = View.GONE

        viewModel.setScannedBooklet(scannedId)

        Toast.makeText(context, rawResult.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        qr_scanner.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qr_scanner.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (CAMERA_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted, we can scan a qr code
            } else {
                // permission not granted, go to previous screen
                findNavController().navigateUp()
            }
        }
    }

    private fun startScanner(view: View) {
        view.apply {
            qr_scanner.setResultHandler(this@BeneficiaryDialog)
            qr_scanner.startCamera()
            qr_scanner.setAutoFocus(true)
            qr_scanner.setFormats(mutableListOf(BarcodeFormat.QR_CODE))
            // for HUAWEI phones, according to docs
            qr_scanner.setAspectTolerance(0.5f)
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (activity as HumansisActivity).checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowExplanation(): Boolean {
        return shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    }

    private fun showExplanationDialog() {
        val builder = AlertDialog.Builder(activity as HumansisActivity)
        builder.setTitle(getString(R.string.camera_permission_dialog_title))
        builder.setMessage(getString(R.string.camera_permission_dialog_message))
        builder.setPositiveButton(getString(R.string.camera_permission_dialog_positive_btn_label)) { _, _ -> requestCameraPermission() }
        builder.setNegativeButton(getString(R.string.camera_permission_dialog_negative_btn_label)) { _, _ -> findNavController().navigateUp() }
        val dialog = builder.create()
        dialog.show()
    }
}