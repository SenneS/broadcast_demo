package be.senne.broadcast_demo

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException


class MainViewModel : ViewModel() {
    var devices = mutableListOf<Device>()

    private var job : Job? = null

    fun start() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val broadcastAddr = InetAddress.getByName("192.168.83.255")
            val bindPort = 14581
            val socket = DatagramSocket(bindPort)

            val bytes = "Hallo mensen.".encodeToByteArray()

            val sendPacket = DatagramPacket(bytes, bytes.size, broadcastAddr, bindPort)

            while (isActive) {
                    val startTime = System.currentTimeMillis()
                    socket.broadcast = true
                    socket.send(sendPacket)
                    while ((System.currentTimeMillis() - startTime) <= 5000) {
                        socket.broadcast = false
                        val recvBytes = ByteArray(512)
                        val receivePacket = DatagramPacket(recvBytes, recvBytes.size)
                        try {
                            socket.receive(receivePacket)
                            println("received packet from ${receivePacket.address}")
                        } catch (_: SocketTimeoutException){
                        }
                    }
            }
            socket.close()
        }
    }

    fun stop() {
        job?.cancel()
    }
}