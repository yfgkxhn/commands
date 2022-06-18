package com.drace.commands;

import com.drace.commands.annotations.PseudoCommand;
import com.drace.commands.context.ArgumentsContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class Command extends org.bukkit.command.Command {

    private final Map<String, Method> subCommands;
    @Setter private BiConsumer<CommandSender, ArgumentsContext> defaultAction;

    public Command(String name, String... aliases) {

        super(name);
        setAliases(Arrays.asList(aliases));

        subCommands = Maps.newHashMap();

        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        SimpleCommandMap simpleCommandMap = craftServer.getCommandMap();

        register(simpleCommandMap);

    }

    public void register(Class<?>... clazzes) {

        for (Class<?> clazz : clazzes) {

            for (Method method : clazz.getMethods()) {

                method.setAccessible(true);

                if (!method.isAnnotationPresent(PseudoCommand.class)) continue;

                Preconditions.checkState(Modifier.isStatic(method.getModifiers()), "method must be static.");
                Preconditions.checkState(method.getParameterCount() == 2, "method cant have more than 2 parameters.");
                Preconditions.checkState(
                        method.getParameterTypes()[0].getSimpleName().equals("Player") &&
                        method.getParameterTypes()[1].getSimpleName().contains("ArgumentsContext"),
                        "invalid method declaration.");

                PseudoCommand pseudoCommand = method.getAnnotation(PseudoCommand.class);

                for (String alias : pseudoCommand.aliases())
                    subCommands.put(alias, method);

                subCommands.put(pseudoCommand.name(), method);

            }

        }

    }

    @Override @SneakyThrows public boolean execute(CommandSender commandSender, String s, String[] arguments) {

        if (arguments.length == 0) {

            execute(commandSender, arguments);

            return false;

        }

        Method method = subCommands.get(arguments[0]);

        if (method != null) {

            method.invoke(null, commandSender, ArgumentsContext.ofCopy(arguments));
            return true;

        }

        defaultAction.accept(commandSender, ArgumentsContext.of(arguments));

        return false;
    }

    public abstract void execute(CommandSender sender, String[] arguments);

}
