package com.androiddevs.routs

import com.androiddevs.data.checkIfUserExists
import com.androiddevs.data.collections.User
import com.androiddevs.data.registerUser
import com.androiddevs.data.requests.AccountRequest
import com.androiddevs.data.responses.SimpleResponse
import com.androiddevs.security.getHashWithSalt
import io.ktor.application.call
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route

//coroutine function
fun Route.registerRoute(){
    route("/register"){
        post {
            val request = try {
                call.receive<AccountRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val userExists = checkIfUserExists(request.email)
            if (!userExists){
                if (registerUser(User(request.email,getHashWithSalt(request.password)))){//
                    call.respond(OK,SimpleResponse(true,"Successfully create account!"))
                }else{
                    call.respond(OK,SimpleResponse(false,"an unknown error occurred"))
                }
            }else{
                call.respond(OK,SimpleResponse(false,"A user with that E-mail already exists"))
            }
        }
    }
}