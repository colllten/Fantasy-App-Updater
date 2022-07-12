package Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class Player {
    int id;
    String firstName;
    String lastName;
    String team;
    int jersey;
    int year;
    String position;

    private static final String API_KEY = "Bearer VTB63JmasppqSnWzLNjrK+duDxjYwrSGWoo2a4z+HQjzkUeUg5cPpPNNPVz0uw6L";

    public Player(int id, String firstName, String lastName, String team, int jersey, int year, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.jersey = jersey;
        this.year = year;
        this.position = position;
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
                String schoolName = team.getSchool().replace(" ", "_");
                String jsonData = getCollegeRoster(schoolName);
                if (jsonData.equals("[]")) { //Some schools have no roster data
                    continue;
                }
                //Write roster info so I don't have to call API every time
                bw.append(jsonData).append("\n");
                JSONArray players = new JSONArray(jsonData);
                //Iterate over every player and add them to the team's roaster ArrayList
                for (int i = 0; i < players.length(); i++) {
                    count++;
                    int id;
                    String firstName;
                    String lastName;
                    String college;
                    int jersey;
                    int year;
                    String position;
                    JSONObject player = players.getJSONObject(i);

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

                    team.roster.add(new Player(id, firstName, lastName, college, jersey, year, position));
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing players to DB");
        }
        System.out.printf("Added %d players to the DB\n", count);
    }

    /***
     * Retrieves roster for specific college from CFDB
     * @param collegeName - name of college
     * @return - JSON string
     */
    private static String getCollegeRoster(String collegeName) {
        //Append college name
        String url = "https://api.collegefootballdata.com/roster?team=" + collegeName + "&year=2021";
        System.out.println("Fetching roster data for " + collegeName + " from CFDB");
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
}
