package cz.applifting.humansis.ui.main.distribute.beneficiary.confirm

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import cz.applifting.humansis.R
import cz.applifting.humansis.model.ReferralType
import cz.applifting.humansis.ui.App
import cz.applifting.humansis.ui.HumansisActivity
import cz.applifting.humansis.ui.main.SharedViewModel
import kotlinx.android.synthetic.main.fragment_confirm_beneficiary.*
import kotlinx.android.synthetic.main.fragment_confirm_beneficiary.view.*
import javax.inject.Inject


class ConfirmBeneficiaryDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ConfirmBeneficiaryViewModel by viewModels { viewModelFactory }
    private lateinit var sharedViewModel: SharedViewModel

    val args: ConfirmBeneficiaryDialogArgs by navArgs()

    private var dialogView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = activity!!.layoutInflater.inflate(R.layout.fragment_confirm_beneficiary, null)
        val alertDialog = AlertDialog.Builder(activity!!, theme)
            .setView(dialogView)
            .setTitle(R.string.confirm_distribution)
            .setPositiveButton(R.string.confirm_distribution, null)
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(true)
            .create()
        // set listener this way so we can avoid dismiss on click
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (viewModel.tryEditBeneficiary()) {
                    dismiss()
                }
            }
        }
        return alertDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as App).appComponent.inject(this)
        sharedViewModel = ViewModelProviders.of(activity as HumansisActivity, viewModelFactory)[SharedViewModel::class.java]

        setupViews()

        viewModel.initBeneficiary(args.beneficiaryId)

        viewModel.referralTypeLD.observe(viewLifecycleOwner, Observer {
            spinner_referral_type.apply {
                val spinnerPos = it.toSpinnerPos()
                if (selectedItemPosition != spinnerPos) {
                    setSelection(spinnerPos)
                }
                viewModel.errorLD.value = null
            }
        })

        viewModel.referralNoteLD.observe(viewLifecycleOwner, Observer {
            tv_referral_note.apply {
                if (text.toString() != it) {
                    setText(it)
                }
                viewModel.errorLD.value = null
            }
        })

        viewModel.errorLD.observe(viewLifecycleOwner, Observer {
            tv_error.visibility = if (it == null) View.GONE else View.VISIBLE
            tv_error.text = it?.let { getString(it) }
        })

        return dialogView
    }

    private fun setupViews() {
        dialogView!!.apply {
            val spinnerOptions = viewModel.referralTypes
                .map { getString(it) }
            ArrayAdapter(context!!, android.R.layout.simple_spinner_item, 0, spinnerOptions).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_referral_type.adapter = adapter
            }
            spinner_referral_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.referralTypeLD.postValue(spinner_referral_type.selectedItemPosition.toReferralType())
                }
            }

            tv_referral_note.addTextChangedListener {
                viewModel.referralNoteLD.postValue(tv_referral_note.text?.toString())
            }
        }
    }

    override fun onDestroyView() {
        dialogView = null
        super.onDestroyView()
    }

    // +-1 for the "none" value which is not in the enum
    private fun ReferralType?.toSpinnerPos() = this?.let { it.ordinal + 1 } ?: 0
    private fun Int.toReferralType() = if (this == 0) null else ReferralType.values()[this - 1]
}