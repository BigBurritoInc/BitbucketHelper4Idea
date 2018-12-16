package ui

import bitbucket.data.ParticipantStatus
import java.awt.Color

/**
 * Colors configuration for different [ParticipantStatus]es
 */
object ParticipantStatusColors {
    private val TRANSPARENT = Color(0, 0, 0, 0)

    private val colors: Map<ParticipantStatus, Color> = mapOf(
            ParticipantStatus.UNAPPROVED to TRANSPARENT,
            ParticipantStatus.APPROVED to Color(89, 168, 105),
            ParticipantStatus.NEEDS_WORK to Color(255, 153, 51)
    )

    fun getColors(status: ParticipantStatus): Color {
        return colors.getOrDefault(status, TRANSPARENT)
    }
}