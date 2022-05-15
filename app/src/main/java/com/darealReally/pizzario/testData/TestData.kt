package com.darealReally.pizzario.testData

import androidx.compose.ui.geometry.Size
import com.darealReally.pizzario.data.AnimationProps
import com.darealReally.pizzario.data.Location
import com.darealReally.pizzario.data.Status
import com.google.android.gms.maps.model.LatLng

val locations = listOf(
    Location(
        branchName = "West",
        address1 = "Emerald Street",
        address2 = "#02-02",
        distance = 3,
        status = Status.Open,
        seatsPercentage = 0.2F,
        coordinates = LatLng(40.78642, -74.07714),
        animationProps = AnimationProps()
    ),
    Location(
        branchName = "North",
        address1 = "Holland Road",
        address2 = "#01-12",
        distance = 9,
        status = Status.Open,
        seatsPercentage = 0.6F,
        coordinates = LatLng(40.79421, -74.06325),
        animationProps = AnimationProps(
            delay = 2,
            degreesToMove = 15F,
            pizzaSize = Size(100F, 105F)
        )
    ),
    Location(
        branchName = "South",
        address1 = "Beach Road",
        address2 = "#01-11",
        distance = 23,
        status = Status.Close,
        coordinates = LatLng(40.78039, -74.07009)
    ),
    Location(
        branchName = "East",
        address1 = "Emerald Street",
        address2 = "#08-02",
        distance = 32,
        status = Status.Open,
        seatsPercentage = 0.2F,
        coordinates = LatLng(40.78360, -74.06138),
        animationProps = AnimationProps(
            delay = 2,
            degreesToMove = 15F,
            pizzaSize = Size(100F, 105F)
        )
    ),
    Location(
        branchName = "North South",
        address1 = "Flower Street",
        address2 = "#05-23",
        distance = 50,
        status = Status.Open,
        seatsPercentage = 0.6F,
        coordinates = LatLng(40.78642, -74.07714)
    )
)