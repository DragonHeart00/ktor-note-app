package com.androiddevs.data

import com.androiddevs.data.collections.Note
import com.androiddevs.data.collections.User
import com.androiddevs.security.checkHashForPassword
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.not
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

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
    return checkHashForPassword(passwordToCheck, actualPassword)
}

suspend fun getAllNotes(): List<Note> {
    return notes.find().toFlow().toList()
}
/*
*
*
* */
suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains  email).toList()
}

suspend fun saveNote(note: Note):Boolean{
    val noteExists = notes.findOneById(note.id) != null
    //if exists, we will update the note
    return if (noteExists){
        notes.updateOneById(note.id,note).wasAcknowledged()
    //if not we will insert a new one
    }else {
        notes.insertOne(note).wasAcknowledged()
    }
}


suspend fun isOwnerOfNote(noteId: String,owner: String): Boolean{
    val note = notes.findOneById(noteId) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(noteId: String, owner:String):Boolean{
    val owners = notes.findOneById(noteId)?.owners ?: return false
    return notes.updateOneById(noteId, setValue(Note::owners,owners+owner)).wasAcknowledged()
}


suspend fun deleteNoteForUser(email: String, noteId:String):Boolean{
    val note = notes.findOne(Note::id eq noteId, Note::owners contains email )
    note?.let { note ->
        if(note.owners.size > 1){
            //the note has multiple owners, so we just delete the email from the owners list
            val newOwners = note.owners - email
            val updateResult = notes.updateOne(Note::id eq note.id, setValue(Note::owners,newOwners))
            return updateResult.wasAcknowledged()
        }
         return notes.deleteOneById(note.id).wasAcknowledged()
    } ?: return false
}