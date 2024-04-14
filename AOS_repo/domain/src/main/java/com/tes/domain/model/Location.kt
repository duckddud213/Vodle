package com.tes.domain.model

typealias Lat = Double
typealias Lng = Double
typealias Location = Pair<Lat, Lng>

val Location.lat: Lat
    get() = this.first

val Location.lng: Lng
    get() = this.second
