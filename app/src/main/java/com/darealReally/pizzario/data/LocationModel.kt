package com.darealReally.pizzario.data

import androidx.compose.ui.geometry.Size
import com.google.android.gms.maps.model.LatLng

enum class Status {
    Open,
    Close
}

data class Location(
    var branchName: String = "Branch Name",
    var address1: String = "Street Name",
    var address2: String = "Unit No.",
    var distance: Int = 0,
    var status: Status = Status.Open,
    var seatsPercentage: Float = 0F,
    var coordinates: LatLng = LatLng(
        40.764051,
        -74.075571
    ),
    var animationProps: AnimationProps = AnimationProps()
)

data class AnimationProps(
    var delay: Int = 1,
    var degreesToMove: Float = -15F,
    var pizzaSize: Size = Size(120F, 125F)
)