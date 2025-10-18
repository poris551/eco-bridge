package digital.proona.ecobridge.paper

import digital.proona.ecobridge.economy.PlayerEconomy
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import java.util.logging.Logger

class EcoBridgeEconomy(private val log: Logger) : Economy {

    private val econImpl = PlayerEconomy

    override fun isEnabled(): Boolean = true

    override fun getName(): String = "EcoBridge"

    override fun fractionalDigits(): Int = 0

    override fun currencyNameSingular(): String = "원"
    override fun currencyNamePlural(): String = "원"

    override fun format(amount: Double): String = String.format("%,.0f원", amount)

    override fun hasAccount(player: OfflinePlayer?): Boolean = player?.uniqueId != null
    override fun hasAccount(playerName: String?): Boolean = true

    override fun hasAccount(player: OfflinePlayer?, worldName: String?): Boolean = hasAccount(player)
    override fun hasAccount(playerName: String?, worldName: String?): Boolean = hasAccount(playerName)

    override fun createPlayerAccount(player: OfflinePlayer?): Boolean = true
    override fun createPlayerAccount(playerName: String?): Boolean = true

    override fun createPlayerAccount(player: OfflinePlayer?, worldName: String?): Boolean = createPlayerAccount(player)
    override fun createPlayerAccount(playerName: String?, worldName: String?): Boolean = createPlayerAccount(playerName)

    override fun getBalance(player: OfflinePlayer?): Double {
        return player?.uniqueId?.let { econImpl.getBalance(it).toDouble() } ?: 0.0
    }

    override fun getBalance(playerName: String?): Double {
        return 0.0
    }

    override fun getBalance(player: OfflinePlayer?, worldName: String?): Double = getBalance(player)
    override fun getBalance(playerName: String?, worldName: String?): Double = getBalance(playerName)


    override fun has(player: OfflinePlayer?, amount: Double): Boolean {
        return getBalance(player) >= amount
    }

    override fun has(playerName: String?, amount: Double): Boolean {
        return false
    }

    override fun has(player: OfflinePlayer?, worldName: String?, amount: Double): Boolean = has(player, amount)
    override fun has(playerName: String?, worldName: String?, amount: Double): Boolean = has(playerName, amount)


    override fun depositPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null || amount <= 0) return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "플레이어 또는 금액 없음"
        )

        val uuid = player.uniqueId
        val newBalance = econImpl.addBalance(uuid, amount.toInt())

        return EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, null)
    }

    override fun depositPlayer(playerName: String?, amount: Double): EconomyResponse {
        return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "OfflinePlayer 사용 필요."
        )
    }

    override fun depositPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse = depositPlayer(player, amount)
    override fun depositPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse = depositPlayer(playerName, amount)

    override fun withdrawPlayer(player: OfflinePlayer?, amount: Double): EconomyResponse {
        if (player == null || amount <= 0) return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "플레이어 또는 금액 없음"
        )

        val uuid = player.uniqueId
        val result = econImpl.removeBalance(uuid, amount.toInt())

        val currentBalance = econImpl.getBalance(uuid).toDouble()

        return if (result) {
            EconomyResponse(amount, currentBalance, EconomyResponse.ResponseType.SUCCESS, null)
        } else {
            EconomyResponse(0.0, currentBalance, EconomyResponse.ResponseType.FAILURE, "돈 부족.")
        }
    }

    override fun withdrawPlayer(playerName: String?, amount: Double): EconomyResponse {
        return EconomyResponse(
            0.0,
            0.0,
            EconomyResponse.ResponseType.FAILURE,
            "OfflinePlyaer 사용 필요."
        )
    }


    override fun withdrawPlayer(player: OfflinePlayer?, worldName: String?, amount: Double): EconomyResponse = withdrawPlayer(player, amount)
    override fun withdrawPlayer(playerName: String?, worldName: String?, amount: Double): EconomyResponse = withdrawPlayer(playerName, amount)


    override fun hasBankSupport(): Boolean = false

    override fun createBank(name: String?, owner: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun createBank(name: String?, owner: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun deleteBank(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankBalance(name: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankHas(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankWithdraw(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun bankDeposit(name: String?, amount: Double): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankOwner(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankOwner(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankMember(name: String?, playerName: String?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun isBankMember(name: String?, player: OfflinePlayer?): EconomyResponse {
        return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "")
    }

    override fun getBanks(): MutableList<String?> {
        return mutableListOf()
    }
}