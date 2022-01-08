package me.sensys.recall;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class Recall extends JavaPlugin {

    private  final Map<String, String> advancementToDisplayMap = new HashMap<>();

    private JDA jda;
    private TextChannel chatChannel;
    private TextChannel statusChannel;

    @SneakyThrows
    @Override
    public void onEnable() {

        saveDefaultConfig();

        String botToken = getConfig().getString("bot-token");
        try {
            jda = JDABuilder.createDefault(botToken)
                    .build()
                    .awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();

        }

        if (jda == null) {
            getServer().getPluginManager().disablePlugin(this);
            getLogger().severe("Can't Connect to Discord Check the bot token in the config");
        }

        String statusChannelId = getConfig().getString("status-channel-id");
        if (statusChannelId != null) {
            statusChannel = jda.getTextChannelById(statusChannelId);
        }

        if (statusChannel != null) {
            statusChannel.sendMessage("The Server Is Turning On").queue();
        }


        String chatChannelId = getConfig().getString("chat-channel-id");
        if (chatChannelId != null) {
            chatChannel = jda.getTextChannelById(chatChannelId);
        }

        ConfigurationSection advancementMap = getConfig().getConfigurationSection("advancementMap");
        if (advancementMap != null) {
            for (String key : advancementMap.getKeys(false)) {;
                advancementToDisplayMap.put(key, advancementMap.getString(key));
            }
        }
        jda.addEventListener(new DiscordListener());
        getServer().getPluginManager().registerEvents(new SpigotListener(), this);
    }

    String ch = getConfig().getString("Server-Status-MSG-Channel-ID:");

    @Override
    public void onDisable() {

        if (statusChannel != null) {
            statusChannel.sendMessage("The Server Is Shutting Down").queue();
        }

        if (jda != null) jda.shutdownNow();
    }

    private void sendMessage(Player player, String content, boolean contentInAuthorLine, Color color) {
        if (chatChannel == null) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor(
                      contentInAuthorLine ? content : player.getDisplayName(),
                        null,
                        "https://crafatar.com/avatars/" + player.getUniqueId().toString() + "?overlay=1"
                );

        if (!contentInAuthorLine) {
            builder.setDescription(content);
        }

        chatChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public final class SpigotListener implements Listener {
        @EventHandler
        private void onChat(AsyncPlayerChatEvent event) {
            sendMessage(event.getPlayer(), event.getMessage(), false, Color.GRAY);
        }

        @EventHandler
        private void onJoin(PlayerJoinEvent event) {
            sendMessage(event.getPlayer(), event.getPlayer().getDisplayName() + " joined the game.", true, Color.GREEN);
        }

        @EventHandler
        private void onQuit(PlayerQuitEvent event) {
            sendMessage(event.getPlayer(), event.getPlayer().getDisplayName() + " left the game.", true, Color.BLUE);
        }

        @EventHandler
        private void onDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            String deathMessage = event.getDeathMessage() == null ? player.getDisplayName() + "died." : event.getDeathMessage();
            sendMessage(player, deathMessage, true, Color.RED);
        }

        @EventHandler
        private void onAdvancement(PlayerAdvancementDoneEvent event) {
            String advancementKey = event.getAdvancement().getKey().getKey();
            String display = advancementToDisplayMap.get(advancementKey);
            if (display == null) return;
            sendMessage(event.getPlayer(), event.getPlayer().getDisplayName() + " has made the advancement {" + display +"}", true, Color.CYAN);
        }
    }

    public  final class DiscordListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            if (!event.getChannel().equals(chatChannel)) return;

            Member member = event.getMember();
            if (member == null || member.getUser().isBot()) return;

            String message = event.getMessage().getContentDisplay();
            Bukkit.broadcastMessage(ChatColor.BLUE + "<" + member.getEffectiveName() + ">" + ChatColor.RESET + " " + message);
        }
    }

}
