package cz.applifting.humansis.ui.main.distribute.beneficiaries

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cz.applifting.humansis.R
import cz.applifting.humansis.extensions.visible
import cz.applifting.humansis.ui.BaseFragment
import cz.applifting.humansis.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_beneficiaries.*

/**
 * Created by Vaclav Legat <vaclav.legat@applifting.cz>
 * @since 5. 9. 2019
 */

class BeneficiariesFragment : BaseFragment() {

    val args: BeneficiariesFragmentArgs by navArgs()

    private val viewModel: BeneficiariesViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beneficiaries, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = args.distributionName
        (activity as MainActivity).supportActionBar?.subtitle = getString(R.string.beneficiaries_title)


        val viewManager = LinearLayoutManager(context)
        val viewAdapter = BeneficiariesAdapter { beneficiary ->
            val action = BeneficiariesFragmentDirections.actionBeneficiariesFragmentToBeneficiaryFragment(
                beneficiary.id,
                getString(R.string.beneficiary_name, beneficiary.givenName, beneficiary.familyName),
                args.distributionName,
                args.projectName,
                beneficiary.distributed
            )
            this.findNavController().navigate(action)
        }

        lc_beneficiaries.init(viewAdapter)
        lc_beneficiaries.setOnRefreshListener {
            viewModel.loadBeneficiaries(args.distributionId, true)
        }


        viewModel.searchResults.observe(viewLifecycleOwner, Observer {
            viewAdapter.update(it)
        })

        viewModel.beneficiariesViewStateLD.observe(viewLifecycleOwner, Observer {
            lc_beneficiaries.setState(it)
            showControls(!it.isRetrieving)
        })

        viewModel.statsLD.observe(viewLifecycleOwner, Observer {
            val (reachedBeneficiaries, totalBeneficiaries) = it
            tv_beneficiaries_reached.text = getString(R.string.beneficiaries_reached, reachedBeneficiaries, totalBeneficiaries)

            if (totalBeneficiaries != 0) {
                pb_beneficiaries_reached.progress = reachedBeneficiaries * 100 / totalBeneficiaries
            }
        })

        context?.let {
            val searchDrawable = ContextCompat.getDrawable(it, R.drawable.ic_search)
            et_search.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null)
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }

        })

        btn_sort.setOnClickListener { viewModel.sortBeneficiaries() }

        viewModel.loadBeneficiaries(args.distributionId, false)

    }

    private fun showControls(show: Boolean) {
        tv_beneficiaries_reached.visible(show)
        pb_beneficiaries_reached.visible(show)
        et_search.visible(show)
        btn_sort.visible(show)
    }
}