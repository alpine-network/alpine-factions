package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.command.AlpineCommand;
import co.crystaldev.alpinecore.util.Messaging;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.util.ComponentHelper;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.command.CommandSender;

/**
 * @since 0.1.0
 */
@Command(name = "factions version")
@Description("Display plugin versioning information.")
final class VersionCommand extends AlpineCommand {
    public VersionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context CommandSender sender) {
        String authors = String.join(", ", AlpineFactions.getInstance().getDescription().getAuthors());
        Messaging.send(sender, ComponentHelper.mini(String.join("<br>",
                "<info>.</info>",
                "<info>|</info> This server is running <b><gradient:#00aaaa:#78cccc:#00aaaa>AlpineFactions</gradient></b>",
                "<info>|</info> <emphasis>Version:</emphasis> v" + Reference.VERSION,
                "<info>|</info> <emphasis>Authors:</emphasis> " + authors,
                "<info>'</info>"
        )));
    }
}
