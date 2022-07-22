package Model;

import java.awt.*;
import java.util.ArrayList;

public class PlayerHashtable {
    public ArrayList<ArrayList<Player>> players;
    private int capacity;
    public int playerCount;

    public PlayerHashtable(int capacity) {
        this.players = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            players.add(new ArrayList<>());
        }

        this.capacity = capacity;

        this.playerCount = 0;
    }

    /***
     * Uses rolling hashing to put player into hashtable using separate chaining
     * @param player - Player to be added
     */
    public void put(Player player) {
        int index = 0;

        //Iterate over first name doing a rolling hash
        index = hash(player.getFirstName()) + hash(player.getLastName());

        //Make index within range
        index %= capacity;
        for (int i = 0; i < players.get(index).size(); i++) {
            if (players.get(index).get(i).firstName.equals(player.firstName) && players.get(index).get(i).lastName.equals(player.lastName) &&
                    players.get(index).get(i).jersey == player.jersey && players.get(index).get(i).team.equals(player.team)) {
                players.get(index).set(i, player);
                return;
            }
        }
        players.get(index).add(player);
        playerCount++;
    }

    public ArrayList<Player> search(String firstName, String lastName) {
        int index = (hash(firstName) + hash(lastName)) % capacity;
        return players.get(index);
    }

    private int hash(String string) {
        int result = 0;
        for (int i = 0; i < string.length(); i++) {
            result += string.charAt(i) * (string.length() - i);
        }

        return result;
    }

    public Player search(String id) {
        for (ArrayList<Player> player : players) {
            for (int j = 0; j < player.size(); j++) {
                if (player.get(j).id == Integer.parseInt(id)) {
                    return player.get(j);
                }
            }
        }
        return null;
    }

    /***
     * Removes specified player
     * @param firstName - Player's first name
     * @param lastName - Player's last name
     * @param jerseyNum - Player's jersey #
     * @param college - Player's college they play for
     */
    public void remove(String firstName, String lastName, int jerseyNum, String college) {
        int index = hash(firstName) + hash(lastName);
        for (int i = 0; i < players.get(index).size(); i++) {
            if (players.get(index).get(i).firstName.equals(firstName) && players.get(index).get(i).lastName.equals(lastName) &&
                    players.get(index).get(i).jersey == jerseyNum && players.get(index).get(i).team.equals(college)) {
                players.get(index).remove(i);
                break;
            }
        }
    }
}
