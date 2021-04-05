package com.mcmiddleearth.entities.command;

import com.google.common.base.Joiner;
import com.mcmiddleearth.command.AbstractCommandHandler;
import com.mcmiddleearth.command.McmeCommandSender;
import com.mcmiddleearth.command.builder.HelpfulLiteralBuilder;
import com.mcmiddleearth.command.builder.HelpfulRequiredArgumentBuilder;
import com.mcmiddleearth.entities.EntityAPI;
import com.mcmiddleearth.entities.entities.McmeEntity;
import com.mcmiddleearth.entities.entities.VirtualEntityFactory;
import com.mcmiddleearth.entities.entities.McmeEntityType;
import com.mcmiddleearth.entities.entities.RealPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class VirtualCommand extends AbstractCommandHandler implements TabExecutor {

    public VirtualCommand(String command) {
        super(command);
    }

    @Override
    protected HelpfulLiteralBuilder createCommandTree(HelpfulLiteralBuilder commandNodeBuilder) {
        commandNodeBuilder
                .requires(sender -> sender instanceof RealPlayer)
                .then(HelpfulLiteralBuilder.literal("spawn")
                        .then(HelpfulRequiredArgumentBuilder.argument("type", word())
                                .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class), null))
                                .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                    .executes(context -> spawnEntity(context.getSource(), context.getArgument("type", String.class),
                                                                                          context.getArgument("name", String.class))))))
                .then(HelpfulLiteralBuilder.literal("remove")
                        .executes(context -> removeEntity((BukkitCommandSender)context.getSource(),
                                                          ((BukkitCommandSender)context.getSource())
                                                          .getSelectedEntities()))
                        .then(HelpfulRequiredArgumentBuilder.argument("name", word())
                                .executes(context -> removeEntity(context.getSource(),
                                                    Collections.singleton(EntityAPI.getEntity(context.getArgument("name", String.class)))))))
                .then(HelpfulLiteralBuilder.literal("selection")
                        .executes(context -> showSelection(context.getSource()))
                        .then(HelpfulLiteralBuilder.literal("clear")
                                .executes(context -> clearSelection(context.getSource()))))
                .then(HelpfulLiteralBuilder.literal("animate")
                        .then(HelpfulRequiredArgumentBuilder.argument("animationId", word())
                                .executes(context -> animateEntity(context.getSource(), context.getArgument("animationId",String.class)))));
        return commandNodeBuilder;
    }

    private int spawnEntity(McmeCommandSender sender, String type, String name) {
        VirtualEntityFactory factory = new VirtualEntityFactory(new McmeEntityType(type), ((RealPlayer)sender).getLocation()).withName(name);
        ((BukkitCommandSender)sender).setSelection(EntityAPI.spawnEntity(factory));
        return 0;
    }

    private int removeEntity(McmeCommandSender sender, Set<McmeEntity> entities) {
        EntityAPI.removeEntity(entities);
        return 0;
    }

    private int showSelection(McmeCommandSender sender) {
        sender.sendMessage(new ComponentBuilder("Mcme - Entities - Selection:").create());
        ((BukkitCommandSender)sender).getSelectedEntities().forEach(entity -> {
            sender.sendMessage(new ComponentBuilder(entity.getEntityId()+" "+entity.getName()+" "
                               +entity.getLocation().getBlockX()+" "+entity.getLocation().getBlockY()+" "+entity.getLocation().getBlockZ()).create());
        });
        return 0;
    }

    private int clearSelection(McmeCommandSender sender) {
        ((BukkitCommandSender)sender).clearSelection();
        sender.sendMessage(new ComponentBuilder("Entity Selection cleared").create());
        return 0;
    }

    private int animateEntity(McmeCommandSender sender, String animationId) {

        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        McmeCommandSender wrappedSender = EntityAPI.wrapCommandSender(sender);
        execute(wrappedSender, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        onTabComplete(new BukkitTabCompleteRequest(sender, String.format("%s %s", alias, Joiner.on(' ').join(args)).trim()));
        return null;
    }

}
