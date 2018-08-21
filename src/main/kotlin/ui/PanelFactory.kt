package ui

import com.intellij.ui.components.JBScrollPane
import javax.swing.JPanel
import javax.swing.JScrollPane

fun createReviewPanel(): Panel {
    return object : Panel() {
        override fun ownUpdated(diff: Diff) {}

        override fun reviewedUpdated(diff: Diff) {
            dataUpdated(diff)
        }
    }
}

fun createOwnPanel(): Panel {
    return object : Panel() {
        override fun ownUpdated(diff: Diff) {
            dataUpdated(diff)
        }

        override fun reviewedUpdated(diff: Diff) {}
    }
}

fun wrapIntoJBScroll(panel: JPanel): JScrollPane {
    val scroll = JBScrollPane(panel)
    scroll.verticalScrollBar.unitIncrement = 14
    return scroll
}


fun wrapIntoScroll(panel: JPanel): JScrollPane {
    val scroll = JScrollPane(panel)
    scroll.verticalScrollBar.unitIncrement = 14
    return scroll
}