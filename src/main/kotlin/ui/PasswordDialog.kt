package ui

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPasswordField

/**
 * @author Dmitrii Kopylov
 * @since 01.09.2018
 */
class PasswordDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    val passwordField = JPasswordField()
    init {
        title = "Enter Bitbucket's password"
        init()
    }
    override fun createCenterPanel(): JComponent? {
        return passwordField
    }

    fun getPassword(): CharArray {
        return passwordField.password
    }
}