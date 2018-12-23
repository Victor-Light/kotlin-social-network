package edu.uiowa

import com.google.gson.Gson
import java.io.File

// tell other modules what function can be used in this reply module
interface replyInterface{
    fun getUserId():Int
    fun getImagId():Int
    fun getReplyId():Int
    fun getContent():String
}

// same reason in user.kt
fun convert_jsonto_replyobj(jsonobj:ArrayList<String>):ArrayList<reply>{
    val result = ArrayList<reply>()
    val G = Gson()
    for (i in jsonobj){
        val userobj = G.fromJson(i,reply::class.java)
        result.add(userobj)
    }
    return result
}

//data in this module could be stored in the user data structure, but it would be a large data set, and it
//would be difficult to maintain, and fetch data. so I decide to dismantle the large data set into separate
//when user reply to a image, we need a object to record all the replies
// so that we can display all the replies
//current image has

class reply:replyInterface{
    private val replyId:Int
    private val userId:Int
    private val imgId:Int
    private val content:String
    constructor(uid:Int,iid:Int,content:String){
        replyId = reply.AllReplies.size
        userId = uid
        imgId = iid
        this.content = content

        reply.AllReplies.add(this)

        val G = Gson()
        val json = G.toJson(this)
        reply.my_file.appendText(json+"\n")
    }

    companion object {
        val my_file = File("src/main/kotlin/reply.txt")
        val readjson = my_file.readText()
        val show =    readjson.split("{","}")
        val jsonobj = loadJson(show)
        var AllReplies = convert_jsonto_replyobj(jsonobj)
    }
    override fun getContent(): String {
        return this.content
    }

    override fun getImagId(): Int {
        return this.imgId
    }

    override fun getReplyId(): Int {
        return this.replyId
    }

    override fun getUserId(): Int {
        return this.userId
    }
}

fun main(args: Array<String>) {
    reply(1,1,"nice picture")
    reply(2,1,"nice picture")
    reply(1,2,"nice picture")
}