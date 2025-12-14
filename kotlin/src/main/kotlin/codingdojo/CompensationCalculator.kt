package codingdojo

import java.math.BigDecimal
import java.time.Duration

object CompensationCalculator {
    val MAX_OVERTIME_HOURS_RATE_1 = BigDecimal.TEN
    const val THRESHOLD_OVERTIME_HOURS_RATE_2 = 6

    fun calculateOvertime(hoursOvertimeTotal: BigDecimal, assignment: Assignment, briefing: Briefing): Overtime {
        var hoursOvertimeRate1 = BigDecimal.ZERO
        var hoursOvertimeRate2 = BigDecimal.ZERO

        if (shouldApplyRate1(assignment, briefing)) {
            hoursOvertimeRate1 = hoursOvertimeTotal
        } else {
            hoursOvertimeRate1 = calculateRate1(hoursOvertimeTotal)
            hoursOvertimeRate2 = calculateRate2(hoursOvertimeTotal, hoursOvertimeRate1, assignment)
        }

        return Overtime(hoursOvertimeRate1, hoursOvertimeRate2)
    }

    // Helper function to decide if Rate 1 applies
    private fun shouldApplyRate1(assignment: Assignment, briefing: Briefing): Boolean {
        return !briefing.watcode && !briefing.z3 && !assignment.isUnionized ||
                briefing.hbmo && assignment.isUnionized ||
                (briefing.watcode && !assignment.isUnionized && briefing.foreign) ||
                (briefing.foreign && !assignment.isUnionized) ||
                (briefing.watcode && assignment.isUnionized)
    }

    // Helper function to calculate Rate 1
    private fun calculateRate1(hoursOvertimeTotal: BigDecimal): BigDecimal {
        return if (hoursOvertimeTotal.compareTo(MAX_OVERTIME_HOURS_RATE_1) < 1) {
            hoursOvertimeTotal
        } else {
            MAX_OVERTIME_HOURS_RATE_1
        }
    }

    // Helper function to calculate Rate 2
    private fun calculateRate2(hoursOvertimeTotal: BigDecimal, hoursOvertimeRate1: BigDecimal, assignment: Assignment): BigDecimal {
        var hoursOvertimeRate2 = hoursOvertimeTotal.subtract(hoursOvertimeRate1)
        if (assignment.isUnionized) {
            val threshold = calculateThreshold(assignment, THRESHOLD_OVERTIME_HOURS_RATE_2.toLong())
            hoursOvertimeRate2 = hoursOvertimeRate2.min(threshold)
        }
        return hoursOvertimeRate2
    }

    // Helper function to calculate the threshold based on the assignment's duration
    private fun calculateThreshold(assignment: Assignment, threshold: Long): BigDecimal {
        val remainder: Duration = assignment.duration.minusHours(threshold)
        return if (remainder.isNegative) {
            BigDecimal.valueOf(assignment.duration.toSeconds() / 3600)
        } else {
            BigDecimal.valueOf(threshold)
        }
    }
}
