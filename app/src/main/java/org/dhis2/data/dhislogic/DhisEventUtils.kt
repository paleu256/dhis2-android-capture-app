package org.dhis2.data.dhislogic

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.program.ProgramStage
import org.joda.time.DateTime
import java.util.Date
import javax.inject.Inject

class DhisEventUtils @Inject constructor(
    val d2: D2,
    val dhisEnrollmentUtils: DhisEnrollmentUtils,
    val dhisOrgUnitUtils: DhisOrgUnitUtils,
    val dhisAccessUtils: DhisAccessUtils,
    val dhisCategoryUtils: DhisCategoryUtils
) {

    fun isEventEditable(eventUid: String): Boolean {
        val event =
            d2.eventModule().events().uid(eventUid).blockingGet()
        val program =
            d2.programModule().programs().uid(event.program()).blockingGet()
        val stage =
            d2.programModule().programStages().uid(event.programStage()).blockingGet()

        val isExpired = hasEventExpired(
            event.eventDate(),
            event.completedDate(),
            event.status(),
            program.completeEventsExpiryDays()!!,
            if (stage.periodType() != null) stage.periodType() else program.expiryPeriodType(),
            program.expiryDays()!!
        )
        val blockAfterComplete =
            event.status() == EventStatus.COMPLETED && stage.blockEntryForm()!!
        val isInCaptureOrgUnit = d2.organisationUnitModule().organisationUnits()
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .byUid().eq(event.organisationUnit()).one().blockingExists()
        val hasCatComboAccess = dhisCategoryUtils.getEventCatComboAccess(event)
        return dhisEnrollmentUtils.isEventEnrollmentOpen(event) &&
                !blockAfterComplete &&
                !isExpired &&
                dhisAccessUtils.getEventAccessDataWrite(event) &&
                dhisOrgUnitUtils.dateInOrgUnitRange(event.eventDate(), event.organisationUnit()) &&
                isInCaptureOrgUnit &&
                hasCatComboAccess
    }

    fun checkAddEventInEnrollment(
        enrollmentUid: String?,
        stage: ProgramStage,
        isSelected: Boolean
    ): Boolean {
        val enrollment = d2.enrollmentModule().enrollments().uid(enrollmentUid).blockingGet()
        val enrollmentStatusCheck =
            !(enrollment == null || enrollment.status() != EnrollmentStatus.ACTIVE)
        val totalEventCount = d2.eventModule().events()
            .byEnrollmentUid().eq(enrollmentUid)
            .byProgramStageUid().eq(stage.uid())
            .byDeleted().isFalse
            .blockingCount()
        val stageNotRepeatableZeroCount = stage.repeatable() != true &&
                totalEventCount == 0
        val stageRepeatableZeroCount = stage.repeatable() == true &&
                totalEventCount == 0
        val stageRepeatableCountSelected = stage.repeatable() == true &&
                totalEventCount > 0 && isSelected

        return enrollmentStatusCheck && (
                stageNotRepeatableZeroCount ||
                        stageRepeatableZeroCount ||
                        stageRepeatableCountSelected
                )
    }

    fun hasEventExpired(
        eventDate: Date?,
        completeDate: Date?,
        status: EventStatus?,
        compExpDays: Int,
        programPeriodType: PeriodType?,
        expDays: Int
    ): Boolean {
        if (status == EventStatus.COMPLETED && completeDate == null) {
            return false
        }

        val expiredBecauseOfPeriod: Boolean
        val expiredBecauseOfCompletion: Boolean = if (status == EventStatus.COMPLETED) {
            DateTime(completeDate).plusDays(compExpDays).isBeforeNow
        } else {
            false
        }

        return if (programPeriodType != null) {1
            val currentPeriod = d2.periodModule().periodHelper()
                .blockingGetPeriodForPeriodTypeAndDate(
                    programPeriodType,
                    eventDate!!,
                    false
                )
            val lastDateToEdit = DateTime(currentPeriod.endDate()).plusDays(expDays)
            expiredBecauseOfPeriod = lastDateToEdit.isBeforeNow
            expiredBecauseOfPeriod || expiredBecauseOfCompletion
        } else expiredBecauseOfCompletion
    }
}
