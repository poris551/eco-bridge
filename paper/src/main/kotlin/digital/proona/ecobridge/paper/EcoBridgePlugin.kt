package digital.proona.ecobridge.paper

import net.milkbowl.vault.economy.Economy
import org.bukkit.event.Listener
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class EcoBridgePlugin : JavaPlugin(), Listener {
    override fun onEnable() {
        registerEcoBridgeAsVault()
        server.pluginManager.registerEvents(this, this)
    }

    private fun registerEcoBridgeAsVault() {
        val ecoImpl = EcoBridgeEconomy(logger)
        server.servicesManager.register(Economy::class.java, ecoImpl, this, ServicePriority.Highest)
    }
}