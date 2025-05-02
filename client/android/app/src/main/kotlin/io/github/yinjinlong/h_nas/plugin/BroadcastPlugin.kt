package io.github.yinjinlong.h_nas.plugin

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.github.yinjinlong.h_nas.Broadcast
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.MulticastSocket
import java.net.SocketTimeoutException

class BroadcastPlugin : MethodChannel.MethodCallHandler {

    companion object {
        const val NAME = "broadcast_plugin"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onMethodCall(
        call: MethodCall,
        result: MethodChannel.Result
    ) {
        when (call.method) {
            "receiveBroadcast" -> {
                GlobalScope.launch {
                    result.success(receiveBroadcast())
                }
            }

            else -> result.notImplemented()
        }
    }

    private suspend fun receiveBroadcast(): String {
        val socket = withContext(Dispatchers.IO) {
            MulticastSocket(Broadcast.port)
        }
        Broadcast.joinGroups(socket)
        socket.soTimeout = 5000
        val buf = ByteArray(1024)
        val pack = DatagramPacket(buf, buf.size)
        try {
            withContext(Dispatchers.IO) {
                socket.receive(pack)
            }
            val str = String(pack.data, 0, pack.length)
            return "${pack.address}\n$str"
        } catch (_: SocketTimeoutException) {
        }
        return ""
    }
}