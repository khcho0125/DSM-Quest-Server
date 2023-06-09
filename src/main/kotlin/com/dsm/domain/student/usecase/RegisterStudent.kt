package com.dsm.domain.student.usecase

import com.dsm.domain.student.token.TokenProvider
import com.dsm.domain.student.token.TokenResult
import com.dsm.exception.AuthenticateStudentException
import com.dsm.exception.DormitoryRoomException
import com.dsm.persistence.entity.AuthenticateStudent
import com.dsm.persistence.entity.Password
import com.dsm.persistence.entity.Student
import com.dsm.persistence.repository.AuthenticateStudentRepository
import com.dsm.persistence.repository.DormitoryRoomRepository
import com.dsm.persistence.repository.StudentRepository
import com.dsm.plugins.database.dbQuery
import kotlinx.serialization.Serializable

/**
 *
 * 학생 가입을 담당하는 RegisterStudent
 *
 * @author Chokyunghyeon
 * @date 2023/03/31
 **/
class RegisterStudent(
    private val authenticateStudentRepository: AuthenticateStudentRepository,
    private val studentRepository: StudentRepository,
    private val tokenProvider: TokenProvider,
    private val dormitoryRoomRepository: DormitoryRoomRepository
) {
    suspend operator fun invoke(request: Request): Response = dbQuery {
        val studentId: Int = registerStudentAccount(request)
        return@dbQuery Response(
            id = studentId,
            token = tokenProvider.generateToken(studentId)
        )
    }

    private suspend fun registerStudentAccount(request: Request): Int {
        val authenticate: AuthenticateStudent = authenticateStudentRepository.findByNumber(request.number)
            ?: throw AuthenticateStudentException.UnknownNumber()

        authenticate(request.name)

        if (dormitoryRoomRepository.existsById(request.room).not()) {
            throw DormitoryRoomException.NotFound()
        }

        val studentId: Int = studentRepository.insert(
            Student.register(
                authenticate = authenticate,
                password = request.password,
                room = request.room
            )
        )
        authenticateStudentRepository.update(authenticate.used())

        return studentId
    }

    class Request(
        val number: Int,
        val name: String,
        val room: Int,
        password: String
    ) {
        val password: Password by lazy { Password(password) }
    }

    @Serializable
    data class Response(
        val id: Int,
        val token: TokenResult
    )
}
