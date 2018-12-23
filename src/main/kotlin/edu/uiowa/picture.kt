package edu.uiowa

import com.google.gson.Gson
import java.io.File

//pictureInterface is the only interface picture class has, so the purpose is telling other module what function we can use
interface PictureInterface{
    fun getPicID():Int
    fun getUserID():Int
    fun getComment():String
}
//same reason as in user.kt
fun convert_jsonto_Picobj(jsonobj:ArrayList<String>):ArrayList<Picture>{
    val result = ArrayList<Picture>()
    val G = Gson()
    for (i in jsonobj){
        val userobj = G.fromJson(i,Picture::class.java)
        result.add(userobj)
    }
    return result
}

//data in this module could be stored in the user data structure, but it would be a large data set, and it
//would be difficult to maintain, and fetch data. so I decide to dismantle the large data set into separate
//the reason why I create a separate picture class is that one user can post many images, if I store it ito user data structure
//the user data would be huge, and different to maintain. In this case, when I want certain data, all I need to do is filter
//it and return the certain data I want
class Picture:PictureInterface{
    private val PicID:Int
    private val UserID:Int
    private val Pic:String
    private val comment:String
    constructor(User_id:Int,filename: String,com:String){
        PicID = Picture.AllPicture.size
        Picture.addPicture(this)
        UserID = User_id
        Pic = filename
        comment = com

        val G = Gson()
        val json = G.toJson(this)
        Picture.my_file.appendText(json+"\n")
    }
    companion object {
        val my_file = File("src/main/kotlin/pictures.txt")
        val readjson = my_file.readText()
        val show =    readjson.split("{","}")
        val jsonobj = loadJson(show)
        var AllPicture = convert_jsonto_Picobj(jsonobj)
        fun addPicture(i: Picture) {
            Picture.AllPicture.add(i)
        }

        fun fetchPic(id: Int):Picture?{
            if(id in 0 until Picture.AllPicture.size){
                return Picture.AllPicture[id]
            }
            return null
        }
    }

    override fun getComment(): String {
        return this.comment
    }

    override fun getPicID(): Int {
        return PicID
    }

    override fun getUserID(): Int {
        return UserID
    }

    override fun toString(): String {
        return this.Pic
    }
}

