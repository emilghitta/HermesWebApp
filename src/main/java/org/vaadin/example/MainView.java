package org.vaadin.example;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import java.util.*;


/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@Route
@CssImport("./styles/styles.css")

public class MainView extends VerticalLayout {

    private Utils utill = new Utils();
    private Engine eng = new Engine();
    public String jsonValue;
    private String dirNN;
    private String dropdownInput;
    protected HashMap<String,String> builds = new HashMap<>();
    private List<String> channelList = new ArrayList<String>(Arrays.asList("Latest Nightly","Beta","Release","ESR","DevEd"));
    private List<String> osVersionList = new ArrayList<String>(Arrays.asList("win64","win32","win64-aarch64","mac","linux-x86_64","linux-i686"));
    private List<String> fileTypeWindows64List = new ArrayList<String>(Arrays.asList("archive","Firefox Setup exe","Firefox Setup msi"));
    private List<String> fileTypeWindows32List = new ArrayList<String>(Arrays.asList("archive","Firefox Installer.exe","Firefox Setup exe","Firefox Setup msi"));
    private List<String> fileTypeWindows32ESRList = new ArrayList<String>(Arrays.asList("archive","Firefox Setup exe","Firefox Setup msi"));
    private List<String> fileTypeWindowsArmList = new ArrayList<String>(Arrays.asList("archive","Firefox Setup exe"));
    private List<String> fileTypeLinuxList = new ArrayList<String>(Arrays.asList("archive"));
    private List<String> fileTypeMacList = new ArrayList<String>(Arrays.asList("dmg","pkg"));
    private List<String> fileTypeMacListDevEd = new ArrayList<String>(Arrays.asList("dmg"));
   protected H2 errorMessage = new H2("");
    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
    public MainView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addClassName("main-window");

        UI current = UI.getCurrent();
        current.getPage().setTitle("Hermes");

        H1 h1 = new H1("Hermes");
        h1.getElement().getThemeList().add("dark");
        add(h1);


        //Channel Selector dropdown menu
        ComboBox channelSelector = new ComboBox();
        channelSelector.isRequired();
        channelSelector.setLabel("Choose the download channel");
        channelSelector.setItems(channelList);
        add(channelSelector);

        //Version selector dropdown menu
        ComboBox buildVersion = new ComboBox();
        buildVersion.isRequired();
        buildVersion.setLabel("Specify build version");
//        buildVersion.setPlaceholder("eg. 82.0b4");
        buildVersion.setEnabled(false);
        add(buildVersion);

        //Build Number Dropdown menu
        ComboBox buildNumber = new ComboBox();
        buildNumber.isRequired();
        buildNumber.setLabel("Select build");
        buildNumber.setEnabled(false);
        add(buildNumber);

        //OS Version dropdown
        ComboBox osVersion = new ComboBox();
        osVersion.isRequired();
        osVersion.setLabel("Platform");
        osVersion.setEnabled(false);
        osVersion.setItems(osVersionList);
        add(osVersion);

        //Locale selector dropdown
        ComboBox locale = new ComboBox();
        locale.isRequired();
        locale.setLabel("Locale");
        locale.setEnabled(false);
        add(locale);

        //Installer Type dropdown menu
        ComboBox installerType = new ComboBox();
        installerType.isRequired();
        installerType.setLabel("Installer");
        installerType.setEnabled(false);
        add(installerType);

        //Download anchor
        Anchor anc = new Anchor();
        anc.setText("Download build");
        anc.setEnabled(false);
        add(anc);


        errorMessage.setVisible(false);
        add(errorMessage);

        /*
        On channel selector value change we:
        - are resetting the OS version, build version and locale values.
        - If the channel selector value is Nightly we are currently clearing version input and fetching always the latest nightly. We need to update this!
        - If the channel selector value is !Nightly we are: Enabling the VersionInput dropdwn menu, clearing the build number input and enabling the osVersion (maybe we need to change that as well).

         */
        channelSelector.addValueChangeListener(event -> {
            osVersion.setValue("");
            buildVersion.setValue("");
            locale.setValue("");

            if (channelSelector.getValue().toString().contains("Latest Nightly")) {
                buildVersion.setLabel("");
                buildVersion.setEnabled(false);
                buildNumber.clear();
                buildNumber.setEnabled(false);
                osVersion.setEnabled(true);
            } else{
                buildVersion.setEnabled(true);
                buildNumber.clear();
                osVersion.setEnabled(false);
            }

            if(channelSelector.getValue() != null){
                buildVersion.focus();
            }
        });


        /*
        Adding focus listener on Verison Numbers drodpown menu.
        - This dropdown is enabled after providing input inside the Channel Selector dropdown menu.
        - Clearing the build number and version input on focus.
        - Calling the utility parseHtmlBuildVersion method and providing the return of the utility buildPathForBuildVersion method, the required combo box and the value of
        the channel selector.
         */

       buildVersion.addFocusListener(event ->{
           buildNumber.setEnabled(true);
           buildNumber.clear();
           buildVersion.clear();
           utill.parseHtmlBuildVersion(utill.buildPathForBuildVersion(channelSelector.getValue().toString()),buildVersion,channelSelector.getValue().toString());
       });

       buildVersion.addValueChangeListener(event -> {
          if(buildVersion.getValue() != null && !buildVersion.isEnabled()){
              buildNumber.focus();
          }
       });

       /*
       Adding focus listener for the build number dropdown menu.
       - On focus we are clearing the previously entered build number
       - On focus we are calling the utility parseHTMLBuildNumber method and passing the return type of the utility buildPathForBuildNumber method and the build number combo box
        */

