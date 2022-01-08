package net.okocraft.durabilitytools.command;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.okocraft.durabilitytools.DurabilityTools;
import net.okocraft.durabilitytools.configuration.languages.Languages;
import org.bukkit.command.CommandSender;

@Accessors(fluent = true)
public abstract class BaseCommand {

    protected final Commands registration;
    protected final DurabilityTools plugin;
    protected final Languages languages;

    @Getter
    protected final String name;

    @Getter
    protected final String permissionNode;

    @Getter
    protected final int leastArgLength;

    @Getter
    protected final boolean playerOnly;

    @Getter
    protected final String usage;

    @Getter
    protected final List<String> alias;

    protected BaseCommand(Commands registration, String name, String permissionNode, int leastArgLength,
                          boolean playerOnly, String usage, String... alias) {
        this.registration = registration;
        this.plugin = registration.plugin;
        this.languages = registration.languages;
        this.name = name;
        this.permissionNode = permissionNode;
        this.leastArgLength = leastArgLength;
        this.playerOnly = playerOnly;
        this.usage = usage;
        this.alias = Arrays.asList(alias);
    }

    public abstract boolean runCommand(CommandSender sender, String[] args);

    public List<String> runTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    public boolean validateArgsLength(int argsLength) {
        return leastArgLength() <= argsLength;
    }

    public boolean hasPermission(CommandSender sender) {
        if (permissionNode == null || permissionNode.isEmpty()) {
            return true;
        }

        return sender.hasPermission(permissionNode());
    }

    /**
     * numberを解析してint型にして返す。numberのフォーマットがintではないときはdefを返す。
     *
     * @param number 解析する文字列
     * @param def    解析に失敗したときに返す数字
     * @return int型の数字。
     * @author LazyGon
     */
    protected int parseIntOrDefault(String number, int def) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            return def;
        }
    }
}