import bitbucket.createClient
import rx.Observable

object Runner {
    @JvmStatic fun main(args: Array<String>) {
        val client = createClient()
        client.requestReviewedPRs()
                .doOnError { println("Error: $it") }
                .flatMap { page -> Observable.from(page.values)}
                .forEach { println("ReviewingPR: $it") }

        client.requestOwnPRs()
                .doOnError { println("Error: $it") }
                .flatMap { page -> Observable.from(page.values) }
                .forEach { println("OwnPR: $it") }
    }
}