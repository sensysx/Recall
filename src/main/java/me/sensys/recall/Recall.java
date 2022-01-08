package me.sensys.recall;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.plugin.java.JavaPlugin;

public final class Recall extends JavaPlugin {

    private JDA discordBot;

    @SneakyThrows
    @Override
    public void onEnable() {
        String discordToken = getConfig().getString("DISCORD_TOKEN");

        saveDefaultConfig();

        if (discordToken == null) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe("Please Provide a valid Discord Bot Token in the config");
        }

        this.discordBot = JDABuilder.createDefault(discordToken)
                .build();

        }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
