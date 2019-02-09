package ui

import bitbucket.data.PRParticipant
import bitbucket.data.ParticipantStatus
import com.intellij.util.ui.JBImageIcon
import java.awt.Image
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.Icon

object ReviewerComponentFactory {
    const val statusIconSize = 20
    const val avatarSize = 30
    private val defaultAvatar = scaleImage(resourceImage("avatar.png"), avatarSize)

    val defaultAvatarIcon = JBImageIcon(defaultAvatar)
    private var needsWorkIcon = JBImageIcon(scaleImage(resourceImage("needs_work.png"), statusIconSize))
    private var approvedIcon = JBImageIcon(scaleImage(resourceImage("approved.png"), statusIconSize))

    fun getStatusIcon(participant: PRParticipant): Icon? {
        val status = participant.status
        if (status == ParticipantStatus.NEEDS_WORK)
            return needsWorkIcon
        if (status == ParticipantStatus.APPROVED)
            return approvedIcon
        return null
    }

    private fun scaleImage(image: Image, size: Int) = image.getScaledInstance(size, size, BufferedImage.SCALE_DEFAULT)

    private fun resourceImage(relativePath: String) = scaleImage(ImageIO.read(javaClass.classLoader.getResource(relativePath)), avatarSize)

    fun createIconForPrParticipant(image: BufferedImage): Icon {
        return JBImageIcon(scaleImage(image, avatarSize))
    }
}