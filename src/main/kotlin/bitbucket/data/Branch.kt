package bitbucket.data

import com.fasterxml.jackson.annotation.JsonProperty

data class Branch(@JsonProperty("displayId") val name:String)
