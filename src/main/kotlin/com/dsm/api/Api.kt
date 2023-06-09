package com.dsm.api

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

/**
 *
 * Routing을 도와주는 Api
 *
 * @author Chokyunghyeon
 * @date 2023/03/22
 **/
abstract class Api(private val route: Routing.() -> Unit) {
    operator fun invoke(app: Application): Routing = app.routing(route)
}
