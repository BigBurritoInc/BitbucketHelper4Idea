import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

class IconProvider: FileIconProvider {
    override fun getIcon(file: VirtualFile, flags : Int, prj: Project?): Icon? {
        return IconLoader.getIcon("alien.png")
    }
}