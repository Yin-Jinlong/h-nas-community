package io.github.yinjinlong.hnas

import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.MulticastSocket
import java.net.NetworkInterface
import kotlin.collections.iterator

object Broadcast {
    val port = 12000
    val groupAddress = Inet4Address.getByAddress(byteArrayOf(225.toByte(), 1, 2, 9))
    val group = InetSocketAddress(groupAddress, port)

    fun joinGroups(socket: MulticastSocket): Int {
        var count = 0
        for (i in NetworkInterface.getNetworkInterfaces()) {
            if (i.supportsMulticast() && i.isIPv4()) {
                println("Bind '${i.displayName}'")
                socket.joinGroup(group, i)
                count++
            }
        }
        return count
    }

    fun NetworkInterface.isIPv4(): Boolean {
        return interfaceAddresses.any { it.address is Inet4Address }
    }

}