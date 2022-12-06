package be.senne.broadcast_demo

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.lifecycle.ViewModel
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException


class MainViewModel : ViewModel() {
    var devices = mutableListOf<Device>()



    init {
    }

    fun start() {

    }

    fun stop() {

    }

    fun ping() {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val broadcastAddr = InetAddress.getByName("192.168.83.255")
        val bindPort = 14581
        val socket = DatagramSocket(bindPort)
        socket.broadcast = true
        val bytes = byteArrayOf(0x10, 0x11, 0x12, 0x13, 0x70, 0x69, 0x7A, 0x7A, 0x61, 0x00)

        val sendPacket = DatagramPacket(bytes, bytes.size, broadcastAddr, bindPort)
        socket.soTimeout = 200

        while(true) {
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
    }
}