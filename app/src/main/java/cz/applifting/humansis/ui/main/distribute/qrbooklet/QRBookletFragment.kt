package cz.applifting.humansis.ui.main.distribute.qrbooklet

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
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
            (activity as MainActivity).invalidateOptionsMenu()
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

        if (isCameraPermissionGranted()) {
            qr_scanner.setResultHandler(this)
            qr_scanner.startCamera()
            qr_scanner.setAutoFocus(true)
            qr_scanner.setFormats(mutableListOf(BarcodeFormat.QR_CODE))
            // for HUAWEI phones, according to docs
            qr_scanner.setAspectTolerance(0.5f)
        } else {
            requestCameraPermission()
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
        this.findNavController().navigate(action)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (CAMERA_REQUEST_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity as MainActivity, Manifest.permission.CAMERA)
                if (showRationale) {

                } else {

                }
            }
        }

    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity as MainActivity, arrayOf(Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    private fun isCameraPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (activity as MainActivity).checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
}