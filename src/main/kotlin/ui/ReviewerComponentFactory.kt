package ui

import bitbucket.data.PRParticipant
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel

object ReviewerComponentFactory {
    private const val imageSize = 40
    private val approved = resourceImage("approved.png")
    private val defaultAvatar = resourceImage("avatar.png")

    //it seems that bitbucket api v1 is not supply information about "need work" flags
    fun create(reviewer: PRParticipant): JLabel {
        val avatar = try { scaleImage(URL(reviewer.user.links.getIconHref())) } catch (e: IOException) { defaultAvatar }
        val icon = if (reviewer.approved) overlay(avatar, approved) else ImageIcon(avatar)
        val iconComponent =  JLabel(icon)
        iconComponent.toolTipText = reviewer.user.displayName
        return iconComponent
    }

    private fun overlay(image: Image, overlay: Image): ImageIcon {
        val combined = BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB)
        val g = combined.graphics
        g.drawImage(image, 0, 0, null)
        g.drawImage(overlay, 0, 0, null)
        return ImageIcon(combined)
    }

    private fun scaleImage(url: URL) = ImageIO.read(url).getScaledInstance(imageSize, imageSize, BufferedImage.SCALE_DEFAULT)

    private fun resourceImage(relativePath: String) = scaleImage(javaClass.classLoader.getResource(relativePath))

}