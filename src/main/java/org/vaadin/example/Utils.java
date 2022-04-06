package org.vaadin.example;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils{
    protected String fileOutputType;
    protected String fileOutput;
    protected String errorMessage;
    protected String fileNN, dirNN;
    public HashMap<String, Boolean> checkBoxes = new HashMap<>();
    public List<String> buildNumberList = new ArrayList<>();
    public List<String> buildVersNumber = new ArrayList<>();
    public List<String> localeList = new ArrayList<>();
    private String nightlyReleaseNotes ="https://www.mozilla.org/en-US/firefox/nightly/notes/";
    String OS = System.getProperty("os.name");



    protected String parseHtml() throws IOException {
        Document doc = Jsoup.connect(nightlyReleaseNotes).get();
        String content = doc.location();

        String nightlyCurrentVersion = content.replaceFirst("https://www.mozilla.org/en-US/firefox/","");
        return nightlyCurrentVersion = nightlyCurrentVersion.replaceFirst("/releasenotes/","");
    }

    public void parseHTMLBuildNumber(String build, ComboBox buildNumberDrop){
        try{
            buildNumberList.clear();

            Document buildNumber = Jsoup.connect(build).timeout(4000).get();
            Elements con = buildNumber.select("a");

            String builds = con.text();
            String item[] =  builds.replace("..","").split("/");

            for(int i = 0; i <item.length; i++){
                buildNumberList.add(item[i].trim());
            }

            buildNumberDrop.setItems(buildNumberList);

        } catch (IOException ioException) {
            System.out.println("Resource not found");
        }
    }

    public void parseHTMLLocale(String build, ComboBox localeCombo) throws IOException {
        localeList.clear();

        Document buildLocale = Jsoup.connect(build).timeout(4000).get();
        Elements con = buildLocale.select("a");

        String locale = con.text();
        String item[] = locale.replace("..", "").split("/");

        for(int i =0; i <item.length;i++){
            localeList.add(item[i].trim());
        }
        localeCombo.setItems(localeList);
    }

    public void parseHtmlBuildVersion(String build, ComboBox builderVers,String channel){
        try{
            buildVersNumber.clear();
            Document buildVersion = Jsoup.connect(build).timeout(4000).get();
            Elements con = buildVersion.select("a[href*='candidates']:not([href*='archived'])");

            String buildV = con.text();
            System.out.println(buildV);
            if(channel.contains("Beta")){
                String item[] = buildV.replace("-candidates","").split("/");

                for(int i = 0; i < item.length; i++){
                    if(item[i].contains("b")){
                        buildVersNumber.add(item[i].trim());
                    }
                }
            }else if(channel.contains("Release")){
                String item[] = buildV.replace("-candidates","").split("/");
                for(int i = 0; i < item.length; i++){
                    if(!item[i].contains("b") && !item[i].contains("esr") && !item[i].contains("archived")){
                        buildVersNumber.add(item[i].trim());
                    }
                }
            }else if(channel.contains("ESR")){
                buildV = buildV.replace("..","");
                String item[] = buildV.replace("-candidates","").split("/");

                for(int i = 0; i < item.length; i++){
                    if(item[i].contains("esr")){
                        buildVersNumber.add(item[i].trim());
                    }
                }
            }else if(channel.contains("DevEd")){
                buildV = buildV.replace("..","");
                String item[] = buildV.replace("-candidates","").split("/");

                for(int i = 0; i < item.length;i++){
                    buildVersNumber.add(item[i].trim());
                }
            }
            builderVers.setItems(buildVersNumber);

        }catch (IOException ioException) {
            System.out.println("Resource not found");
        }
    }

    public String buildPathForBuildNumber(String build, String buildVersion){
        if(!build.contains("DevEd") && !build.contains("Latest Nightly")){
            return "https://archive.mozilla.org/pub/firefox/candidates/" + buildVersion + "-candidates/";
        } else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/" + buildVersion + "-candidates/";
        } else{
            return "";
        }
    }

    public String buildPathForBuildVersion(String build){
        System.out.println(build);
        if(!build.contains("DevEd") && !build.contains("Latest Nightly")){
            System.out.println("Returned this");
            return "https://archive.mozilla.org/pub/firefox/candidates/";
        }else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/";
        }else{
            return "";
        }
    }

    public String buildPathForLocale(String build, String buildVersion, String buildNumber, String osVersion){
        if(!build.contains("DevEd") && !build.contains("Latest Nightly")){
            return "https://archive.mozilla.org/pub/firefox/candidates/" + buildVersion + "-candidates/" + buildNumber + "/" + osVersion + "/";
        } else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/" + buildVersion + "-candidates/"+ buildNumber + "/" + osVersion + "/";
        } else{
            return "";
        }
    }

    public boolean checkForInvalidString(String toVerify, String invalidCharacter){
        return toVerify.contains(invalidCharacter);
    }

    protected String osCheck(){
        if(OS.contains("Mac OS X") || OS.contains("Linux")){
            return "/";
        }else{
            return "\\";
        }
    }


}
