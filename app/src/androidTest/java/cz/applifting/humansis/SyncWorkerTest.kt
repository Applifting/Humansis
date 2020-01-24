package cz.applifting.humansis

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.testing.TestWorkerBuilder
import cz.applifting.humansis.managers.LoginManager
import cz.applifting.humansis.model.Target
import cz.applifting.humansis.model.db.BeneficiaryLocal
import cz.applifting.humansis.model.db.DistributionLocal
import cz.applifting.humansis.model.db.ProjectLocal
import cz.applifting.humansis.model.db.SyncError
import cz.applifting.humansis.repositories.BeneficieriesRepository
import cz.applifting.humansis.repositories.DistributionsRepository
import cz.applifting.humansis.repositories.ErrorsRepository
import cz.applifting.humansis.repositories.ProjectsRepository
import cz.applifting.humansis.synchronization.SyncWorker
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SyncWorkerTest {

    private lateinit var context: Context
    private lateinit var worker: SyncWorker
    @MockK
    private lateinit var projectsRepository: ProjectsRepository
    @MockK
    private lateinit var distributionsRepository: DistributionsRepository
    @MockK
    private lateinit var beneficiariesRepository: BeneficieriesRepository
    @MockK
    private lateinit var errorsRepository: ErrorsRepository
    private val errors: MutableList<SyncError> = mutableListOf()
    @RelaxedMockK // TODO test setting values
    private lateinit var sharedPreferences: SharedPreferences
    @MockK
    private lateinit var loginManager: LoginManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        context = ApplicationProvider.getApplicationContext()
        worker = TestWorkerBuilder.from(context, SyncWorker::class.java).build().also {
            it.projectsRepository = projectsRepository
            it.distributionsRepository = distributionsRepository
            it.beneficieriesRepository = beneficiariesRepository
            it.errorsRepository = errorsRepository.apply {
                coEvery { clearAll() } answers { errors.clear() }
                coEvery { insertAll(any()) } answers { errors.addAll(firstArg()) }
            }
            it.sp = sharedPreferences
            it.loginManager = loginManager.apply {
                coEvery { tryInitDB() } returns true
            }
        }
    }

    @Test
    fun allEmpty() {
        projectsRepository.apply {
            coEvery { getProjectsOnline() } returns emptyList()
        }
        beneficiariesRepository.apply {
            coEvery { getAssignedBeneficieriesOfflineSuspend() } returns emptyList()
            coEvery { getAllReferralChangesOffline() } returns emptyList()
        }
        val result = runBlocking { worker.doWork() }
        assertSuccess(result)
    }

    @Test
    fun download() {
        val projectsCount = 2
        projectsRepository.apply {
            coEvery { getProjectsOnline() } returns List(projectsCount) { anyProject() }
        }
        val distributionCount = 3
        distributionsRepository.apply {
            coEvery { getDistributionsOnline(any()) } returns List(distributionCount) { anyDistribution() }
        }
        beneficiariesRepository.apply {
            coEvery { getAssignedBeneficieriesOfflineSuspend() } returns emptyList()
            coEvery { getAllReferralChangesOffline() } returns emptyList()
            coEvery { getBeneficieriesOnline(any()) } returns List(5) { anyBeneficiary() }
        }
        val result = runBlocking { worker.doWork() }
        assertSuccess(result)
        projectsRepository.apply {
            coVerify(exactly = 1) { getProjectsOnline() }
        }
        distributionsRepository.apply {
            coVerify(exactly = projectsCount) { getDistributionsOnline(any()) }
        }
        beneficiariesRepository.apply {
            coVerify(exactly = projectsCount * distributionCount) { getBeneficieriesOnline(any()) }
        }
    }

    private fun assertSuccess(result: ListenableWorker.Result) {
        println(errors)
        Assert.assertThat(result, instanceOf(ListenableWorker.Result.Success::class.java))
        coVerify(exactly = 0) { errorsRepository.insertAll(any()) }
    }

    private fun anyProject() = ProjectLocal(
        id = 0,
        name = "",
        numberOfHouseholds = 0
    )

    private fun anyDistribution() = DistributionLocal(
        id = 0,
        name = "",
        numberOfBeneficiaries = 0,
        commodities = emptyList(),
        dateOfDistribution = "",
        projectId = 0,
        target = Target.FAMILY,
        completed = false
    )

    private fun anyBeneficiary() = BeneficiaryLocal(
        id = 0,
        beneficiaryId = 0,
        givenName = null,
        familyName = null,
        distributionId = 0,
        distributed = false,
        vulnerabilities = emptyList(),
        reliefIDs = emptyList(),
        qrBooklets = emptyList(),
        edited = false,
        commodities = emptyList(),
        nationalId = null,
        originalReferralType = null,
        originalReferralNote = null,
        referralType = null,
        referralNote = null
    )
}