package be.senne.broadcast_demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket

class MainViewModel : ViewModel() {
    var devices = mutableListOf<Device>()

    private var job : Job?
    private var sock = lazy {
        val s = DatagramSocket()
        s.broadcast = true
        s.bind()
        s
    }

    init {
        job = null

        val device1 = Device("Device 1", 12)
        val device2 = Device("Device 2", 3465)
        val device3 = Device("Device 3", 68284572)
        val device4 = Device("Device 4", 19234753923358)

        devices.addAll(listOf(device1, device2, device3, device4))
    }

    fun start() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val actualMagicArray = byteArrayOf(0x31, 0x56, 0x7F, 0x14)
            var magicBuffer = ByteArray(4)
            while(isActive) {
                val receivedPacket : DatagramPacket = DatagramPacket(magicBuffer, 4)
                sock.value.receive(receivedPacket)

                if(magicBuffer.contentEquals(actualMagicArray)) {
                    val bytes = "device 1;12".encodeToByteArray()
                    val datagramPacket = DatagramPacket(bytes, bytes.size)
                    sock.value.send(datagramPacket)
                    delay(2000)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun ping() {
        val actualMagicArray = byteArrayOf(0x31, 0x56, 0x7F, 0x14)
        val sendPacket : DatagramPacket = DatagramPacket(actualMagicArray, 4)
        sock.value.send(sendPacket)

        val devs = ArrayList<Device>()

        val startTime = System.currentTimeMillis()

        while(System.currentTimeMillis() - startTime >= 2000) {
            val receivedData = ByteArray(4)
            val receivedPacket = DatagramPacket(receivedData, 4)
            sock.value.receive(receivedPacket)

            val device = Device(receivedData[0].toString(), receivedData[1].toLong())
            devs.add(device)
        }
        devices = devs
    }
}