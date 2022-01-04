package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.not
import org.litote.kmongo.reactivestreams.KMongo

//specify how we want to access database
private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User):Boolean{
    /**
     * insertOne is used to insert the user in the collection.
     * Here, we are using wasAcknowledged because
     * if the write is successful we will return true or else false.
     */
    return users.insertOne(user).wasAcknowledged()
}

//to check if user already exist
suspend fun checkIfUserExists(email:String):Boolean{
    // we will search for the user with email
    // User :: email : go through all users and check if email is existing
    //SELECT * FROM user WHERE email = $email
    return users.findOne(User::email eq email) != null
}

//when user want to sign in, we will check if the password same as user have created
suspend fun checkPasswordForEmail(email: String,passwordToCheck:String):Boolean{
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == passwordToCheck
}



suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains  email).toList()
}