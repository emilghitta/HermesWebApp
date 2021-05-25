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
    private String nightlyReleaseNotes ="https://www.mozilla.org/en-US/firefox/nightly/notes/";
    String OS = System.getProperty("os.name");



    protected String parseHtml() throws IOException {
        Document doc = Jsoup.connect(nightlyReleaseNotes).get();
        String content = doc.location();

        String nightlyCurrentVersion = content.replaceFirst("https://www.mozilla.org/en-US/firefox/","");
        return nightlyCurrentVersion = nightlyCurrentVersion.replaceFirst("/releasenotes/","");
    }

    public void parseHTMLBuildVersion(String build, ComboBox buildNumberDrop){
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

    public String buildPathForBuildVersion(String build, TextField buildVersion){
        if(!build.contains("DevEd") || !build.contains("Latest Nightly")){
            return "https://archive.mozilla.org/pub/firefox/candidates/" + buildVersion.getValue() + "-candidates/";
        } else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/" + buildVersion.getValue() + "-candidates/";
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
