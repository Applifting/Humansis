package cz.applifting.humansis.ui.main.distribute.beneficiary

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import cz.applifting.humansis.extensions.tryNavigate
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.components.TitledTextView
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
        val INVALID_CODE = "Invalid code"
        val ALREADY_ASSIGNED = "Already assigned"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: BeneficiaryViewModel by viewModels { viewModelFactory }
    private lateinit var sharedViewModel: SharedViewModel

    val args: BeneficiaryDialogArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            setStyle(STYLE_NORMAL, R.style.FullscreenDialogDarkStatusBar)
        } else {
            setStyle(STYLE_NORMAL, R.style.FullscreenDialog)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                handleBackPressed()
            }
        }
    }

    private fun TitledTextView.setOptionalValue(value: String?) {
        visible(!value.isNullOrEmpty())
        setValue(value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beneficiary, container, false)
        (activity?.application as App).appComponent.inject(this)
        sharedViewModel = ViewModelProviders.of(activity as HumansisActivity, viewModelFactory)[SharedViewModel::class.java]

        view.apply {
            btn_close.setImageResource(if (args.isFromList) R.drawable.ic_arrow_back else R.drawable.ic_close_black_24dp)
            btn_close.setOnClickListener {
                handleBackPressed()
            }
            tv_booklet.visible(args.isQRVoucher)
        }

        viewModel.initBeneficiary(args.beneficiaryId)

        viewModel.beneficiaryLD.observe(viewLifecycleOwner, Observer {
            // Views
            view.apply {
                tv_status.setValue(getString(if (it.distributed) R.string.distributed else R.string.not_distributed))
                tv_status.setStatus(it.distributed)
                tv_beneficiary.setValue("${it.givenName} ${it.familyName}")
                tv_distribution.setValue(args.distributionName)
                tv_project.setValue(args.projectName)
                tv_screen_title.text = getString(if (it.distributed) R.string.detail else R.string.assign)
                tv_screen_subtitle.text = getString(R.string.beneficiary_name, it.givenName, it.familyName)
                tv_referral_type.setOptionalValue(it.referralType?.textId?.let { getString(it) })
                tv_referral_note.setOptionalValue(it.referralNote)

                if (it.distributed) {
                    if (it.edited) {
                        btn_action.text = context.getString(R.string.revert)
                        btn_action.background = context.getDrawable(R.drawable.background_revert_btn)
                    } else {
                        btn_action.visible(false)
                    }
                } else {
                    btn_action.text = context.getString(if (args.isQRVoucher) R.string.confirm_distribution else R.string.assign)
                    btn_action.background = context.getDrawable(R.drawable.background_confirm_btn)
                }

                // Handle QR voucher
                if (args.isQRVoucher) {
                    val booklet = it.qrBooklets?.firstOrNull()

                    if (!it.distributed) {
                        tv_booklet.setRescanActionListener {
                            viewModel.scanQRBooklet(null)
                        }
                    }

                    tv_booklet.setStatus(it.distributed)
                    tv_booklet.setValue(
                        when (booklet) {
                            INVALID_CODE -> getString(R.string.invalid_code)
                            ALREADY_ASSIGNED -> getString(R.string.already_assigned)
                            else -> booklet
                        }
                    )
                    view.btn_action.isEnabled = booklet.isValidBooklet

                    if (booklet == null) {
                        qr_scanner_holder.visibility = View.VISIBLE
                        startScanner(view!!)
                    } else {
                        qr_scanner_holder.visibility = View.GONE
                    }

                    if (!isCameraPermissionGranted() && !it.distributed) {
                        requestCameraPermission()
                    }
                }

                btn_action.setOnClickListener { _ ->
                    if (it.edited) {
                        if (viewModel.isAssignedInOtherDistribution) {
                            showConfirmBeneficiaryDialog(it)
                        } else {
                            viewModel.revertBeneficiary()
                        }
                    } else {
                        showConfirmBeneficiaryDialog(it)
                    }
                }

                when (viewModel.previousEditState) {
                    null -> viewModel.previousEditState = it.edited
                    it.edited -> {}
                    else -> {
                        // edit state changed
                        // Close dialog and notify shareViewModel after beneficiary is saved to db
                        val text = if (it.distributed) {
                            "Item was successfully distributed."
                        } else {
                            "Distribution was successfully reverted."
                        }
                        sharedViewModel.showToast(text)
                        dismiss()
                    }
                }
            }
        })

        viewModel.scannedIdLD.observe(viewLifecycleOwner, Observer {
            viewModel.scanQRBooklet(it)
        })

        viewModel.goBackEventLD.observe(viewLifecycleOwner, Observer {
            handleBackPressed()
        })

        return view
    }

    private val String?.isValidBooklet
        get() = (this != null && this != INVALID_CODE && this != ALREADY_ASSIGNED)

    override fun onResume() {
        super.onResume()
        if (qr_scanner_holder.visibility == View.VISIBLE) {
            startScanner(view!!)
        }
    }

    override fun handleResult(rawResult: Result?) {
        qr_scanner_holder?.visibility = View.GONE
        val scannedId = rawResult.toString()
        viewModel.checkScannedId(scannedId)
    }

    override fun onPause() {
        super.onPause()
        if (args.isQRVoucher) {
            qr_scanner.stopCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (CAMERA_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.beneficiaryLD.postValue(viewModel.beneficiaryLD.value)
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
            qr_scanner.setSquareViewFinder(true)
            qr_scanner.setFormats(mutableListOf(BarcodeFormat.QR_CODE))
            // for HUAWEI phones, according to docs
            qr_scanner.setAspectTolerance(0.1f)
        }
    }

    private val BeneficiaryLocal.hasUnsavedQr
        get() = args.isQRVoucher && qrBooklets?.firstOrNull().isValidBooklet && !distributed

    private fun handleBackPressed() {
        if (viewModel.beneficiaryLD.value?.hasUnsavedQr == true) {
            showDismissConfirmDialog()
        } else {
            dismiss()
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
        builder.setTitle(getString(cz.applifting.humansis.R.string.camera_permission_dialog_title))
        builder.setMessage(getString(cz.applifting.humansis.R.string.camera_permission_dialog_message))
        builder.setPositiveButton(getString(cz.applifting.humansis.R.string.camera_permission_dialog_positive_btn_label)) { _, _ -> requestCameraPermission() }
        builder.setNegativeButton(getString(cz.applifting.humansis.R.string.camera_permission_dialog_negative_btn_label)) { _, _ -> findNavController().navigateUp() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showConfirmBeneficiaryDialog(beneficiaryLocal: BeneficiaryLocal) {
        tryNavigate(
            R.id.beneficiaryDialog,
            BeneficiaryDialogDirections.actionBeneficiaryDialogToConfirmBeneficiaryDialog(
                beneficiaryLocal.id
            )
        )
    }

    private fun showDismissConfirmDialog() {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.confirm_scan_title)
            .setMessage(R.string.confirm_scan_question)
            .setPositiveButton(R.string.confirm_distribution) { _, _ ->
                viewModel.beneficiaryLD.value?.let { showConfirmBeneficiaryDialog(it) } ?: dismiss()
            }
            .setNegativeButton(R.string.dont_save) { _, _ -> dismiss() }
            .create()
            .show()
    }
}