package com.dsm.persistence.entity

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

/**
 *
 * 배달 미션 객체를 담당하는 Mission
 *
 * @author Chokyunghyeon
 * @date 2023/03/16
 **/
object MissionTable : UUIDTable("tbl_mission") {
    val student: Column<EntityID<UUID>> = reference("student_id", StudentTable).uniqueIndex()
    val deliveryman: Column<EntityID<UUID>?> = reference("delivery_man_id", StudentTable).nullable()
    val stuff: Column<String> = varchar("stuff", Mission.STUFF_MAX_LENGTH)
    val deadline: Column<LocalDateTime> = datetime("deadline")
    val price: Column<Long> = long("price")
    val state: Column<DeliveryState> = enumerationByName("state", DeliveryState.STATE_MAX_LENGTH)
}

enum class DeliveryState {
    POSTING, DELIVERING, COMPLETED, MISSED;

    internal companion object {
        const val STATE_MAX_LENGTH: Int = 10
    }
}

data class Mission(
    val id: UUID = UUID(0, 0),
    val studentId: UUID,
    val deliverymanId: UUID?,
    val stuff: String,
    val deadline: LocalDateTime,
    val price: Long,
    val state: DeliveryState
) {

    internal companion object {
        const val STUFF_MAX_LENGTH: Int = 50

        fun doPost(studentId: UUID, stuff: String, deadline: LocalDateTime, price: Long): Mission = Mission(
            studentId = studentId,
            stuff = stuff,
            deadline = deadline,
            state = DeliveryState.POSTING,
            price = price,
            deliverymanId = null
        )
    }
}
