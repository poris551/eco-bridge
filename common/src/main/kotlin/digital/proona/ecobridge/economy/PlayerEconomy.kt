package digital.proona.ecobridge.economy

import digital.proona.redisClient.RuneRedisAPI
import java.util.UUID

object PlayerEconomy {

    private const val NAMESPACE = "rune"
    private const val SET_NAME = "playerdata"

    fun getBalance(uuid: UUID): Int {
        val data = RuneRedisAPI.finder(NAMESPACE, SET_NAME, uuid.toString()).getAll() ?: emptyMap()
        return data["money"]?.toIntOrNull() ?: 0
    }

    fun setBalance(uuid: UUID, amount: Int, publishUpdate: Boolean = true) {
        RuneRedisAPI.save(NAMESPACE, SET_NAME, uuid.toString(), mapOf("money" to amount.toString()))
//        if (publishUpdate) {
//            RuneRedisAPI.publish("economy:balance_update", "$uuid:money=$amount")
//        }
    }

    fun addBalance(uuid: UUID, amount: Int) : Int {
        val current = getBalance(uuid)
        setBalance(uuid, current + amount)
        return current+amount
    }

    fun removeBalance(uuid: UUID, amount: Int): Boolean {
        val current = getBalance(uuid)
        if (current < amount) return false
        setBalance(uuid, current - amount)
        return true
    }

    fun getTopBalances(limit: Int = 10): List<Pair<UUID, Int>> {
        val keys = RuneRedisAPI.getKeys(NAMESPACE, SET_NAME)
        return keys.mapNotNull { key ->
            val uuidStr = key.substringAfterLast(":")
            val uuid = try { UUID.fromString(uuidStr) } catch (e: Exception) { null } ?: return@mapNotNull null
            uuid to getBalance(uuid)
        }.sortedByDescending { it.second }
            .take(limit)
    }
}