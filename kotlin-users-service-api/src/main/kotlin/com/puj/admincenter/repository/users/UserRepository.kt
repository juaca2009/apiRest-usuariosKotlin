package com.puj.admincenter.repository.users

import com.puj.admincenter.domain.users.User

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
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
        SELECT User
        FROM User User
        WHERE User.id = :id
    """)
    fun findUserById(id: Int): User?

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

    @Modifying
    @Query("""
        DELETE
        FROM User user
        WHERE user.id = :id
    """)
    fun deleteByUserId(@Param("id") id: Int): Int

    @Query( """
        SELECT User
        FROM User User 
        WHERE User.username = :username
    """)
    fun findUserByUsername(username: String): User?

    @Query("""
        SELECT User.password
        FROM User User
        WHERE User.email = :email
    """)
    fun findPasswordByEmail(email: String): String?

    @Query("""
        SELECT User
        FROM User User
        WHERE User.email = :email
    """)
    fun findUserByEmail(email: String): User?
    
    @Modifying
    @Query("UPDATE User User SET User.password = :nPassword WHERE User.id = :id")
    fun updatePasswordByUsername(nPassword: String, id: Int)

    @Modifying
    @Query("UPDATE User User SET User.password = :nPassword, User.email= :nEmail, User.name= :nName, User.username= :nUsername WHERE User.id = :id")
    fun updateUser(nPassword: String, nEmail:String, nName:String, nUsername:String, id: Int)
}