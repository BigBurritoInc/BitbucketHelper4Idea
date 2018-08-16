package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

class Links(@JsonProperty("self") val self: List<String>)