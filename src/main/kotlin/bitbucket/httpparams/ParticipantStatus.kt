package bitbucket.httpparams

import bitbucket.data.HttpRequestParameter
import com.palominolabs.http.url.UrlBuilder

enum class ParticipantStatus: HttpRequestParameter {
    UNAPPROVED,
    NEEDS_WORK,
    APPROVED,
    ANY {
        override fun apply(urlBuilder: UrlBuilder) {
            //do not add any parameter
        }
    };

    override fun apply(urlBuilder: UrlBuilder) {
        urlBuilder.queryParam("participantStatus", name)
    }
}