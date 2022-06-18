package com.drace.commands.context;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;

public class ArgumentsContext {

    private String[] arguments;

    public static ArgumentsContext of(String[] arguments) {

        ArgumentsContext argumentsContext = new ArgumentsContext();

        argumentsContext.arguments = arguments;

        return argumentsContext;

    }

    public static ArgumentsContext ofCopy(String[] arguments) {

        ArgumentsContext argumentsContext = new ArgumentsContext();

        argumentsContext.arguments = Arrays.copyOfRange(arguments, 1, arguments.length);

        return argumentsContext;

    }

    public int size() {

        return arguments.length;

    }

    public String getArgument(int index) {

        return arguments[--index];

    }

    public double getArgumentAsDouble(int index) {

        return NumberUtils.toDouble(arguments[--index], Double.MIN_VALUE);

    }

    public int getArgumentAsInt(int index) {

        return NumberUtils.toInt(arguments[--index], Integer.MIN_VALUE);

    }

    public @Nullable Player getArgumentAsPlayer(int index) {

        return Bukkit.getPlayerExact(arguments[--index]);

    }

}
