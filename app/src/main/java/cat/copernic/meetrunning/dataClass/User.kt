package cat.copernic.meetrunning.dataClass

data class User(var username : String ?= null,
                var distance : Int ?= null,
                var time: Long? = 0,
                var description: String? = "",
                var email : String ?= null,
)/*,
                var image: ImageView ?= null*/