package net.okocraft.durabilitytools.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public class HelpCommand extends BaseCommand {
    protected HelpCommand(Commands registration) {
        super(registration,
            "help",
            "durabilitytools.commands.help",
            1,
            false,
            "/dt help"
        );
    }

    @Override
    public boolean runCommand(CommandSender sender, String[] args) {
        var helpCommand = languages.language(sender).command().helpCommand();
        
        List<BaseCommand> permittedCommands = registration.getPermittedCommands(sender);
        if (permittedCommands.isEmpty()) {
            helpCommand.noPermittedCommand().sendTo(sender);
            return true;
        }
        
        helpCommand.line().sendTo(sender);
        for (BaseCommand command : permittedCommands) {
            helpCommand.content().sendTo(sender, command.usage, helpCommand.description().get(command.name()));
        }
        helpCommand.line().sendTo(sender);
        return true;
    }
}
