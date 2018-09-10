package ui

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.*
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import java.awt.BorderLayout
import javax.swing.*

import java.awt.*
import com.intellij.util.xmlb.XmlSerializerUtil


/**
 * todo refactor
 * @author Dmitrii Kopylov
 * @since 25.08.2018
 */
class BitbucketHelperConfigurable : SearchableConfigurable, Configurable.NoScroll {

    var settings: Settings = Settings()

    override fun isModified(): Boolean {
        return projectField.text != settings.project ||
                slugField.text != settings.slug ||
                urlField.text != settings.url ||
                loginField.text != settings.login
    }

    override fun getId(): String {
        return "preferences.BitbucketHelper4Idea"
    }

    override fun getDisplayName(): String {
        return "BitbucketHelper4Idea"
    }

    override fun apply() {
        settings.project = projectField.text
        settings.slug = slugField.text
        settings.url = urlField.text
        settings.login = loginField.text
        UpdateTaskHolder.reschedule()
    }

    private val projectField = JTextField()
    private val slugField = JTextField()
    private val urlField = JTextField()
    private val loginField = JTextField()

    override fun createComponent(): JComponent? {
        val project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()) ?: return JLabel("Empty project!")
        val storer = ServiceManager.getService<Storer>(project, Storer::class.java);
        if (storer != null)
            settings = storer.settings

        val mainPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.FIRST_LINE_START
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.insets = Insets(0, 0, 0, 10)
        gbc.fill = GridBagConstraints.CENTER

        mainPanel.add(JLabel("Project"), gbc)
        gbc.gridy++
        mainPanel.add(JLabel("Slug"), gbc)
        gbc.gridy++
        mainPanel.add(JLabel("Bitbucket URL"), gbc)
        gbc.gridy++
        mainPanel.add(JLabel("Login"), gbc)

        gbc.gridy = 0
        gbc.gridx = 1
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        mainPanel.add(projectField, gbc)
        gbc.gridy++
        mainPanel.add(slugField, gbc)
        gbc.gridy++
        mainPanel.add(urlField, gbc)
        gbc.gridy++
        mainPanel.add(loginField, gbc)

        val wrapper = JPanel(BorderLayout())
        wrapper.add(mainPanel, BorderLayout.NORTH)
        return wrapper
    }

    override fun reset() {
        projectField.text = settings.project
        slugField.text = settings.slug
        urlField.text = settings.url
        loginField.text = settings.login
    }
}

data class Settings(var project: String = "", var slug: String = "", var login: String = "", var url: String = "")

@State(name = "BitbucketHelper4Idea", storages = arrayOf(Storage(StoragePathMacros.WORKSPACE_FILE)))
class Storer : PersistentStateComponent<Settings> {
    val settings:Settings = Settings()

    override fun getState(): Settings? {
        return settings
    }

    override fun loadState(state: Settings) {
        XmlSerializerUtil.copyBean(state, settings);
    }
}