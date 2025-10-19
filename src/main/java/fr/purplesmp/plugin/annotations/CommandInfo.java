package fr.purplesmp.plugin.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();                   // Nom de la commande (obligatoire)
    String[] aliases() default {};   // Aliases
    String permission() default "";  // Permission nécessaire
    String permissionMessage() default ""; // Message si pas de permission
    String usage() default "";       // Usage (ex: /hello <player>)
    String description() default ""; // Description de la commande
}
