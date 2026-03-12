package com.example.smartretailph.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.smartretailph.data.local.AppDatabase
import com.example.smartretailph.data.local.dao.UserDao
import com.example.smartretailph.data.local.entities.UserEntity
import com.example.smartretailph.data.models.User
import com.example.smartretailph.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun logout()
}

/**
 * Local multi-user authentication using SharedPreferences with salted password hashes.
 *
 * - Supports multiple accounts on a single device (by email).
 * - Persists the currently logged-in user between app launches.
 * - Stores salted SHA-256 hashes (not plain text passwords).
 */
class LocalAuthRepository(
    context: Context
) : AuthRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val userDao: UserDao =
        AppDatabase.getInstance(context.applicationContext).userDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    init {
        // Restore previously logged-in user if any
        val email = prefs.getString(KEY_CURRENT_EMAIL, null)
        val uid = prefs.getString(KEY_CURRENT_UID, null)
        if (email != null && uid != null) {
            _currentUser.value = User(uid = uid, email = email)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isEmpty() || password.isEmpty()) {
            return Result.Error("Email and password must not be empty")
        }

        val entity = userDao.findByEmail(normalizedEmail)
        if (entity == null) {
            return Result.Error("Account not found")
        }

        val computedHash = hashPassword(password, entity.passwordSalt)
        if (computedHash != entity.passwordHash) {
            return Result.Error("Incorrect password")
        }

        val user = User(uid = entity.id, email = normalizedEmail, displayName = entity.displayName)
        setCurrentUser(user)
        return Result.Success(user)
    }

    override suspend fun signUp(email: String, password: String): Result<User> {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isEmpty() || password.length < 6) {
            return Result.Error("Invalid email or password too short")
        }

        val existing = userDao.findByEmail(normalizedEmail)
        if (existing != null) {
            return Result.Error("Account already exists")
        }

        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        val uid = UUID.randomUUID().toString()

        userDao.insert(
            UserEntity(
                id = uid,
                email = normalizedEmail,
                displayName = null,
                passwordSalt = salt,
                passwordHash = hash
            )
        )

        val user = User(uid = uid, email = normalizedEmail)
        setCurrentUser(user)
        return Result.Success(user)
    }

    override suspend fun logout() {
        prefs.edit()
            .remove(KEY_CURRENT_EMAIL)
            .remove(KEY_CURRENT_UID)
            .apply()
        _currentUser.value = null
    }

    private fun setCurrentUser(user: User) {
        prefs.edit()
            .putString(KEY_CURRENT_EMAIL, user.email)
            .putString(KEY_CURRENT_UID, user.uid)
            .apply()
        _currentUser.value = user
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    private fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        val hashBytes = digest.digest(password.toByteArray())
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    companion object {
        private const val PREFS_NAME = "local_auth_prefs"
        private const val KEY_CURRENT_EMAIL = "current_email"
        private const val KEY_CURRENT_UID = "current_uid"
    }
}

