package com.dsm.domain.mission.usecase

import com.dsm.exception.QuestException
import com.dsm.persistence.entity.Quest
import com.dsm.persistence.repository.QuestRepository
import com.dsm.plugins.database.dbQuery
import java.time.LocalDateTime

/**
 *
 * 배달 미션 게시를 담당하는 PostMission
 *
 * @author Chokyunghyeon
 * @date 2023/04/26
 **/
class PostQuest(
    private val questRepository: QuestRepository
) {

    suspend operator fun invoke(request: Request, studentId: Int) : Unit = dbQuery {
        if (questRepository.existsByOwnerId(studentId)) {
            throw QuestException.AlreadyPosted()
        }

        questRepository.insert(Quest.doPost(
            orderId = studentId,
            stuff = request.stuff,
            deadline = request.deadline,
            price = request.price
        ))
    }

    data class Request(
        val stuff: String,
        val deadline: LocalDateTime,
        val price: Long
    )
}