//package edu.uiowa
//
//import org.junit.Test
//import org.junit.Assert.*
//
//class unitTest {
//
//    @Test
//    fun testEngineBrowsing(){
//        val victor = User("victor")
//        val grace = User("grace")
//        val tom = User("tom")
//        val nike = User("nike")
//        victor.post("victor picture")
//        grace.post("grace picture")
//        tom.post("tom picture")
//        nike.post("nike picture")
//
//        val engine = Engine()
//        val first_picture = engine.firstPicture()
//        val second_picture = engine.nextPicture(first_picture!!)
//        val third_picture = engine.nextPicture(second_picture!!)
//        val forth_picture = engine.nextPicture(third_picture!!)
//
//        assertEquals(null,engine.prevPicture(first_picture!!))
//        assertEquals(null,engine.nextPicture(forth_picture!!))
//        assertEquals(second_picture,engine.nextPicture(first_picture!!))
//        assertEquals(third_picture,engine.nextPicture(second_picture!!))
//        assertEquals(third_picture,engine.prevPicture(forth_picture!!))
//        assertEquals(second_picture,engine.fetch(1))
//        assertEquals(third_picture,engine.fetch(2))
//
//        //empty the engine
//        engine.emptyEngine()
//    }
//
//    @Test
//    fun testUser1() {                            //test method User.likeother(), User.remainLikes(), User.likeList()
//        val victor = User("victor")
//        val grace = User("grace")
//        val tom = User("tom")
//        val nike = User("nike")
//        val max = User("max")
//        victor.post("victor picture")
//        grace.post("grace picture")
//        tom.post("tom picture")
//        nike.post("nike picture")
//        max.post("max picture")
//        val engine = Engine()
//        val first_picture = engine.firstPicture()
//        val second_picture = engine.nextPicture(first_picture!!)
//        val third_picture = engine.nextPicture(second_picture!!)
//        victor.likeother(second_picture!!)
//        victor.likeother(third_picture!!)
//
//        val testList = ArrayList<Picture>()
//        testList.add(grace.myPictures()[0])
//        testList.add(tom.myPictures()[0])
//        assertEquals(testList,victor.likeList())
//        assertEquals(3,victor.remainLikes())
//        assertEquals(6,grace.remainLikes())
//        println(victor.profile())
//
//        //empty the engine
//        engine.emptyEngine()
//        }
//    @Test
//    fun testUser2() {                           //test method User.dislikeother()
//        val victor = User("victor")
//        val grace = User("grace")
//        val tom = User("tom")
//        val nike = User("nike")
//        victor.post("victor picture")
//        grace.post("grace picture")
//        tom.post("tom picture")
//        nike.post("nike picture")
//        nike.post("nike second pic")
//        nike.post("nike third pic")
//        nike.post("nike forth pic")
//
//        val engine = Engine()
//        nike.likeother(engine.fetch(0)!!)
//        victor.likeother(engine.fetch(1)!!)
//        victor.likeother(engine.fetch(2)!!)
//        victor.likeother(engine.fetch(3)!!)
//        victor.likeother(engine.fetch(4)!!)
//        victor.likeother(engine.fetch(5)!!)
//        victor.likeother(engine.fetch(6)!!)
//
//        val testList = ArrayList<Picture>()
//        testList.add(engine.fetch(1)!!)
//        testList.add(engine.fetch(2)!!)
//        testList.add(engine.fetch(3)!!)
//        testList.add(engine.fetch(4)!!)
//        testList.add(engine.fetch(5)!!)
//        testList.add(engine.fetch(6)!!)
//        assertEquals(testList,victor.likeList())
//
//        tom.dislikeother(engine.fetch(0)!!)
//        testList.removeAt(0)
//        assertEquals(testList,victor.likeList())
//        grace.dislikeother(engine.fetch(0)!!)
//        assertEquals(testList,victor.likeList())
//        //empty engine
//        engine.emptyEngine()
//        }
//    @Test
//    fun testUser3(){                           //test method User.mypictures()
//        val engine = Engine()
//        val nike = User("nike")
//        nike.post("nike picture")
//        nike.post("nike second pic")
//        nike.post("nike third pic")
//        nike.post("nike forth pic")
//
//        val testList = ArrayList<Picture>()
//        testList.add(engine.fetch(0)!!)
//        testList.add(engine.fetch(1)!!)
//        testList.add(engine.fetch(2)!!)
//        testList.add(engine.fetch(3)!!)
//
//
//        assertEquals(testList,nike.myPictures())
//
//        //empty engine
//        engine.emptyEngine()
//    }
//
//    @Test
//    fun testUser4(){                           //test method User.send_message() and user.send_message_list(),user.received_m_list()
//        val engine = Engine()
//        val nike = User("nike")
//        val tom = User("tom")
//        val grace = User("grace")
//
//        nike.send_message(tom,"hello")
//        nike.send_message(grace,"hi, there")
//
//        val testList = ArrayList<Message>()
//        testList.add(Message(nike,tom,"hello"))
//        testList.add(Message(nike,grace,"hi, there"))
//
//        assertEquals(testList,nike.message_I_send())
//        println(testList)
//        val testlist2 = ArrayList<Message>()
//        testlist2.add(Message(nike,tom,"hello"))
//
//        assertEquals(testlist2,tom.message_I_received())
//        println(nike.profile())
//
//        //empty engine
//        engine.emptyEngine()
//
//    }
//
//
//
//    }
