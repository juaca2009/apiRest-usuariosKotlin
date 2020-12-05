package com.puj.admincenter.service

import com.puj.admincenter.domain.users.User
import com.puj.admincenter.dto.users.UserDto
import com.puj.admincenter.dto.users.CreateUserDto
import com.puj.admincenter.dto.IdResponseDto
import com.puj.admincenter.repository.users.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.puj.admincenter.dto.users.updatePasswordDto
import com.puj.admincenter.dto.users.UpdateUserDto
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {
    companion object {
        val LOG = LoggerFactory.getLogger(UserService::class.java)!!
    }

    fun count(): Long {
        return userRepository.count()
    }

    fun getById(userId: Int,
                authorization: String?): ResponseEntity<*> {

        val user = userRepository.findById(userId)  // Hace solo el query
        return if (user.isPresent()) {
            ResponseEntity.ok(UserDto.convert(user.get()))
        } else {
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }
    }

    fun getAllUsers():ResponseEntity<List<User>>{
        val user = userRepository!!.findAll()
        return if (user!=null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity<List<User>>(HttpStatus.NOT_FOUND)
        }
    }

    fun create(createUserDto: CreateUserDto): ResponseEntity<*> {
        if (userRepository.existsByEmail(createUserDto.email)) {
            val messageError = "User with email: ${createUserDto.email} already exists."
            LOG.error(messageError)
            return ResponseEntity<Any>(messageError,
                                       HttpStatus.CONFLICT)
        }
        //instancia encargada de encriptar contraseña
        val encriptador = BCryptPasswordEncoder()
        //encripta contraseña
        val contra_encrip = encriptador.encode(createUserDto.password)
        val user = User(email = createUserDto.email,
                        name = createUserDto.name,
                        password = contra_encrip,
                        username = createUserDto.username)
        val userSaved = userRepository.save(user)
        LOG.info("User ${createUserDto.email} created with id ${userSaved.id}")

        val responseDto = IdResponseDto(userSaved.id.toLong())
        return ResponseEntity<IdResponseDto>(responseDto,
                                             HttpStatus.CREATED)
    }

    @Transactional
    fun updatePsw(updateP: updatePasswordDto): ResponseEntity<*> {
        val encriptador = BCryptPasswordEncoder()
        val contra = userRepository.passwordByUsername(updateP.username)
        val user = userRepository.findUserByUsername(updateP.username)
        return if (user != null && encriptador.matches(updateP.password, contra)){
            val nPas = encriptador.encode(updateP.passwordN)
            userRepository.updatePasswordByUsername(nPas, user.id)
            LOG.info("Password of User ${updateP.username} updated with id ${user.id}")
            val responseDto = IdResponseDto(user.id.toLong())
            ResponseEntity<IdResponseDto>(responseDto, HttpStatus.OK)
        }
        else{
            val message2 = "the user does not exist or is not enabled" 
            ResponseEntity<String>(message2, HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun editUser(userId: Int, updateUserDto: UpdateUserDto): ResponseEntity<*> {
        val currentUser = userRepository.findById(userId);
        return if(currentUser != null) {
            val encriptador=BCryptPasswordEncoder()

            val contra_encrip = encriptador.encode(updateUserDto.password)
            val updateUser:User = currentUser.get().copy(
                email = updateUserDto.email,
                name = updateUserDto.name,
                password=contra_encrip,
                username = updateUserDto.username
            )
            val userSaved = userRepository.save(updateUser)
            LOG.info("User ${userSaved.email} was updated successfully")
    
            ResponseEntity<Any>(HttpStatus.OK)
        } else {
            LOG.error("User not found")
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }
    }

    @Transactional
    fun deleteUser(userId: Int): ResponseEntity<*> {
        val currentUser = userRepository.findUserById(userId);
        return if(currentUser != null)  {
            userRepository.deleteByUserId(userId)
            LOG.info("User with id $userId was deleted successfully")

            ResponseEntity<Any>(HttpStatus.OK)
        } else {
            LOG.error("User not found")
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        }
    } 
}