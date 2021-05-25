package org.vaadin.example;
import java.io.IOException;
import java.util.HashMap;

public class Engine extends Utils {
    protected String osSelection;
    protected String typeOfFile;
    protected String buildNumber;
    protected Utils utill = new Utils();
    private final HashMap<String, String> buildPath = new HashMap<String, String>();
    String finalPath;


    public String initiateDownload() {
        return finalPath;

    }

    protected void pathFoundation(HashMap<String,String> builds, String buildNumber, String osSelection, String locale, String fileType, String fileName) throws IOException {

        String archiveLink = "https://archive.mozilla.org/pub/firefox/";

        if(builds.get("betaVersion") != null){
            System.out.println("Beta Not Null");
            buildPath.put("betaPath", archiveLink + "candidates/" + builds.get("betaVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("betaVersion"),fileType,osSelection));
            finalPath = buildPath.get("betaPath");
            System.out.println(finalPath);


        }else if(builds.get("releaseVersion") != null) {
            System.out.println("Release is not null!");
            buildPath.put("releasePath", archiveLink + "candidates/" + builds.get("releaseVersion") + "-candidates/" + buildNumber + "/" + osSelection + "/" + locale + "/" + installerPathBuilder(builds.get("releaseVersion"), fileType, osSelection));
            finalPath = buildPath.get("releasePath");
            System.out.println(finalPath);
        }else if(builds.get("esrVersion") != null){
            System.out.println("ESR Not Null");
            buildPath.put("esrPath", archiveLink + "candidates/" + builds.get("esrVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("esrVersion"),fileType,osSelection));
            finalPath = buildPath.get("esrPath");
            System.out.println(finalPath);

        } else if(builds.get("devedVersion") != null){
            System.out.println("Deved Not Null");
            String archivesLinkDevEd = "https://archive.mozilla.org/pub/devedition/";
            buildPath.put("devedPath", archivesLinkDevEd + "candidates/" + builds.get("devedVersion") + "-candidates/" + buildNumber +  "/" + osSelection +"/" + locale + "/" + installerPathBuilder(builds.get("devedVersion"),fileType,osSelection));
            finalPath = buildPath.get("devedPath");
            System.out.println(finalPath);
        }else {
            String latestNightlyPath = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central/";
        if(locale.contains("en-US")){
            buildPath.put("latestNightlyPath", latestNightlyPath +"firefox-" + utill.parseHtml() + "." + locale + "." + osSelection +  installerPathBuilder(builds.get("latestNightly"),fileType,osSelection));
        }else{
            String latestNightlyLocalePath = "https://archive.mozilla.org/pub/firefox/nightly/latest-mozilla-central-l10n/";
            buildPath.put("latestNightlyPath", latestNightlyLocalePath + "firefox-" + utill.parseHtml() + "." + locale + "." + osSelection + installerPathBuilder(builds.get("latestNightly"),fileType,osSelection));
        }
            finalPath = buildPath.get("latestNightlyPath");
            System.out.println(finalPath);

        }
    }

    protected String installerPathBuilder(String builds,String fileType,String osSelection){
        if(fileType.contains("Firefox Setup exe")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.exe";
            }else{
                fileOutputType = ".exe";
                return "Firefox%20Setup%20" + builds + ".exe";
            }

        }else if(fileType.contains("Firefox Setup msi")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.msi";
            }else{
                fileOutputType = ".msi";
                return "Firefox%20Setup%20" + builds + ".msi";
            }

        }else if(fileType.contains("Firefox Installer.exe")){
            if(builds.contains("Nightly")){
                return fileOutputType = ".installer.exe";
            }else{
                fileOutputType = ".exe";
                return "Firefox%20Installer.exe";
            }
        }else{
            if(builds.contains("Nightly")) {
                if(fileType.contains("dmg")) {
                    return fileOutputType = ".dmg";
                }else if(fileType.contains("pkg")) {
                    return fileOutputType = ".pkg";
                }else if(osSelection.contains("linux-x86_64") || osSelection.contains("linux-i686")){
                    return fileOutputType = ".tar.bz2";
                }else{
                    return fileOutputType = ".zip";
                }
            }else {
                if (fileType.contains("dmg")) {
                    fileOutputType = ".dmg";
                    return "Firefox%20" + builds + fileOutputType;
                } else if (fileType.contains("pkg")) {
                    fileOutputType = ".pkg";
                    return "Firefox%20" + builds + fileOutputType;
                } else {
                    if(osSelection.contains("linux-x86_64") || osSelection.contains("linux-i686") || osSelection.contains("linux")){
                        fileOutputType = ".tar.bz2";
                        return "firefox-" + builds + ".tar.bz2";
                    }else{
                        fileOutputType = ".zip";
                        return "firefox-" + builds + ".zip";
                    }
                }
            }
        }
    }


}
