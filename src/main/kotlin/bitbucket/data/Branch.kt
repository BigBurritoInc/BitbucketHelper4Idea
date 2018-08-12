package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Branch(@JsonProperty("id") val name:String)
