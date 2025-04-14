package com.yjl.hnas

import com.google.gson.Gson
import com.yjl.hnas.data.APIInfo
import java.net.DatagramPacket
import java.net.MulticastSocket


fun main() {
    MulticastSocket().use {
        with(it) {
            init()
            run(
                APIInfo(
                    schema = "http",
                    port = 8888,
                    path = "/api"
                )
            )
        }
    }
}

fun MulticastSocket.init() {
    Broadcast.joinGroups(this)
}

fun MulticastSocket.run(api: APIInfo) {
    println("Broadcasting...")

    val data = Gson().toJson(api).encodeToByteArray()
    val packet = DatagramPacket(data, data.size, Broadcast.group)

    while (true) {
        send(packet)
        Thread.sleep(2000)
    }
}
