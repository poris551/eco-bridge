package digital.proona.ecobridge.velocity

import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import jakarta.inject.Inject

@Plugin(id = "ecobridge-velocity", name = "EcoBridgeVelocity", version = "1.0-SNAPSHOT")
class EcoBridgeVelocityPlugin @Inject constructor(
    val server: ProxyServer
) {
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        val manager: CommandManager = server.commandManager
        manager.register(manager.metaBuilder("돈").build(), MoneyCommand(this))
    }
}
