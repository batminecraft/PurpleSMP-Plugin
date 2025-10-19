package fr.purplesmp.plugin.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandsInfos {
    CommandInfo[] value(); // tableau de commandes
}
