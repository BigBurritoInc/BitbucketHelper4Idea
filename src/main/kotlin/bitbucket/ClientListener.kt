package bitbucket

interface ClientListener {
    fun invalidCredentials() {}
    fun actionForbidden() {}
    fun requestFailed(e: Exception) {}
}