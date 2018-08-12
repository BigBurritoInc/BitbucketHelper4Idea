import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.vcsUtil.VcsUtil
import git4idea.GitVcs
import git4idea.branch.GitBranchUtil
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository


//todo: get rid of null-checks
object Git: VCS {

    override fun checkoutBranch(branch: String) {
        println("Checking out $branch")
        val currentProject = currentProject()
        val currentRepository = currentRepository()
        if (currentProject != null && currentRepository != null) {
            val branchController = GitBrancher.getInstance(currentProject)
            branchController.checkout(branch, false, listOf(currentRepository)) {/* empty callback */}
        } else {
            println("prj or repo is null $currentProject $currentRepository")
        }
    }

    override fun currentBranch(): String {
        val repository = currentRepository()
        if (repository != null)
            return GitBranchUtil.getDisplayableBranchText(repository)
        return ""
    }

    fun currentProject(): Project? {
        return CommonDataKeys.PROJECT.getData(DataManager.getInstance().dataContext)
    }

    fun currentRepository(): GitRepository? {
        val prj = currentProject()
        if (prj != null && gitVcs() != null)
            return GitBranchUtil.getCurrentRepository(prj)
        return null
    }

    fun gitVcs(): GitVcs? {
        val prj = currentProject()
        val baseDir = prj?.baseDir
        if (prj != null && baseDir != null) {
            val vcs = VcsUtil.getVcsFor(prj, baseDir)
            //Initial problem: git4idea.GitVcs cannot be cast to git4idea.GitVcs
            //Lessons learned: do not add runtime dependencies for a module if it is a plugin.
            //Use plugin.xml to describe them
            if (vcs is GitVcs)
                return vcs
        }
        return null
    }
}