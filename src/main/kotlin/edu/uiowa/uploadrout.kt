package edu.uiowa

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

//comment
//
//class uploadserver is used for handling user post their images
//all functions are copied from professor example
//upload image will be stored into static/uploads file

class uploadserver {
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
                    filename = "img"+(Picture.AllPicture.size+1).toString()+".png"
                }
            }
            part.dispose()
        }
        // now we can write the file
        if(filecontent.size>1024) {
            val filetowrite = FileOutputStream("src/main/resources/static/uploads/$filename")
            filetowrite.write(filecontent.toByteArray())
            filetowrite.close()
            Picture(userId, filename, comment_value)
        }
        return "uploaded: ${filecontent.size} bytes to $filename"
    }
}