        buildNumber.addFocusListener(event -> {
            buildNumber.clear();
            utill.parseHTMLBuildNumber(utill.buildPathForBuildNumber((String) channelSelector.getValue(), buildVersion.getValue().toString()), buildNumber);
        });

        /*
        Adding a value change listener to enable the osVersion dropdown after providing the buildNumber
         */

        buildNumber.addValueChangeListener(event -> {
            if(buildNumber.getValue() != null && !buildNumber.isEnabled()) {

                osVersion.focus();
            }
            osVersion.setEnabled(true);

        });


        osVersion.addValueChangeListener(event -> {

            locale.setEnabled(true);
            installerType.setEnabled(true);
            //autoOpenBuilds.setEnabled(false);

            if (osVersion.getValue().toString().contains("linux-x86_64") || osVersion.getValue().toString().contains("linux-i686")){
                installerType.clear();
                installerType.setItems(fileTypeLinuxList);
            } else if (osVersion.getValue().toString().contains("mac")) {
                if (channelSelector.getValue().toString().contains("DevEd")) {
                    installerType.clear();
                    installerType.setItems(fileTypeMacListDevEd);
                } else {
                    installerType.clear();
                    installerType.setItems(fileTypeMacList);
                }
            } else if (osVersion.getValue().toString().equals("win64")) {
                installerType.clear();
                installerType.setItems(fileTypeWindows64List);
            } else if (osVersion.getValue().toString().equals("win64-aarch64")) {
                System.out.println("here aarch");
                installerType.clear();
                installerType.setItems(fileTypeWindowsArmList);
            } else {
                if(channelSelector.getValue().toString().contains("ESR")){
                    System.out.println("here 32 ESR");
                    installerType.clear();
                    installerType.setItems(fileTypeWindows32ESRList);
                }else{
                    System.out.println("here 32");
                    installerType.clear();
                    installerType.setItems(fileTypeWindows32List);
                }

            }
        });

        /*
        Locale drop down menu event listener
         */

        osVersion.addValueChangeListener(event -> {
           if(osVersion.getValue() != null && !osVersion.isEnabled()){
               locale.focus();
           }
        });

        locale.addFocusListener(event ->{
           installerType.setEnabled(true);
           locale.clear();
            try {
                utill.parseHTMLLocale(utill.buildPathForLocale(channelSelector.getValue().toString(),buildVersion.getValue().toString(),buildNumber.getValue().toString(),osVersion.getValue().toString()),locale);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }


        });

        locale.addValueChangeListener(event -> {
            if(locale.getValue() != null && !locale.isEnabled()){
                installerType.focus();
            }

        });


        installerType.addValueChangeListener(event -> {
            anc.setEnabled(true);

        });

        anc.getElement().addEventListener("mouseover", e -> {
            if (jsonValue == null) {
                builds.clear();
                dropdownInput = channelSelector.getValue().toString().trim();

                if (dropdownInput.contains("Latest Nightly")) {
                    System.out.println("Nightly");
                    builds.put("latestNightly", "Nightly");
                    utill.fileNN = "LatestNightly" + "-" + locale.getValue();
                } else if (dropdownInput.contains("Beta")) {
                    if (!eng.checkForInvalidString(buildVersion.getValue().toString(), "b")) {
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    } else {
                    System.out.println("Beta");
                    builds.put("betaVersion", buildVersion.getValue().toString());
                    utill.fileNN = buildVersion.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }

            } else if (dropdownInput.contains("Release")) {
                if (eng.checkForInvalidString(buildVersion.getValue().toString(), "b")) {
                    setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                } else {
                    System.out.println("Release");
                    builds.put("releaseVersion", buildVersion.getValue().toString());
                    utill.fileNN = buildVersion.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }
            } else if (dropdownInput.contains("ESR")) {
                    System.out.println("ESR");
                    builds.put("esrVersion", buildVersion.getValue().toString());
                    utill.fileNN = buildVersion.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
            } else if (dropdownInput.contains("DevEd")) {
                if (!eng.checkForInvalidString(buildVersion.getValue().toString(), "b")) {
                    setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                } else {
                    System.out.println("DevEd");
                    builds.put("devedVersion", buildVersion.getValue().toString());
                    utill.fileNN = buildVersion.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }
            }

            try {
                eng.osSelection = osVersion.getValue().toString();
                eng.typeOfFile = installerType.getValue().toString();
                eng.buildNumber = buildNumber.getValue().toString();

                System.out.println(buildNumber.getValue().toString());
                System.out.println(utill.fileNN);
            } catch (NullPointerException nl) {
                anc.setEnabled(false);
            }


            try {
                eng.pathFoundation(builds, eng.buildNumber, eng.osSelection, locale.getValue().toString(), eng.typeOfFile, utill.fileNN);
                anc.setHref(eng.finalPath);
            } catch (NullPointerException | IOException ioException) {
                System.out.println(ioException);
                setErrorMessage("Build Download Error: Please double check you Build Version or Locale");

            }
            System.out.println(eng.finalPath);
        }

        });
    }


    public void setErrorMessage(String message){
        if(message.contains("SUCCESS!!") || message.contains("File Uploaded Successfully") || message.contains("Download finished successfully")){
            errorMessage.setVisible(true);
            errorMessage.setText(message);
        } else if(message.contains("JSON contains invalid or Nightly builds")){
            errorMessage.setVisible(true);
            errorMessage.setText(message);
        } else if(message.contains("Download in progress")){
            errorMessage.setVisible(true);
            errorMessage.setText(message);

        }
    }

}
