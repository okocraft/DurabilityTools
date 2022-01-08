package net.okocraft.durabilitytools.command;

import net.okocraft.durabilitytools.configuration.languages.language.Language;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {

    protected ReloadCommand(Commands registration) {
        super(
            registration,
            "reload",
            "durabilitytools.commands.reload",
            1,
            false,
            "/dt reload"
        );
    }


    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        Language language = languages.language(sender);
        language.command().reloadCommand().start().sendTo(sender);
        plugin.reload();
        language.command().reloadCommand().complete().sendTo(sender);
        return true;
    }
}
