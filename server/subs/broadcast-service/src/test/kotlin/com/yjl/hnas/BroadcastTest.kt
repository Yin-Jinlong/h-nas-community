package com.yjl.hnas

import java.net.DatagramPacket
import java.net.MulticastSocket
import kotlin.test.Test

class BroadcastTest {

    @Test
    fun test() {
        val socket = MulticastSocket(Broadcast.port)
        Broadcast.joinGroups(socket)

        val buf = ByteArray(1024)
        val pack = DatagramPacket(buf, buf.size)
        socket.receive(pack)
        val str = String(pack.data, 0, pack.length)
        println(pack.address)
        println(str)
    }

}