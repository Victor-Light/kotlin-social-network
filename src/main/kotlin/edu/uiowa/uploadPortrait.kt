package edu.uiowa

import io.ktor.request.MultiPartData
import io.ktor.request.PartData
import java.io.FileOutputStream
import java.io.InputStream
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.*
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.toMap
import java.io.*
// uploadportrait is used for handling user upload portrait then store the image into static/portrait
class uploadportrait {
    private fun readstream(R: InputStream): ArrayList<Byte> {
        val buffer = ArrayList<Byte>() // ArrayList is mutable
        while (true) { // read each byte of stream, accumulate bytes
            val b = R.read()
            if (b < 0) break
            buffer.add(b.toByte())
        }
        return buffer
    }

    suspend fun filesave(p: MultiPartData,userId:Int): String {
        var filecontent = ArrayList<Byte>() // will be file's content
        var filename = "unknown" // will be assigned file name we hope
        var comment_value = ""
        while (true) {
            val part = p.readPart() ?: break
            when (part) {
                is PartData.FormItem -> {
                    comment_value = part.value
                    println("FormItem: ${part.partName} = ${part.value}")
                }
                is PartData.FileItem -> {
                    println("FileItem: ${part.partName} -> ${part.originalFileName} of ${part.contentType}")
                    val contentStream = part.streamProvider()
                    filecontent = readstream(contentStream)
                    filename = "user"+userId.toString()+".png"
                }
            }
            part.dispose()
        }
        // now we can write the file
        if(filecontent.size>50) {
            val filetowrite = FileOutputStream("src/main/resources/static/portrait/$filename")
            filetowrite.write(filecontent.toByteArray())
            filetowrite.close()
        }
        return filename
    }
}
