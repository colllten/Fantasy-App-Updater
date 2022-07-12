package Model;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class College {
    int id;
    String school;
    String mascot;
    String abbrv;
    String conference;

    ArrayList<Player> roster = new ArrayList<>();
    public static ArrayList<College> colleges = new ArrayList<>(120); //There are at least 130 colleges

    public College(int id, String school, String mascot, String abbrv, String conference) {
        this.id = id;
        this.school = school;
        this.mascot = mascot;
        this.abbrv = abbrv;
        this.conference = conference;
    }

    /***
     * Iterates through all the teams that CFDB returned and puts them into a static array to iterate through for
     * Firestore updates
     */
    public static void fillCollegeList() {
        System.out.println("Filling College List");
        try {
            BufferedReader br = new BufferedReader(new FileReader
                    ("/Users/coltenglover/Java Projects/FantasyApp/src/main/resources/FBSTeams.txt"));
            String jsonData = br.readLine();
            JSONArray teamsArray = new JSONArray(jsonData); //CFDB returns an array

            //Create Colleges to stick into the ArrayList
            for (int i = 0; i < teamsArray.length(); i++) {
                //Driver.pb.step();
                JSONObject team = teamsArray.getJSONObject(i);
                colleges.add(new College(1, team.getString("school"), team.getString("mascot"),
                        team.getString("abbreviation"), team.getString("conference")));
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File was not found while trying to fill the college ArrayList");
        } catch (IOException e) {
            System.out.println("ERROR occurred while reading from FBSTeams.txt to fill the Colleges ArrayList");
        }
    }

    public int getId() {
        return id;
    }

    public String getSchool() {
        return school;
    }

    public String getMascot() {
        return mascot;
    }

    public String getAbbrv() {
        return abbrv;
    }

    public String getConference() {
        return conference;
    }

    public ArrayList<Player> getRoster() {
        return roster;
    }
}