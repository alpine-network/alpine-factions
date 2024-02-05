package co.crystaldev.factions.command;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.factions.AlpineFactions;
import co.crystaldev.factions.Reference;
import co.crystaldev.factions.command.framework.BaseFactionsCommand;
import co.crystaldev.factions.util.ComponentHelper;
import co.crystaldev.factions.util.Messaging;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.description.Description;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

/**
 * @author BestBearr <crumbygames12@gmail.com>
 * @since 02/04/2024
 */
@Command(name = "factions version")
@Description("Display plugin versioning information.")
public final class VersionCommand extends BaseFactionsCommand {

    private static final Component VERSION_COMPONENT;

    public VersionCommand(AlpinePlugin plugin) {
        super(plugin);
    }

    @Execute
    public void execute(@Context CommandSender sender) {
        Messaging.send(sender, VERSION_COMPONENT);

        Messaging.send(sender, Reference.MINI_MESSAGE.deserialize("<info>This is a test"));
    }

    static {
        String authors = String.join(", ", AlpineFactions.getInstance().getDescription().getAuthors());

        MiniMessage mm = Reference.MINI_MESSAGE;
        VERSION_COMPONENT = ComponentHelper.joinNewLines(
                mm.deserialize("<dark_aqua>."),
                mm.deserialize("<dark_aqua>|</dark_aqua> This server is running <b><gradient:#00aaaa:#78cccc:#00aaaa>AlpineFactions</gradient>"),
                mm.deserialize("<dark_aqua>|</dark_aqua> <gray>Version:</gray> <color:#b8f2f2>v" + Reference.VERSION),
                mm.deserialize("<dark_aqua>|</dark_aqua> <gray>Authors:</gray> <color:#b8f2f2>" + authors),
                mm.deserialize("<dark_aqua>'")
        );
    }
}
