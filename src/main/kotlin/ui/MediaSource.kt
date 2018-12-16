package ui

import java.net.URL
import java.util.concurrent.CompletableFuture

interface MediaSource<T> {
    fun retrieve(url: URL): CompletableFuture<T>
}