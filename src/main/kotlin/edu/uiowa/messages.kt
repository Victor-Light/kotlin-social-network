package edu.uiowa

import com.google.gson.Gson
import java.io.File

// same reason as in the user.kt
fun convert_jsonto_Mesobj(jsonobj:ArrayList<String>):ArrayList<Message>{
    val result = ArrayList<Message>()
    val G = Gson()
    for (i in jsonobj){
        val userobj = G.fromJson(i,Message::class.java)
        result.add(userobj)
    }
    return result
}


//data in this module could be stored in the user data structure, but it would be a large data set, and it
//would be difficult to maintain, and fetch data. so I decide to dismantle the large data set into separate
//when user send emails through each other there might be lots of emails through each other,
//so in this case we need to use a separate class -- message class to store the data,
// it is easy for us to fetch the data we want. like which user id send which message
class Message{
    private val messageID:Int
    private val sendFrom:Int
    private val sendTo:Int
    private var messageContent:String

    constructor(from:User,to:User,content: String){
        messageID = Message.AllMessages.size
        Message.AllMessages.add(this)
        sendFrom = from.UserID()
        sendTo = to.UserID()
        messageContent = content

        val G = Gson()
        val json = G.toJson(this)
        Message.my_file.appendText(json+"\n")
    }
    companion object {
        val my_file = File("src/main/kotlin/messages.txt")
        val readjson = my_file.readText()
        val show =    readjson.split("{","}")
        val jsonobj = loadJson(show)
        var AllMessages = convert_jsonto_Mesobj(jsonobj)
        fun emptyMessage(){
            AllMessages = ArrayList()
        }
    }
    fun content():String{
        return messageContent
    }
    fun sendFromId():Int{
        return sendFrom
    }
    fun sendToId():Int{
        return sendTo
    }
    override fun toString(): String {
        return "From: "+this.sendFrom.toString()+" To: "+this.sendTo + " content: "+this.messageContent
    }

    override fun equals(other: Any?): Boolean {
        val otherMess = other as Message
        return (this.sendFrom==otherMess.sendFrom) && (this.sendTo == otherMess.sendTo) && (this.messageContent==otherMess.messageContent)
    }
}
