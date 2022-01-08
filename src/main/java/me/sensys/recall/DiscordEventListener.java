package me.sensys.recall;

import  lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DiscordEventListener extends ListenerAdapter {


    private final Recall plugin;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("rc/status")) {

            MessageChannel channel = event.getChannel();
            channel.sendMessage("The Server Is On");

        }
    }
}
