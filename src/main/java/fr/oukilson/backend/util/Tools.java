package fr.oukilson.backend.util;

import fr.oukilson.backend.entity.Game;
import fr.oukilson.backend.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class Tools {


    /**
     * compares a string of characters to a given regex and returns matching status
     * @param regex string of characters that define the accepted or *not* accepted list of chars
     * @param string string of characters we wish to check the individual characters of
     * @return true if the string is valid(conform to the regex), false if it isn't
     */
    public static boolean checkRegex(String regex, String string){
        return Pattern.compile(regex).matcher(string).find();
    }


    /**
     * function checks if a user is already on a list by using its UUID
     * @param user user to check
     * @param map list of users to verify
     * @return true if user is on list, false otherwise
     */
    public static boolean userOnList(User user, Map<Long, User> map){
        return map.containsKey(user.getId());
    }

    /**
     * function checks if game is already on a list by using its UUID
     * @param game game to check
     * @param map list of games to verify
     * @return true if game is on list, false otherwise
     */
    public static boolean gameOnList(Game game, HashMap<UUID, Game> map){
        return map.containsKey(game.getUUID());
    }
}
