package ui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.concurrency.AppExecutorUtil
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import java.util.concurrent.*
import java.util.function.Supplier
import javax.imageio.ImageIO

class ImagesSource: MediaSource<BufferedImage> {
    private val log = Logger.getInstance("ImagesSource")

    //The key is String, not java.net.URL, because URL's hashCode() and equals() are blocking operations
    private val cachedImages: MutableMap<String /* url */, CompletableFuture<BufferedImage>> = ConcurrentHashMap()
    private val executor: ExecutorService = AppExecutorUtil.getAppExecutorService()
    private val defaultAvatar = resourceImage("avatar.png")

    override fun retrieve(url: URL): CompletableFuture<BufferedImage> {
        return cachedImages.computeIfAbsent(url.toString()) {
            CompletableFuture.supplyAsync<BufferedImage>(Supplier {
                try {
                    ImageIO.read(url)
                } catch (e: IOException) {
                    log.warn("Cannot read image by URL: $url")
                    ImageIO.read(defaultAvatar)
                }
            }, executor)
        }
    }

    private fun resourceImage(relativePath: String) =
            javaClass.classLoader.getResource(relativePath)
}