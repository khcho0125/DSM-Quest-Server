package com.dsm.api

import com.dsm.domain.mission.usecase.GetQuest
import com.dsm.domain.mission.usecase.PostQuest
import com.dsm.plugins.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 *
 * 배달 퀘스트의 API를 관리하는 QuestApi
 *
 * @author Chokyunghyeon
 * @date 2023/04/26
 **/
class QuestApi(
    postQuest: PostQuest,
    getQuest: GetQuest
) : Api({
    route("/mission") {
        get {
            call.respond(
                message = getQuest(),
                status = HttpStatusCode.OK
            )
        }
    }

    authenticate("/quest") {
        post {
            val request: PostQuest.Request = call.receive()
            val studentId: Int = call.currentUserId()

            postQuest(request, studentId)

            call.response.status(HttpStatusCode.NoContent)
        }

        patch("/{quest-id}") {

        }

        delete("/{quest-id}") {

        }
    }
}) {
    companion object {
        val module: Module = module {
            singleOf(::PostQuest)
            singleOf(::GetQuest)
            singleOf(::QuestApi) bind Api:: class
        }
    }
}