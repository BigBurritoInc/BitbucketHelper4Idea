package http

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient



class HttpAuthClient {
    fun getClient() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
                AuthScope("httpbin.org", 80),
                UsernamePasswordCredentials("user", "passwd"))
        val httpclient = HttpAsyncClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build()
        try {
            httpclient.start()
            val httpget = HttpGet("http://httpbin.org/basic-auth/user/passwd")
            System.out.println("Executing request " + httpget.getRequestLine())
            val future = httpclient.execute(httpget, null)
            val response = future.get()
            System.out.println("Response: " + response.statusLine)
            println("Shutting down")
        } finally {
            httpclient.close()
        }
    }
}