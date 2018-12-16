package ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import java.util.concurrent.*
import javax.imageio.ImageIO

class ImagesSource: MediaSource<BufferedImage> {
    //The key is String, not java.net.URL, because URL's hashCode() and equals() are blocking operations
    private val cachedImages: MutableMap<String /* url */, CompletableFuture<BufferedImage>> = ConcurrentHashMap()
    private val executor: ExecutorService = AppExecutorUtil.getAppExecutorService()
    private val defaultAvatar = resourceImage("avatar.png")

    override fun retrieve(url: URL): CompletableFuture<BufferedImage> {
        return cachedImages.computeIfAbsent(url.toString()) {
            val future = CompletableFuture<BufferedImage>()
            executor.submit(GetImageTask(url, defaultAvatar, future))
            future
        }
    }

    private fun resourceImage(relativePath: String) =
            javaClass.classLoader.getResource(relativePath)

    class GetImageTask(
            private val url: URL,
            private val defaultUrl: URL,
            private val future: CompletableFuture<BufferedImage>
    ): Runnable {
        private val log = Logger.getInstance("GetImageTask")

        override fun run() {
            log.debug("Running GetImageTask for url: $url")
            try {
                future.complete(ImageIO.read(url))
            } catch (e: IOException) {
                log.warn("Cannot read image by URL: $url")
            }
            future.complete(ImageIO.read(defaultUrl))
        }
    }
}