package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

//specify how we want to access database
private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User):Boolean{
    return users.insertOne(user).wasAcknowledged()
}

//to check if user already exist
suspend fun checkIfUserExists(email:String):Boolean{
    // we will search for the user with email
    // User :: email : go through all users and check if email is existing
    //SELECT * FROM user WHERE email = $email
    return users.findOne(User::email eq email) != null
}