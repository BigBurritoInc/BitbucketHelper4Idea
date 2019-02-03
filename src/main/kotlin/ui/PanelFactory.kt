package ui

import bitbucket.data.PR
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.components.JBScrollPane
import java.util.concurrent.Executor
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

var imagesSource: MediaSource<Icon> = ImagesSource()
var awtExecutor: Executor = Executor { command -> ApplicationManager.getApplication().invokeLater(command) }

fun createReviewPanel(): Panel {
    return object : Panel() {
        override fun createPRComponent(pr: PR): PRComponent {
            return PRComponent(pr, imagesSource, awtExecutor)
        }

        override fun ownUpdated(diff: Diff) {}

        override fun reviewedUpdated(diff: Diff) {
            dataUpdated(diff)
        }
    }
}

fun createOwnPanel(): Panel {
    return object : Panel() {
        override fun createPRComponent(pr: PR): PRComponent {
            return OwnPRComponent(pr, imagesSource, awtExecutor)
        }

        override fun ownUpdated(diff: Diff) {
            dataUpdated(diff)
        }

        override fun reviewedUpdated(diff: Diff) {}
    }
}

fun wrapIntoJBScroll(panel: JPanel): JScrollPane {
    val scroll = JBScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
    scroll.verticalScrollBar.unitIncrement = 14
    return scroll
}