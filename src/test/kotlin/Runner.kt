import bitbucket.BitbucketClientFactory
import rx.Observable

object Runner {
    @JvmStatic fun main(args: Array<String>) {
        val client = BitbucketClientFactory.createClient()
        val r = PanelRunner
    //    client.approve(r.createPR(7255))
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