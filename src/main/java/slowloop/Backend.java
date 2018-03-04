package slowloop;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Backend {

    public HashMap<String,String> news = new HashMap<String, String>();
    public String wins;
    public String losses, draws;
    public Document document, document1, documentWiki;
    public String opponent;
    public String name, boxer, nationality, bio, fight;
    public StringBuilder completeName;
    public boolean wikiConnected = true;

    //Properly capitalize the boxer's name.
    public String boxerNameCapitalized (String boxer){

        boolean underscore = false;
        String boxerName = boxer.substring(0, 1).toUpperCase() + boxer.substring(1);
        String boxerFullName = boxerName.replace(" ", "_");
        completeName = new StringBuilder();

        try{
            //Properly capitalize surnames that start with De (examples: DeMarco, DeSantos, DeAngelo).
            if(Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+1)).equalsIgnoreCase("d")
                    && Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+2)).equalsIgnoreCase("e")
                    && !(Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+3)).equalsIgnoreCase("_"))
                    && !(Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+3)).equalsIgnoreCase("l"))
                    && !(Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+5)).equalsIgnoreCase("_"))
                    || Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+1)).equalsIgnoreCase("m")
                    && Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+2)).equalsIgnoreCase("c")
                    && !(Character.toString(boxerFullName.charAt(boxerFullName.indexOf("_")+3)).equalsIgnoreCase("_"))) {

                int count = 0;

                for (char letter : boxerFullName.toCharArray()) {

                    if (Character.toString(letter).equals("_") || Character.toString(letter).equals("'")) {
                        underscore = true;
                        completeName.append(Character.toString(letter));
                        continue;
                    }
                    if (underscore == true) {
                        completeName.append(Character.toString(letter).toUpperCase());
                        underscore = false;
                        continue;
                    }
                    if (count != 0 && (count + 2) != boxerFullName.indexOf(boxerFullName.charAt(boxerFullName.indexOf("_"))) + 3) {

                        completeName.append(Character.toString(letter).toLowerCase());
                    }
                    if ((count + 2) == boxerFullName.indexOf(boxerFullName.charAt(boxerFullName.indexOf("_"))) + 3) {

                        completeName.append(Character.toString(letter).toUpperCase());
                    } else if (count == 0) {
                        completeName.append(letter);
                    }
                    count++;
                }
            }
            //Properly capitalize names that start with Mc.
            else {

                int count = 0;

                for (char letter : boxerFullName.toCharArray()) {

                    if (Character.toString(letter).equals("_") || Character.toString(letter).equals("'")) {
                        underscore = true;
                        completeName.append(Character.toString(letter));
                        continue;
                    }
                    if(count == 2 && boxerFullName.contains("Mc")){
                        completeName.append(Character.toString(letter).toUpperCase());
                        count++;
                        continue;
                    }
                    if (underscore == true) {
                        completeName.append(Character.toString(letter).toUpperCase());
                        underscore = false;
                        continue;
                    }if(count != 0){
                        completeName.append(Character.toString(letter).toLowerCase());
                    }
                    else if(count == 0){
                        completeName.append(letter);
                    }
                    count++;
                }
            }
        }catch(Exception e){
        }

        return completeName.toString();
    }

    //Get the boxer's name from either Boxrec or Wikipedia.
    public void boxerName (){
        boxer = "";

        try{
            //Get boxer's name from Wikipedia.
            Element wiki = documentWiki.select("title").first();

            boxer = wiki.text().substring(0, wiki.text().indexOf(" -")).replace(" (boxer)", "");

            if(boxer.contains("(")){
                boxer = boxer.substring(0, boxer.indexOf("(")-1);
            }

        }catch(Exception e){

            //Get boxer's name from Boxrec page if unable to get it from Wikipedia.
            Element boxerName = document1.select("p").first();

            for(char letter : completeName.toString().replace("_", " ").toCharArray()){

                if(boxerName.text().contains(Character.toString(letter))){
                    boxer += letter;
                }
            }
        }
    }

    //Get the boxer's country of birth from Wikipedia.
    public void wikiNation(){
        Element wiki = documentWiki.select("table.infobox").first();
        Elements nationality1 = wiki.select("tr");

        try{
            for (Element element : nationality1) {

                if (element.text().contains("Born")) {

                    if (element.text().contains("Soviet Union")) {

                        nationality = element.text().substring(0, element.text().lastIndexOf(", S"));
                        nationality = nationality.substring(nationality.lastIndexOf(",") + 2, nationality.length())
                                .replace("-SSR", "").replace(" SSR", "").replace("-SFSR", "")
                                .replace(" SFSR", "").replace("-ASSR", "").replace(" ASSR", "");

                    } else {
                        if (element.text().contains(",")) {
                            nationality = element.text().substring(element.text().lastIndexOf(", ") + 1, element.text().length()).replace(")", "").replaceFirst(" ", "");
                        } else {
                            nationality = element.text().substring(element.text().lastIndexOf(" ") + 1, element.text().length()).replace(")", "").replaceFirst(" ", "");
                        }
                    }
                }
            }
        }catch(Exception e){
        }
    }

    //Get the boxer's country of birth from either Boxrec or Wikipedia.
    public void nation (){
        nationality = "";

        try {
            //Get boxer's country of birth from Boxrec.
            String birthplace;
            Element country = document1.select("p").first();

            if (country.text().contains("Birthplace")) {

                String birth = country.text().substring(country.text().indexOf("Birthplace:") + 12);
                birthplace = birth.substring(0, birth.indexOf(":"));

                if(birthplace.contains(") ")){

                    birthplace = birthplace.substring(0, birthplace.indexOf("(")) + birthplace.substring(birthplace.indexOf(")")+2, birthplace.length());
                }

                if(birthplace.contains(",")){
                    nationality = birthplace.substring(birthplace.lastIndexOf(",") + 2, birthplace.lastIndexOf(" "));
                }
                else{
                    nationality = birthplace.substring(0, birthplace.lastIndexOf(" "));
                }
            }
            else if(country.text().contains("Hometown")){

                String birth = country.text().substring(country.text().indexOf("Hometown:") + 10);
                birthplace = birth.substring(0, birth.indexOf(":"));

                if(birthplace.contains(") ")){

                    birthplace = birthplace.substring(0, birthplace.indexOf("(")) + birthplace.substring(birthplace.indexOf(")")+2, birthplace.length());
                }

                if(birthplace.contains(",")){
                    nationality = birthplace.substring(birthplace.lastIndexOf(",") + 2, birthplace.lastIndexOf(" "));
                }
                else{
                    nationality = birthplace.substring(0, birthplace.lastIndexOf(" "));
                }
            }
            else {
                //Get boxer's country of birth from Wikipedia if unable to find it on their Boxrec page.
                wikiNation();
            }
        }catch(Exception e){
            try{
                //Get boxer's country of birth from Wikipedia if the boxer's Boxrec page can't be found.
                wikiNation();

            }catch(Exception a){
                nationality = "";
            }
        }
    }

    //Grabs a biography paragraph of the boxer from Wikipedia. Shortens the paragraph and edits out unnecessary brackets.
    public void biography(){
        bio = "";

        try{
            if(wikiConnected == false){
                //if there is no Wiki page for the boxer then leave bio blank.
                bio = "";

            }else{
                int i = 0, count = 0, position= 0;
                boolean found = false, foundSuper = false;
                Elements intro = documentWiki.select("p");
                String biography = "";

                //Grab the first relevant paragraph from the boxer's Wikipedia page.
                for(Element element : intro){

                    if(element.text().contains(boxer.substring(0, boxer.indexOf(" "))) && found == false
                            || element.text().contains(boxer.substring(boxer.lastIndexOf(" ")+1, boxer.length())) && found == false
                            || element.text().contains(completeName.substring(0, completeName.indexOf("_"))) && found == false
                            || element.text().contains(completeName.substring(completeName.lastIndexOf("_") + 1, completeName.length())) && found == false){

                        if(!(element.text().contains("(") && element.text().contains(")"))){
                            biography = element.text();
                            found = true;
                        }
                        else{
                            biography = element.text().substring(0, element.text().indexOf(" (")) + element.text().substring(element.text().indexOf(")")+1, element.text().length());
                            found = true;
                        }
                    }
                }

                found = false;

                //Remove any round brackets with a few words inside of them. Ignore round brackets with single (capitalized) words like (Super) or (Unified).
                for(char bracket : biography.toCharArray()){

                    if(Character.toString(bracket).equalsIgnoreCase("(")
                            && (Character.toString(biography.charAt(biography.indexOf(bracket)+1)).matches("[[\\p{L}&&[^\\p{Lu}]]]"))){

                        position = biography.indexOf(bracket);
                        found = true;
                    }
                    if(Character.toString(bracket).equalsIgnoreCase("(")
                            && (Character.toString(biography.charAt(biography.indexOf(bracket)+1)).matches("\\p{Lu}"))){

                        foundSuper = true;
                    }
                    if(found == true && Character.toString(bracket).equalsIgnoreCase(")")
                            || found == false && foundSuper == false && Character.toString(bracket).equalsIgnoreCase(")")){

                        //Fix a specific case where the paragraph has a closing bracket but not an opening one (and the opening bracket should have been located before
                        //a certain word).
                        if(found == false && foundSuper == false && biography.substring(0, biography.indexOf(")")).contains("born")){

                            String first;
                            String last;

                            if((biography.substring(biography.indexOf("born")-2, biography.indexOf("born")-1).equals(","))){

                                first = biography.substring(0, biography.indexOf("born")-2);
                            }
                            else{
                                first = biography.substring(0, biography.indexOf("born")-1);
                            }
                            if((biography.substring(biography.indexOf(")")+1, biography.indexOf(")")+2)).equals(",")){

                                last = biography.substring(biography.indexOf(")")+3, biography.length());
                            }
                            else{
                                last = biography.substring(biography.indexOf(")")+2, biography.length());
                            }

                            biography = first + " " + last;
                        }
                        else if(found == false && foundSuper == false && !(biography.substring(0, biography.indexOf(")")).contains("born"))){

                            biography = biography.substring(0, biography.indexOf(bracket)) + biography.substring(biography.indexOf(bracket)+1, biography.length());
                        }
                        else{
                            if(Character.toString(biography.charAt(biography.indexOf(bracket) + 1)).equalsIgnoreCase(",")){

                                biography = biography.substring(0, position - 1) +  biography.substring(biography.indexOf(bracket) + 1, biography.length());
                            }
                            else{
                                biography = biography.substring(0, position) + biography.substring(biography.indexOf(bracket) + 2, biography.length());
                            }
                        }

                        found = false;
                    }
                    if(Character.toString(bracket).equalsIgnoreCase(")") && foundSuper == true){
                        foundSuper = false;
                    }
                }

                //Remove reference links (square brackets with a number in them) from the paragraph.
                biography = biography.replaceAll("\\[[0-9]+\\]", "");

                //Shorten the grabbed Wiki bio paragraph.
                String shortBio = "";

                for(char letter : biography.toCharArray()){
                    count++;

                    if(Character.toString(letter).contains(".") && count > 100){
                        i++;
                    }
                    else if(Character.toString(letter).contains(".") && count < 100 && count == biography.length()){
                        i++;
                    }
                    if(!(i > 0)){
                        shortBio += letter;
                    }
                }

                String compareBio = shortBio;

                //If length of the bio is greater than 190 characters then shorten it further.
                if(shortBio.length() > 190){
                    boolean end = false;

                    //Shorten the bio length to the last comma. Loop will end if the bio goes below 190 characters. The loop will also end if
                    //it's unable to get the bio below 190 characters.
                    while (end == false){

                        if(!(shortBio.contains(",")) && shortBio.length() > 190){

                            shortBio = shortBio.substring(0, shortBio.lastIndexOf("."));
                            end = true;
                        }
                        else if(shortBio.contains(",") && shortBio.length() > 190){
                            shortBio = shortBio.substring(0, shortBio.lastIndexOf(","));
                        }
                        else if(shortBio.length() < 190){
                            end = true;
                        }
                    }

                    bio = shortBio + ".";

                    //Find the second last comma of a sentence.
                    int add = 0;
                    int comma = 0;

                    if(shortBio.contains(",")){
                        add = 1;
                        boolean foundComma = false;

                        for(int a = 0; a < shortBio.length();a++){

                            if(Character.toString(shortBio.charAt((shortBio.length() - 1) - a)).equalsIgnoreCase(",") && foundComma == false){
                                comma = a;
                                foundComma = true;
                            }
                        }
                    }
                    else{
                        add = 0;
                    }

                    //If the shortened bio ends at a comma that comes after a very brief introductory phrase (or a phrase with just a few words)
                    //then fix the bio so that it ends at a better spot.
                    if(shortBio.substring((shortBio.length() - comma) + add, shortBio.length()).length() < 33
                            && !(shortBio.substring((shortBio.length() - comma) + add, shortBio.length()).length() == 0)
                            || shortBio.substring(shortBio.lastIndexOf(".")+2, shortBio.length()).length() < 33
                            || shortBio.length() < 33){

                        if(shortBio.contains(".") && shortBio.substring(shortBio.lastIndexOf(".") + 2, shortBio.length()).contains(",")
                               || shortBio.contains(".") && !(shortBio.substring(shortBio.lastIndexOf(".") + 2, shortBio.length()).contains(","))
                                && shortBio.substring(shortBio.lastIndexOf(".")+2, shortBio.length()).length() < 33){

                            boolean endLoop = false;

                            for(int a = 0; a < shortBio.length();a++){

                                if(Character.toString(shortBio.charAt((shortBio.length() - 1) - a)).equalsIgnoreCase(",") && endLoop == false
                                        || Character.toString(shortBio.charAt((shortBio.length() - 1) - a)).equalsIgnoreCase(".") && endLoop == false){

                                    if(a < 35){
                                        if(Character.toString(shortBio.charAt((shortBio.length() - 1) - a)).equalsIgnoreCase(".")){
                                            bio = shortBio.substring(0, shortBio.length()-a);
                                            endLoop = true;
                                        }
                                        else{
                                            shortBio = shortBio.substring(0, (shortBio.length() - 1) - a);
                                        }
                                    }
                                    else{
                                        bio = shortBio + ".";
                                    }
                                }
                            }
                        }
                        else{
                            for(char letter : compareBio.substring(shortBio.length()+1, compareBio.length()).toCharArray()){

                                shortBio += letter;
                            }
                            bio = shortBio + ".";

                            if(shortBio.length() > 200){

                                bio = shortBio.substring(0, shortBio.indexOf(".")) + ".";
                            }
                        }
                    }
                }
                else{
                    shortBio = shortBio.replaceAll("\\[[0-9]+\\]", "");

                    if(shortBio.length() == 0){
                        bio = shortBio;
                    }else{
                        if(shortBio.endsWith(".")){
                            bio = shortBio;
                        }
                        else{
                            bio = shortBio + ".";
                        }
                    }
                }
            }
        }catch (Exception e){
        }
    }

    //Grabs the boxer's fight record and next scheduled fight from either Boxrec or Wikipedia.
    public void fightRecordAndSchedule(){
        this.wins = "";
        this.losses = "";
        this.draws = "";
        this.opponent = "";
        this.fight = "";

        try{
            //Get boxer's wins, losses, and draws from Boxrec.
            Elements wins = document.select("td[class=bgW]");
            Element ko = document.select("th[class=textWon]").first();
            Elements losses = document.select("td[class=bgL]");
            Elements draws = document.select("td[class=bgD]");

            this.wins = wins.text() + " " + "(" + ko.text() + ")";
            this.losses = losses.text();
            this.draws = draws.text();

            //Get boxer's next fight from Boxrec.
            if(document.select("table[border]").select("td[valign=center]").select("div.boutResult").first().text().equalsIgnoreCase("s")){

                Element date = document.select("table").select("a[href*=/en/date?date=]").first();
                fight = "Date: " + date.text();

                if(!(document.select("table").select("td[valign=center]").select("td:contains(TBA)").text().equalsIgnoreCase("TBA"))){
                    Element opponentBoxer = document.select("table").select("td[valign=center]").select("a[href*=/en/boxer/]").first();
                    opponent = opponentBoxer.text();

                }else{ opponent = "TBA"; }

            }else{ fight = "No scheduled fight."; }

        }catch(Exception e){
            //Get boxer's wins, losses, and draws from Wikipedia in case Boxrec website is unavailable.
            if(wikiConnected == false){

                //If there is no Wikipedia page for the boxer then leave the fighter's record n/a
                fight = "Date: n/a";
                opponent = "n/a";
                wins = "n/a";
                losses = "n/a";
                draws = "n/a";

            }else{
                try{
                    Element wiki = documentWiki.select("table.infobox").first();

                    fight = "Date: n/a";
                    opponent = "n/a";

                    if(!(wiki.text().contains("Total"))) {

                        wins = "n/a";
                        losses = "n/a";
                        draws = "n/a";
                    }
                    else{
                        String record;

                        if(wiki.text().contains("Total Fights")){
                            record = wiki.text().substring(wiki.text().indexOf("Total fights")+13);
                        }else{
                            record = wiki.text().substring(wiki.text().indexOf("Total")+8);
                        }

                        if(record.contains("Draws")){
                            String boxerWins = record.substring(record.indexOf("Wins") + 5, record.length());
                            String boxerDraws = record.substring(record.indexOf("Draws") + 6, record.length());

                            if(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 3).equalsIgnoreCase("By")){

                                wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") +
                                        " (" + boxerWins.substring(boxerWins.indexOf("By knockout") + 11, boxerWins.indexOf("Losses")).replace(" ", "") +" KOs)";

                            }else if(record.contains("Wins by KO")){

                                wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") +
                                        " (" + boxerWins.substring(boxerWins.indexOf("Wins by KO") + 11, boxerWins.indexOf("Losses")).replace(" ", "") +" KOs)";
                            }
                            else if(!(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 3).equalsIgnoreCase("By"))
                                    || !(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 5).equalsIgnoreCase("Wins"))){

                                wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") + " (0 KOs)";
                            }

                            losses = boxerWins.substring(boxerWins.indexOf("Losses") + 6, boxerWins.indexOf("Draws")).replace(" ", "");

                            if(boxerDraws.length() < 3){
                                draws = boxerDraws;
                            }
                            else{
                                draws = boxerDraws.substring(0, boxerDraws.indexOf(" ")).replace(" ", "");
                            }
                        }
                        else{
                            String boxerWins = record.substring(record.indexOf("Wins") + 5, record.length());
                            String boxerLosses = record.substring(record.indexOf("Losses") + 7, record.length());

                            if(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 3).equalsIgnoreCase("By")){

                               wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") +
                                            " (" + boxerWins.substring(boxerWins.indexOf("By knockout") + 11, boxerWins.indexOf("Losses")).replace(" ", "") +" KOs)";

                            }else if(record.contains("Wins by KO")){

                                wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") +
                                        " (" + boxerWins.substring(boxerWins.indexOf("Wins by KO") + 11, boxerWins.indexOf("Losses")).replace(" ", "") +" KOs)";
                            }
                            else if(!(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 3).equalsIgnoreCase("By"))
                                    || !(boxerWins.substring(boxerWins.indexOf(" ")+1, boxerWins.indexOf(" ") + 5).equalsIgnoreCase("Wins"))){

                                wins = boxerWins.substring(0, boxerWins.indexOf(" ")).replace(" ", "") + " (0 KOs)";
                            }

                            draws = "0";

                            if(boxerLosses.length() < 3){
                                losses = boxerLosses;
                            }
                            else{
                                losses = boxerLosses.substring(0, boxerLosses.indexOf(" ")).replace(" ", "");
                            }
                        }
                    }
                }catch(Exception a){
                    //If the boxer's wiki page has no info box (to display fight record) then wins, losses and draws return "n/a".
                    wins = "n/a";
                    losses = "n/a";
                    draws = "n/a";
                }

                //Get the fighter's next opponent and fight date from Wikipedia (If available).
                try{
                    Elements wiki = documentWiki.select("table.wikitable").select("td");
                    Elements wiki1 = documentWiki.select("table.wikitable").select("tbody").select("tr");

                    int count = 0;
                    boolean stop = false, stopAgain = false, foundOpponent = false;

                    if(!(wiki.text().contains("N/A"))){

                        if(!(wiki1.text().contains("Opponent"))){

                            fight = "Date: n/a";
                            opponent = "n/a";
                        }
                        else{
                            fight = "No scheduled fight.";
                        }
                    }
                    else{
                        for(Element element : wiki){
                            count++;

                            if(element.text().contains("N/A") && stop == false && count < 17){

                                opponent = wiki.get(count).text();
                                stop = true;

                            }
                            //If no opponent/upcoming fight scheduled despite "N/A" being found.
                            else if(element.text().contains("N/A") && stop == false && count > 17){

                                fight = "No scheduled fight.";
                            }

                            //If an opponent is found/there is a fight scheduled then find the fight date
                            if((element.text().matches("[a-zA-Z]{3}\\W\\p{Digit}+\\p{Punct}\\W\\p{Digit}{4}")
                                    || element.text().matches("\\p{Digit}{4}\\p{Punct}\\p{Digit}{2}\\p{Punct}\\p{Digit}{2}")
                                    || element.text().matches("[a-zA-Z]{3}\\W\\p{Digit}+\\W\\p{Digit}{4}") || element.text().matches("\\p{Digit}+\\W[a-zA-Z]{3}\\W\\p{Digit}{4}")
                                    || element.text().matches("[a-zA-Z]{3}\\p{Punct}\\W\\p{Digit}+\\W\\p{Digit}{4}")) && stopAgain == false && stop == true){

                                String year;
                                String month;
                                String day;

                                if(element.text().matches("\\p{Digit}{4}\\p{Punct}\\p{Digit}{2}\\p{Punct}\\p{Digit}{2}")){

                                    List<String> months = new ArrayList<String>();

                                    Collections.addAll(months, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec");

                                    if(element.text().substring(5,7).startsWith("0")){
                                        month = element.text().substring(6,7);
                                    }
                                    else{
                                        month = element.text().substring(5,7);
                                    }
                                    year = element.text().substring(0, 4);
                                    day = element.text().substring(element.text().lastIndexOf("-")+1,element.text().length()) + ", ";

                                    if(day.startsWith("0")){

                                        day = day.substring(1, day.length());
                                    }

                                    fight = "Date: " + months.get(Integer.parseInt(month)-1) + " " + day + year;
                                    stopAgain = true;
                                }
                                else if(element.text().matches("[a-zA-Z]{3}\\W\\p{Digit}+\\W\\p{Digit}{4}")){

                                    fight = "Date: " + element.text().substring(0, element.text().lastIndexOf(" ")) + "," + element.text()
                                            .substring(element.text().lastIndexOf(" "), element.text().length());
                                    stopAgain = true;
                                }
                                else if(element.text().matches("\\p{Digit}+\\W[a-zA-Z]{3}\\W\\p{Digit}{4}")){

                                    year = element.text().substring(element.text().lastIndexOf(" "), element.text().length());
                                    month = element.text().substring(element.text().indexOf(" "),element.text().indexOf(" ") + 4) + " ";
                                    day = element.text().substring(0,element.text().indexOf(" ")) + ",";

                                    fight = "Date:" + month + day + year;
                                    stopAgain = true;
                                }
                                else if(element.text().matches("[a-zA-Z]{3}\\p{Punct}\\W\\p{Digit}+\\W\\p{Digit}{4}")){

                                    String comma = element.text().substring(3,4);
                                    year = element.text().substring(element.text().lastIndexOf(" "), element.text().length());
                                    month = element.text().substring(0,3);
                                    day = element.text().substring(element.text().indexOf(" "), element.text().lastIndexOf(" "));

                                    fight = "Date: " + month + day + comma + year;
                                    stopAgain = true;
                                }
                                else{
                                    fight = "Date: " + wiki.get(count-1).text();
                                    stopAgain = true;
                                }
                            }
                        }
                    }
                }
                catch(Exception a){
                    fight = "Date: n/a";
                    opponent = "n/a";
                }
            }
        }
    }

    //Connect to the websites where the boxer's information will be grabbed from. Also call all the methods used to grab the information.
    public void connect (String name) {

        //Connect to Boxrec page.
        try{
            if(name.endsWith(".")){
                try{
                    document1 = Jsoup.connect("http://boxrec.com/media/index.php/" + URLEncoder.encode(boxerNameCapitalized(name.substring(0, name.length() - 1)), "UTF8")).get();
                }
                catch(Exception a){
                    document1 = Jsoup.connect("http://boxrec.com/media/index.php/" + URLEncoder.encode(boxerNameCapitalized(name), "UTF8")).get();
                }
            }
            else{
                document1 = Jsoup.connect("http://boxrec.com/media/index.php/" + URLEncoder.encode(boxerNameCapitalized(name), "UTF8")).get();
            }

            Elements url = document1.select("a[href]");

            for(Element line : url){
                if(line.outerHtml().contains("http://boxrec.com/en/boxer/")){
                    document = Jsoup.connect(line.attr("abs:href")).get();
                }
            }
        }
        catch(Exception e){
            try{
                if(boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_")+1, boxerNameCapitalized(name).lastIndexOf("_")+3).equals("De")
                        && boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_")+3, boxerNameCapitalized(name)
                        .lastIndexOf("_")+4).matches("\\p{Lu}{1}")) {

                    String boxer = boxerNameCapitalized(name).substring(0, boxerNameCapitalized(name).lastIndexOf("_") + 3) +
                            boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_") + 3, boxerNameCapitalized(name).length()).toLowerCase();

                    documentWiki = Jsoup.connect("http://boxrec.com/media/index.php/" + URLEncoder.encode(boxer, "UTF8")).get();
                }
            }catch(Exception a){
            }
        }

        //Connect to Wikipedia page.
        try{
            documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxerNameCapitalized(name), "UTF8")).get();

            if(documentWiki.text().contains("may refer to:") || documentWiki.text().contains("is the name of:") || !((documentWiki.select("p").text()).contains("boxer"))){

                documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxerNameCapitalized(name), "UTF8") + "_(boxer)").get();
            }
        }
        catch(Exception e){

            try{
                if(boxerNameCapitalized(name).contains("Jr") && !(boxerNameCapitalized(name).endsWith("."))){

                    documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxerNameCapitalized(name) + ".", "UTF8")).get();

                    if(documentWiki.text().contains("may refer to:") || documentWiki.text().contains("is the name of:") || !((documentWiki.select("p").text()).contains("boxer"))){

                        documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxerNameCapitalized(name) + ".", "UTF8") + "_(boxer)").get();
                    }
                }
                else if(boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_")+1, boxerNameCapitalized(name).lastIndexOf("_")+3).equals("De")
                        && boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_")+3, boxerNameCapitalized(name)
                        .lastIndexOf("_")+4).matches("\\p{Lu}{1}")){

                    String boxer = boxerNameCapitalized(name).substring(0, boxerNameCapitalized(name).lastIndexOf("_")+3) +
                            boxerNameCapitalized(name).substring(boxerNameCapitalized(name).lastIndexOf("_")+3, boxerNameCapitalized(name).length()).toLowerCase();

                    documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxer, "UTF8")).get();

                    if(documentWiki.text().contains("may refer to:") || documentWiki.text().contains("is the name of:") || !((documentWiki.select("p").text()).contains("boxer"))){

                        documentWiki = Jsoup.connect("https://en.wikipedia.org/wiki/" + URLEncoder.encode(boxer, "UTF8") + "_(boxer)").get();
                    }
                }
                else{
                    wikiConnected = false;
                }
            }
            catch(Exception a){
                wikiConnected = false;
            }
        }

        //Call methods
        boxerName();
        nation();
        biography();
        fightRecordAndSchedule();
    }

    //Grab the latest news articles related to the boxer. 5 articles or less. Article titles and their corresponding urls
    //are stored in a HashMap.
    public HashMap<String, String> findArticles (String bName) throws IOException {

        name = bName.replace("_", "+");

        try{
            Document document1 = Jsoup.connect("https://www.badlefthook.com/search?order=date&q=" +  name + "&type=Article").get();
            Elements articles = document1.select("h2[class=c-entry-box--compact__title]").select("a");

            for(Element element : articles){
                if(news.size() != 5){
                    news.put(element.text(), element.attr("href"));
                }
            }
        }
        catch(Exception e){ }

        return news;
    }
}
