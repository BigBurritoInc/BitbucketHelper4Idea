package bitbucket

interface ClientListener {
    fun invalidCredentials() {}
    fun requestFailed(e: Exception) {}
}