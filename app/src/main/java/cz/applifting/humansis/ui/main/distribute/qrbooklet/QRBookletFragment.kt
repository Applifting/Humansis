package cz.applifting.humansis.ui.main.distribute.qrbooklet

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
import cz.applifting.humansis.ui.main.distribute.beneficiary.BeneficiaryViewModel
import kotlinx.android.synthetic.main.fragment_beneficiary.pb_loading
import kotlinx.android.synthetic.main.fragment_beneficiary.tv_beneficiary
import kotlinx.android.synthetic.main.fragment_beneficiary.tv_distribution
import kotlinx.android.synthetic.main.fragment_beneficiary.tv_project
import kotlinx.android.synthetic.main.fragment_beneficiary.tv_status
import kotlinx.android.synthetic.main.fragment_beneficiary_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 9. 9. 2019
 */

class QRBookletFragment : BaseFragment(), ZXingScannerView.ResultHandler {

    companion object {
        private const val CAMERA_REQUEST_CODE = 0
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.toString()?.let {
            goToBeneficiaryFragment(it)
        }
    }

    val args: QRBookletFragmentArgs by navArgs()

    private val viewModel: BeneficiaryViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beneficiary_qr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.assign_booklet)
        (activity as MainActivity).supportActionBar?.subtitle = args.beneficiaryName

        viewModel.distributedLD.observe(viewLifecycleOwner, Observer {
            tv_status.setValue(getString(if (it) R.string.distributed else R.string.not_distributed))
            tv_status.setStatus(it)
            tv_beneficiary.setValue(args.beneficiaryName)
            tv_distribution.setValue(args.distributionName)
            tv_project.setValue(args.projectName)
        })

        viewModel.refreshingLD.observe(viewLifecycleOwner, Observer {
            pb_loading.visible(it)
            tv_status.visible(!it)
            tv_beneficiary.visible(!it)
            tv_distribution.visible(!it)
            tv_project.visible(!it)
        })

        viewModel.loadBeneficiary(args.beneficiaryId)

    }

    override fun onResume() {
        super.onResume()

        // todo decide what to do if the permission is not granted permanently
        if (!isCameraPermissionGranted()) {
            if (shouldShowExplanation()) {
                showExplanationDialog()
            } else {
                requestCameraPermission()
            }

        } else {
            qr_scanner.setResultHandler(this)
            qr_scanner.startCamera()
            qr_scanner.setAutoFocus(true)
            qr_scanner.setFormats(mutableListOf(BarcodeFormat.QR_CODE))
            // for HUAWEI phones, according to docs
            qr_scanner.setAspectTolerance(0.5f)
        }

    }

    override fun onPause() {
        super.onPause()
        qr_scanner.stopCamera()
    }

    private fun goToBeneficiaryFragment(bookletId: String) {
        val action = QRBookletFragmentDirections.actionQrBeneficiaryFragmentToBeneficiaryFragment(
            args.beneficiaryId,
            args.beneficiaryName,
            args.distributionName,
            args.projectName,
            args.distributionStatus,
            bookletId
        )
        findNavController().navigate(action)
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

    private fun requestCameraPermission() {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (activity as MainActivity).checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowExplanation(): Boolean {
        return shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    }

    private fun showExplanationDialog() {
        val builder = AlertDialog.Builder(activity as MainActivity)
        builder.setTitle(getString(R.string.camera_permission_dialog_title))
        builder.setMessage(getString(R.string.camera_permission_dialog_message))
        builder.setPositiveButton(getString(R.string.camera_permission_dialog_positive_btn_label)) { _, _ -> requestCameraPermission() }
        builder.setNegativeButton(getString(R.string.camera_permission_dialog_negative_btn_label)) { _, _ -> findNavController().navigateUp() }
        val dialog = builder.create()
        dialog.show()
    }

}