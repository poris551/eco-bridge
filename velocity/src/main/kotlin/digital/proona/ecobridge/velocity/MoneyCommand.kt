package digital.proona.ecobridge.velocity

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import digital.proona.cacheClient.RuneCacheAPI
import digital.proona.ecobridge.economy.PlayerEconomy
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import java.util.UUID

class MoneyCommand(private val plugin: EcoBridgeVelocityPlugin) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()

        if (source !is Player) {
            source.sendMessage(Component.text("콘솔에서는 사용할 수 없습니다."))
            return
        }

        when {
            args.isEmpty() -> {
                val balance = PlayerEconomy.getBalance(source.uniqueId)
                source.sendMessage(
                    Component.text("│ 현재 돈: ", TextColor.color(0xFFD700))
                        .append(Component.text("$balance 원", TextColor.color(0x00FF00)))
                )
            }

            args.size == 1 && args[0].equals("순위", ignoreCase = true) || (args.size == 2 && args[0].equals("순위", ignoreCase = true)) -> {
                val top = PlayerEconomy.getTopBalances()
                val pageSize = 10
                val totalPages = maxOf((top.size + pageSize - 1) / pageSize, 1)

                val inputPage = args.getOrNull(1)?.toIntOrNull() ?: 1
                val page = inputPage.coerceIn(1, totalPages)

                if (inputPage != page) {
                    source.sendMessage(Component.text("│ 존재하지 않는 페이지입니다. 마지막 페이지 : $totalPages", TextColor.color(0xFF0000)))
                    return
                }

                val startIndex = (page - 1) * pageSize
                val endIndex = minOf(startIndex + pageSize, top.size)
                val pageList = if (startIndex < top.size) top.subList(startIndex, endIndex) else emptyList()

                source.sendMessage(Component.text("│ 돈 순위 TOP $page (${page}/${totalPages})", TextColor.color(0x00BFFF)))
                pageList.forEachIndexed { index, (uuid, money) ->
                    val playerName = plugin.server.getPlayer(uuid).map { it.username }
                        .orElseGet {
                            RuneCacheAPI.finder("rune", "player_names", uuid.toString()).getAll()?.get("name")
                                ?: uuid.toString()
                        }

                    val color = when (startIndex + index) {
                        0 -> TextColor.color(0xFFD700)
                        1 -> TextColor.color(0xC0C0C0)
                        2 -> TextColor.color(0xCD7F32)
                        else -> TextColor.color(0xFFFFFF)
                    }

                    source.sendMessage(Component.text("│ ${startIndex + index + 1}. $playerName - $money 원", color))
                }

                val previousPage = page-1
                val nextPage = page+1

                source.sendMessage(Component.text(""))
                source.sendMessage(
                    Component.text("│ ")
                        .append(Component.text("◀ 이전", TextColor.color(0x00FF00))
                            .clickEvent(ClickEvent.runCommand("/돈 순위 $previousPage")))
                        .append(Component.text(" | "))
                        .append(Component.text("다음 ▶", TextColor.color(0x00FF00))
                            .clickEvent(ClickEvent.runCommand("/돈 순위 $nextPage")))
                )
                source.sendMessage(Component.text(""))
            }

            args.size >= 2 && args[0].equals("보내기", ignoreCase = true) -> {
                val targetName = args[1]
                val amount = args.getOrNull(2)?.toIntOrNull()

                if (amount == null || amount <= 0) {
                    source.sendMessage(Component.text("│ 금액이 올바르지 않습니다.", TextColor.color(0xFF0000)))
                    return
                }

                val targetPlayer = plugin.server.getPlayer(targetName).orElse(null)

                val targetUuid = targetPlayer?.uniqueId ?: run {
                    val redisData = RuneCacheAPI.finder("rune", "player_names", targetName).getAll()
                    redisData?.get("uuid")?.let { UUID.fromString(it) }
                }

                if (targetUuid == null) {
                    source.sendMessage(Component.text("│ 플레이어를 찾을 수 없습니다.", TextColor.color(0xFF0000)))
                    return
                }

                if (targetUuid == source.uniqueId) {
                    source.sendMessage(Component.text("│ 자기 자신에게는 돈을 보낼 수 없습니다.", TextColor.color(0xFF0000)))
                    return
                }

                val success = PlayerEconomy.removeBalance(source.uniqueId, amount)
                if (!success) {
                    source.sendMessage(Component.text("│ 잔액이 부족합니다.", TextColor.color(0xFF0000)))
                    return
                }

                PlayerEconomy.addBalance(targetUuid, amount)

                source.sendMessage(
                    Component.text("│ ${targetName}님에게 ", TextColor.color(0x00FF00))
                        .append(Component.text("$amount 원", TextColor.color(0xFFD700)))
                        .append(Component.text("을 보냈습니다.", TextColor.color(0x00FF00)))
                )

                targetPlayer?.sendMessage(
                    Component.text("│ ${source.username}님으로부터 ", TextColor.color(0x00BFFF))
                        .append(Component.text("$amount 원", TextColor.color(0xFFD700)))
                        .append(Component.text("을 받았습니다.", TextColor.color(0x00BFFF)))
                )
            }

            else -> {
                source.sendMessage(Component.text(""))
                source.sendMessage(Component.text("│ /돈", TextColor.color(0xFFD700)))
                source.sendMessage(Component.text("│ /돈 순위", TextColor.color(0xFFD700)))
                source.sendMessage(Component.text("│ /돈 보내기 <닉네임> <금액>", TextColor.color(0xFFD700)))
                source.sendMessage(Component.text(""))
            }
        }
    }

    override fun suggest(invocation: SimpleCommand.Invocation): List<String> {
        val args = invocation.arguments()
        return when (args.size) {
            1 -> listOf("순위", "보내기").filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> {
                if (args[0].equals("보내기", ignoreCase = true)) {
                    plugin.server.allPlayers.map { it.username }
                        .filter { it.startsWith(args[1], ignoreCase = true) }
                } else emptyList()
            }

            else -> emptyList()
        }
    }
}
