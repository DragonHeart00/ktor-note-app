package com.androiddevs.routs


import com.androiddevs.data.collections.Note
import com.androiddevs.data.getNotesForUser
import com.androiddevs.data.saveNote
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*


fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }
    route("/addNote"){
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                }catch (e:ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                if (saveNote(note)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }

            }
        }
    }
}