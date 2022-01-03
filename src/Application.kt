package com.androiddevs

import com.androiddevs.data.collections.User
import com.androiddevs.data.registerUser
import com.androiddevs.routs.registerRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing){
        registerRoute()
    }

    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }


}

