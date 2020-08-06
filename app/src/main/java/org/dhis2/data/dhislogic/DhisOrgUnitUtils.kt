package org.dhis2.data.dhislogic

import org.hisp.dhis.android.core.D2
import java.util.Date
import javax.inject.Inject

class DhisOrgUnitUtils @Inject constructor(val d2: D2) {

    fun dateInOrgUnitRange(dateToCheck: Date?, orgUnitUid: String?): Boolean {
        var inRange = true
        val orgUnit = d2.organisationUnitModule().organisationUnits()
            .uid(orgUnitUid).blockingGet()
        if (dateToCheck != null && orgUnit.openingDate() != null &&
            dateToCheck.before(orgUnit.openingDate())
        ) inRange = false
        if (dateToCheck != null && orgUnit.closedDate() != null &&
            dateToCheck.after(orgUnit.closedDate())
        ) inRange = false
        return inRange
    }
}
