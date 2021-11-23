package cat.copernic.meetrunning.home

import android.os.Parcel
import android.os.Parcelable

data class PostHome(
    var title: String? = "",
    var description: String? = "",
    var route: MutableList<LatLng> = ArrayList(),
    var user: String? = "",
    var city: String? = "",
    var distance: Double = 0.0,
    var time: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        TODO("route"),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(user)
        parcel.writeString(city)
        parcel.writeDouble(distance)
        parcel.writeInt(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostHome> {
        override fun createFromParcel(parcel: Parcel): PostHome {
            return PostHome(parcel)
        }

        override fun newArray(size: Int): Array<PostHome?> {
            return arrayOfNulls(size)
        }
    }
}
//var image: ImageView ?= null*/)
