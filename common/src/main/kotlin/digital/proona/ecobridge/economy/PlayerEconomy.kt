package digital.proona.ecobridge.economy

import digital.proona.redisClient.RuneRedisAPI
import java.util.UUID

object PlayerEconomy {

    private const val NAMESPACE = "rune"
    private const val SET_NAME = "playerdata"

    fun getBalance(uuid: UUID): Int {
        return RuneRedisAPI.hgetInt(NAMESPACE, SET_NAME, uuid.toString(), "money") ?: 0
    }

    fun setBalance(uuid: UUID, amount: Int) {
        RuneRedisAPI.hset(NAMESPACE, SET_NAME, uuid.toString(), "money", amount)
    }

    fun addBalance(uuid: UUID, amount: Int): Int {
        return RuneRedisAPI.hincr(NAMESPACE, SET_NAME, uuid.toString(), "money", amount)
    }

    fun removeBalance(uuid: UUID, amount: Int): Boolean {
        val newBalance = RuneRedisAPI.hincr(NAMESPACE, SET_NAME, uuid.toString(), "money", -amount)
        return if (newBalance < 0) {
            RuneRedisAPI.hincr(NAMESPACE, SET_NAME, uuid.toString(), "money", amount)
            false
        } else true
    }

    fun getTopBalances(limit: Int = 10): List<Pair<UUID, Int>> {
        return RuneRedisAPI.getKeys(NAMESPACE, SET_NAME).mapNotNull { key ->
            val uuid = runCatching { UUID.fromString(key.substringAfterLast(":")) }.getOrNull() ?: return@mapNotNull null
            uuid to getBalance(uuid)
        }.sortedByDescending { it.second }
            .take(limit)
    }
}
