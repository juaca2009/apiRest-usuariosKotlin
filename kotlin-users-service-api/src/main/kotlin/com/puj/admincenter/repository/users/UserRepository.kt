package com.puj.admincenter.repository.users

import com.puj.admincenter.domain.users.User

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Int>,
                           JpaSpecificationExecutor<User> {

    @Query("""
        SELECT User
        FROM User User
        WHERE User.username = :username
        AND User.password = :password
    """)
    fun findUserByUserAndPassword(username: String,
                                  password: String): User?

    @Query("""
        SELECT COUNT(User) > 0
        FROM User User
        WHERE User.email = :email
    """)
    fun existsByEmail(@Param("email") email: String): Boolean

    @Query("""
        SELECT User.password
        FROM User User
        WHERE User.username = :username
    """)
    fun passwordByUsername(username: String): String

    @Query( """
        SELECT User
        FROM User User 
        WHERE User.username = :username
    """)
    fun findUserByUsername(username: String): User?
}