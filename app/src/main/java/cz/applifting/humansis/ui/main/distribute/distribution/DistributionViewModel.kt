package cz.applifting.humansis.ui.main.distribute.distribution

import androidx.lifecycle.MutableLiveData
import cz.applifting.humansis.api.HumansisService
import cz.applifting.humansis.ui.BaseViewModel
import javax.inject.Inject

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 28, August, 2019
 */
class DistributionViewModel @Inject constructor(
    private val service: HumansisService
) : BaseViewModel() {
    val beneficieriesLD = MutableLiveData<Any>()

}