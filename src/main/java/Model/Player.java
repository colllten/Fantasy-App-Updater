package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Player {
    int id;
    String firstName;
    String lastName;
    String team;
    int jersey;
    int year;
    String position;

    public ArrayList<GameStats> playerGameStats;

    private static final String API_KEY = "Bearer VTB63JmasppqSnWzLNjrK+duDxjYwrSGWoo2a4z+HQjzkUeUg5cPpPNNPVz0uw6L";

    public static PlayerHashtable playerTable = new PlayerHashtable(7151);

    public Player(int id, String firstName, String lastName, String team, int jersey, int year, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.jersey = jersey;
        this.year = year;
        this.position = position;
        this.playerGameStats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            playerGameStats.add(new GameStats(2021, i));
        }
    }

    /***
     * Gathers all players from CFDB, puts them in their respective team roster in Java, and writes it to a file
     */
    public static void getPlayers() {
        int count = 0;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter
                ("/Users/coltenglover/Java Projects/FantasyApp/src/main/resources/Rosters.txt"))) {
            //Iterate through every college
            for (College team : College.colleges) {
                String schoolName = team.getSchool(); //Name of school to be changed to url version
                String jsonData = getCollegeRoster(schoolName); //Pull from CFDB
                if (jsonData.equals("[]")) { //Some schools have no roster data, replace the spaces with %20
                    continue;
                }
                //Write roster info so I don't have to call API every time
                bw.append(jsonData).append("\n");
                JSONArray players = new JSONArray(jsonData);
                //Iterate over every player and add them to the team's roaster ArrayList
                for (int i = 0; i < players.length(); i++) {
                    count++;
                    team.roster.add(parsePlayer(players.getJSONObject(i)));
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing players to DB");
        } catch (RuntimeException e) {

        }
        System.out.printf("Added %d players to the DB\n", count);
        filterOffenseOnly(); //Take out all defensive players
    }


    /***
     * Parses a JSONObject into a Player Object
     * @param player - JSON data containing player info
     * @return - Player with fields completed
     */
    private static Player parsePlayer(JSONObject player) {
        int id;
        String firstName;
        String lastName;
        String college;
        int jersey;
        int year;
        String position;

        try {
            id = Integer.parseInt(player.get("id").toString());
        } catch (NumberFormatException e) {
            id = -1;
        }

        try {
            firstName = player.get("first_name").toString();
        } catch (JSONException e) {
            firstName = "null";
        }

        try {
            lastName = player.get("last_name").toString();
        } catch (JSONException e) {
            lastName = "null";
        }

        try {
            college = player.get("team").toString();
        } catch (JSONException e) {
            college = "null";
        }

        try {
            jersey = Integer.parseInt(player.get("jersey").toString());
        } catch (NumberFormatException e) {
            jersey = -1;
        }

        try {
            year = Integer.parseInt(player.get("year").toString());
        } catch (NumberFormatException e) {
            year = -1;
        }

        try {
            position = player.get("position").toString();
        } catch (JSONException e) {
            position = "null";
        }

        Player player1 = new Player(id, firstName, lastName, college, jersey, year, position);
        return player1;
    }

    /***
     * Retrieves roster for specific college from CFDB
     * @param collegeName - name of college
     * @return - JSON string
     */
    private static String getCollegeRoster(String collegeName) {
        //TODO: Change year to 2022

        //Replace all troublesome characters
        collegeName = College.urlSafeName(collegeName);

        //Final url to make API call
        String url = "https://api.collegefootballdata.com/roster?team=" + collegeName + "&year=2021";

        StringBuilder input = new StringBuilder();
        try {
            URL myUrl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection)myUrl.openConnection();
            conn.setRequestProperty("Authorization", API_KEY);
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String inputLine; //Holds current line returned from API
            while ((inputLine = br.readLine()) != null) {
                input.append(inputLine);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return input.toString();
    }

    /***
     * Instead of calling CFDB everytime, I have written all teams and their rosters to their respective txt files,
     * so I can read from those and fill ArrayLists and search through data
     */
    public static void fillOfflineRosters() {
        try (BufferedReader br = new BufferedReader(new FileReader
                ("/Users/coltenglover/Java Projects/FantasyApp/src/main/resources/Rosters.txt"))) {
            //Each line in the file is associated with a team
            String line = br.readLine();
            int count = 0;
            while (line != null) {
                JSONArray players = new JSONArray(line);

                //Iterate through all players in a given team
                for (int i = 0; i < players.length(); i++) {
                    //Make Player object to add into the college roster
                    Player player = parsePlayer(players.getJSONObject(i));
                    //Iterate through all colleges to find where this player belongs
                    for (College college : College.colleges) {
                        if (college.school.equalsIgnoreCase(player.team)) {
                            college.roster.add(player);
                            count++;
                            break;
                        }
                    }
                }
                line = br.readLine();
            }
            System.out.printf("We have %d players in the DB\n", count);
            filterOffenseOnly(); //Filter out all defensive players
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    /***
     * Removes every defensive player from the roster of every team because they are not needed in
     * fantasy games
     */
    private static void filterOffenseOnly() {
        int count = 0;
        //Go through every college rosters
        for (College college : College.colleges) {
            //List for all offensive players
            ArrayList<Player> players = new ArrayList<>();
            for (Player player : college.roster) {
                if (!player.position.equals("DB") && !player.position.equals("LB") && !player.position.equals("DL")
                        && !player.position.equals("CB") && !player.position.equals("OL") && !player.position.equals("G")
                        && !player.position.equals("C") && !player.position.equals("P") && !player.position.equals("DT")
                        && !player.position.equals("OT") && !player.position.equals("S") && !player.position.equals("LS")
                        && !player.position.equals("NT") && !player.position.equals("DE")) {
                    count++;
                    players.add(player);
                    Player.playerTable.put(player);
                }
            }
            //Reset the college roster
            college.roster = players;
        }
        System.out.printf("After filtering offense only, there are %d in the DB\n", count);
    }

    public String getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTeam() {
        return team;
    }

    public int getJersey() {
        return jersey;
    }

    public int getYear() {
        return year;
    }

    public String toString() {
        return String.format("%s %s | %s | %d | %s", this.firstName, this.lastName, this.position, this.jersey, this.team);
    }
}
