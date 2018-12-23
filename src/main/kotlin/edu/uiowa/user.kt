package edu.uiowa
import com.google.gson.Gson
import java.io.File
import java.io.PrintWriter


//comment:
//UserInterface Interface is the only interface that user class implements, it has all the functions that
//user object can user. it is used for telling people which function they can use

interface UserInterface{
    fun myPictures():List<Picture>
    fun post(url:String,comment:String)
    fun likeother(other: Int)
    fun dislikeother(other: Int)
    fun likeList():ArrayList<Int>         //pictures'id that user likes
    fun receivedLikes():ArrayList<Int>    //likes that User received UserID
    fun remainLikes():Int                  //number of likes that User has
    fun UserID():Int
    fun message_I_send():List<Message>
    fun message_I_received():List<Message>
    fun send_message(To:User,content: String)
    fun user_name():String
    fun change_portrait(s:String)
    fun get_portrait():String
}


//every time we start the program, we need to reload the data into our companion object, so we need
//we need a function to read a sting object then convert into json object. loadJson is the function
fun loadJson(show:List<String>):ArrayList<String>{
    val jsonobj = ArrayList<String>()
    for(i in show){
        if(i.length>2){
            val json = "{"+i+"}"
            jsonobj.add(json)
        }
    }
    return jsonobj
}

//after we have the json object, we have to convert it into user object, then store it into companion object
//that is the key to keep data updated
fun convert_jsonto_obj(jsonobj:ArrayList<String>):ArrayList<User>{
    val result = ArrayList<User>()
    val G = Gson()
    for (i in jsonobj){
        val userobj = G.fromJson(i,User::class.java)
        result.add(userobj)
    }
    return result
}


//when people browse in the browser, all the actions he/she does need to be recorded. so we need a User object
// to represent the one who's using my program. And all the action's he/she does will be recorded into companion
//object, then stored into user.txt file. Every time program starts, data will be read into json object, then
// convert into user object and stored into companion object.
//I used getter style to build the user data structure is to avoid data modified by accident
class User:UserInterface{
    private val id:Int
    private var likeList:ArrayList<Int>
    private var remainLike:Int
    private var receivedlikeList:ArrayList<Int>
    private var user_name:String
    private var password:String
    private var portrait:String

    constructor(name:String,password:String){                     //create a User's id according to the AllUser list incrementing
        user_name = name
        this.password = password
        id = User.AllUsers.size
        User.AllUsers.add(this)
        likeList = ArrayList()
        receivedlikeList = ArrayList()
        remainLike = 5
        portrait = "default.png"

        val G = Gson()
        val json = G.toJson(this)
        my_file.appendText(json+"\n")

    }
    companion object {
        //load user.txt file
        val my_file = File("src/main/kotlin/users.txt")
        val readjson = my_file.readText()
        val show =    readjson.split("{","}")
        val jsonobj = loadJson(show)
        var AllUsers = convert_jsonto_obj(jsonobj)
        fun emptyUser(){
            AllUsers = ArrayList()
        }
        fun find_user(id:Int):User{
            return User.AllUsers[id]
        }
        fun find_user_byname(name:String):User?{
            for (i in User.AllUsers){
                if (i.user_name()==name){
                    return i
                }
            }
            return null
        }
        fun validate_login(name: String,pas: String):Boolean{
            val u = User.find_user_byname(name)
            if (u?.password==pas){
                return true
            }
            return false
        }
    }
    //get username
    override fun user_name(): String {
        return this.user_name
    }
    //do the  post action, create picture object
    override fun post(url: String,comment:String) {
        Picture(this.id,url,comment)

    }
    //return user's portrait file name
    override fun get_portrait(): String {
        return this.portrait
    }
    // return all the images that current user has posted
    override fun myPictures(): List<Picture> {
        return Picture.AllPicture.filter { it.getUserID()==this.UserID() }
    }
    //modify user's portrait file name and rewrite the data into user.txt
    override fun change_portrait(other:String) {
        this.portrait = other
        val writer = PrintWriter(User.my_file)
        writer.print("")
        writer.close()//claer the user.txt and rewrite it

        val G = Gson()
        for(i in User.AllUsers){
            val json = G.toJson(i)
            User.my_file.appendText(json+"\n")
        }
    }
    //do the like function, decrease current user's remain likes by one, add that image id into user's likelist
    //and the image's owner will increase one more remainlikes
    //, if user run out of remain likes, keep remain likes as 0, remove first one in his likelist add new
    //image id. after modified data rewrite data into user.txt
    override fun likeother(other:Int) {
        val other_img_obj = Picture.fetchPic(other)
        if (this.remainLikes()==0){
            this.likeList.removeAt(0)
            this.likeList.add(other_img_obj!!.getPicID())
            User.find_user(other_img_obj!!.getUserID()).receivedlikeList.add(this.UserID())
            User.find_user(other_img_obj!!.getUserID()).addLike()
        }
        else if (other_img_obj!!.getPicID() in this.likeList){
            println("you can't like same one twice")
        }
        else {
            this.likeList.add(other_img_obj!!.getPicID())
            User.find_user(other_img_obj!!.getUserID()).receivedlikeList.add(this.UserID())
            this.deleteLike()
            User.find_user(other_img_obj!!.getUserID()).addLike()
        }
        val writer = PrintWriter(User.my_file)
        writer.print("")
        writer.close()//claer the user.txt and rewrite it

        val G = Gson()
        for(i in User.AllUsers){
            val json = G.toJson(i)
            User.my_file.appendText(json+"\n")
        }


    }
    // return user's liked images
    override fun likeList(): ArrayList<Int> {
        return this.likeList
    }
    //when user dislike a image, the image owner will lose one like from remain likes if he has more than 5 remainlikes
    //after modified data , it will be rewrite into user.txt
    override fun dislikeother(other: Int) {
        val other_img_obj = Picture.fetchPic(other)
        val dislikedUser = User.find_user(other_img_obj!!.getUserID())
        if (dislikedUser.likeList().size > 5){
            dislikedUser.dislikeother(Picture.AllPicture[dislikedUser.likeList()[0]].getPicID())
            dislikedUser.likeList().removeAt(0)
            dislikedUser.deleteLike()
            dislikedUser.receivedLikes().remove(this.UserID())
        }
        val writer = PrintWriter(User.my_file)
        writer.print("")
        writer.close()//claer the user.txt and rewrite it

        val G = Gson()
        for(i in User.AllUsers){
            val json = G.toJson(i)
            User.my_file.appendText(json+"\n")
        }
    }
    //return all the likes that current user received
    override fun receivedLikes(): ArrayList<Int> {
        return receivedlikeList
    }
    //return the remain likes he has
    override fun remainLikes(): Int {
        return this.remainLike
    }
    //return userId
    override fun UserID(): Int {
        return this.id
    }
//fetch all the message current user has sent
    override fun message_I_send(): List<Message> {
        return Message.AllMessages.filter { it.sendFromId()==this.UserID() }
    }
// fetch all the messages current user has received
    override fun message_I_received(): List<Message> {
        return Message.AllMessages.filter { it.sendToId()==this.UserID() }
    }
// send message action
    override fun send_message(To: User, content: String) {
        Message(this,To,content)
    }


//private functions used for getter usage

    private fun addLike(){
        this.remainLike +=1
    }
    private fun deleteLike(){
        this.remainLike -=1
    }

    override fun toString(): String {
        return "'ID = "+this.id.toString() +" user_name = "+this.user_name+"'"
    }
}

