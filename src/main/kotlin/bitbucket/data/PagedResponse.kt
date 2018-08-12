package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PagedResponse<T>(
        @JsonProperty("start") val start: Int,
        @JsonProperty("size") val size: Int,
        @JsonProperty("limit") val limit: Int,
        @JsonProperty("isLastPage") val isLastPage: Boolean,
        @JsonProperty("nextPageStart") val nextPageStart: Int,
        @JsonProperty("values") val values: List<T>)
