package edu.uiowa

import com.beust.klaxon.*
import com.beust.klaxon.Klaxon
import com.beust.klaxon.json
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File
import com.hubspot.jinjava.*
import io.ktor.routing.post
import jdk.nashorn.internal.parser.JSONParser
import java.io.FileReader
import java.io.StringReader
import com.fasterxml.jackson.*
import io.ktor.content.file
import io.ktor.content.files
import io.ktor.content.static
import io.ktor.content.staticRootFolder
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.request.*
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.route
import kotlinx.html.I

//comments

//serverbody is the server that will be started when the main runs, when the server started it will reload the data from all
//txt files create all classes companion object ,and when data is modified the txt file will be rewritten.
//When user use web access routes, certain route will return certain page and data back to the browser.


object serverbody{
    val loginPage = File("src/main/resources/login.html").readText()
    val signUpPage = File("src/main/resources/registerpage.html").readText()
    val testpage = File("src/main/resources/index.html").readText()
    val contactPage = File("src/main/resources/email.html").readText()
    val profilePage = File("src/main/resources/profile.html").readText()
    val jinja = Jinjava()
    var map = mutableMapOf<String,Any>("pic_id" to 1)

    val app = embeddedServer(Netty,8080){

        install(DefaultHeaders)
        install(CallLogging)
        routing {
            //static used for finding static resources images
            static ("static"){
                staticRootFolder = File("src/main/resources/static")
                files("css")
                files("js")
                files("lib")
                files("pic")
                files("uploads")
                files("portrait")
            }
            //"/" redirect to "/index" route
            get("/"){
                call.respondRedirect("/index")
            }
            //"/image/prev/{pic_id} used for update pic_id in the global valuable map
            get("/image/prev/{pic_id}"){
                val pic_id = call.parameters.getAll("pic_id")?.first() ?: "1"
                map["pic_id"] = pic_id.toInt()-1
                call.respondRedirect("/index")
            }
            //"/image/next/{pic_id} used for update pic_id in the global valuable map
            get("/image/next/{pic_id}"){
                val pic_id = call.parameters.getAll("pic_id")?.first() ?: "1"
                map["pic_id"] = pic_id.toInt()+1
                call.respondRedirect("/index")
            }
            //"/login" return login page
            get("/login"){
                call.respondText(loginPage,ContentType("text","html"))
            }
            //"/register" return register page
            get("/register"){
                call.respondText(signUpPage,ContentType("text","html"))
            }
            //"/index" get current image's reply,likes and return index page with these data to browser
            get("/index"){
                var id_num = map["pic_id"] as Int
                if(id_num!! == 0){
                    map["pic_id"] = 1
                    id_num = 1
                }
                if(id_num!! == Picture.AllPicture.size+1){
                    map["pic_id"] = 1
                    id_num = 1
                }


                //portraits represent that who has liked current image
                //num_likes represent how many likes this image has
                val users_like_this = User.AllUsers.filter { id_num-1 in it.likeList() }
                val portraits = users_like_this.map { it.get_portrait() }
                map["portraits"] = portraits
                map["num_likes"] = portraits.size


                //comment is what being posted with, an image with one sentence
                map["comment"] = Picture.fetchPic(id_num as Int -1)!!.getComment()
                val curr_img_replies = reply.AllReplies.filter{it.getImagId()==id_num}


                //re is reply that other users gave to current image
                val re = curr_img_replies.map { User.find_user(it.getUserId()).user_name()+": ${it.getContent()}" }
                map["re"] = re


                call.respondText ( jinja.render(testpage, map),ContentType("text","html") )
            }
            //"/unlike" do the dislike action and modify data, then rewrite data into user.txt file
            get("/unlike"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }
                val current_user = map["user"] as User
                val img_id = map["pic_id"]as Int
                current_user.dislikeother(img_id-1)//because image starts at 1, but index is 0
                call.respondRedirect("/index")
            }
            //"/contact" fetch current user's received messages, and return email page with data to browser
            get("/contact"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }

                //ms are messages that current user received from others
                val current_user = map["user"] as User
                val curr_user_ms = Message.AllMessages.filter { it.sendToId()==current_user.UserID() }
                val ms = curr_user_ms.map{" From: "+User.find_user(it.sendFromId()).user_name() + ".  content: "+it.content()}
                map["ms"] = ms
                call.respondText ( jinja.render(contactPage, map),ContentType("text","html") )
            }
            //"/profile" return profile page
            get("/profile"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }
                val current_user = map["user"] as User
                map["myfavourite"] = current_user!!.likeList().map{it+1}
                call.respondText ( jinja.render(profilePage, map),ContentType("text","html") )

            }
            //"/like" do the like action and modify data , then rewrite data in to user.txt file
            get("/like"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }
                val current_user = map["user"] as User
                println("current user is $current_user")
                val img_id = map["pic_id"]as Int
                println("image id is $img_id")
                current_user.likeother(img_id-1)//because image starts at i, but index is 0
                call.respondRedirect("/index")
            }
            //"/register" get username and password then create new user object then add new data to user.txt file
            post("/register"){
                val params = call.receiveParameters()
                val user_name = params["username"]  //make sure parameters are not empty!!!
                val password = params["password"]
                if (user_name==null &&password==null){
                    call.respondText { "user name and password can not be empty" }
                }
                User(user_name!!,password!!)

                call.respondRedirect("/login")
            }
            //"/login" get username and password from browser then verify if there's a correct user login, if yes redirect to /index
            // if not return txt not valid login
            post("/login"){
                val params = call.receiveParameters()
                val user_name = params["username"]  //make sure parameters are not empty!!!
                val password = params["password"]
                if (user_name==null && password==null){
                    call.respondText { "user name and password can not be empty" }
                }

                if (User.validate_login(user_name!!,password!!)){
                    map["user"]=User.find_user_byname(user_name)!!
                    call.respondRedirect("/index")
                }
                call.respondRedirect("/login")
            }
            //"/email" get send to userid and content from the browser then create message object, then add data into message.txt
            post("/email"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }
                val current_user = map["user"] as User
                // send email function
                val params = call.receiveParameters()
                val email_content = params["content"]
                val user_id = params["userId"]
                if ((email_content == null)||(user_id==null)){
                    call.respondText { "userid and content must be typed in" }
                }
                current_user.send_message(User.find_user(user_id!!.toInt()),email_content!!)
                call.respondText { "you have successfully send email to ${User.find_user(user_id!!.toInt()).user_name()}" }

            }
            //"/reply" get reply content from browser and create reply object then store data into reply.txt
            post("/reply"){
                if (map["user"]==null){
                    call.respondText { "Please login first" }
                }
                val current_user = map["user"] as User
                val params = call.receiveParameters()
                val reply = params["reply"]
                val imgid = map["pic_id"] as Int
                reply(current_user.UserID(),imgid!!,reply!!)

                call.respondRedirect("/index")
            }
            //"/upload" get uploaded image and comment about this image, create the picture object store data into static/uploads
            post("/upload"){
                if (map["user"]==null){
                    call.respondText { "please login first" }
                }
                val curr_user = map["user"] as User
                val multipart = call.receiveMultipart()
                    val f = uploadserver()
                    f.filesave(multipart,curr_user.UserID())
                call.respondRedirect("/index")
            }
            //"/upload_portrait" ger uploaded image modify user.portrait then rewrite data into user.txt file
            post("/upload_portrait"){
                if (map["user"]==null){
                    call.respondText { "please login first" }
                }
                val multipart = call.receiveMultipart()
                val f = uploadportrait()
                val curr_user = map["user"] as User
                val portraitname = f.filesave(multipart,curr_user.UserID())
                curr_user.change_portrait(portraitname)

                call.respondRedirect("/index")
            }
        }
    }
}


fun main(args: Array<String>) {
    serverbody.app.start(wait=true)
}
