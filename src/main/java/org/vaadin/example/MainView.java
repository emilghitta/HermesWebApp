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



        ComboBox channelSelector = new ComboBox();
        channelSelector.isRequired();
        channelSelector.setLabel("Choose the download channel");
        channelSelector.setItems(channelList);
        add(channelSelector);

        TextField versionInput = new TextField();
        versionInput.isRequired();
        versionInput.setLabel("Specify build version");
        versionInput.setPlaceholder("eg. 82.0b4");
        versionInput.setEnabled(false);
        add(versionInput);

        TextField locale = new TextField();
        locale.isRequired();
        locale.setLabel("Locale");
        locale.setPlaceholder("eg. en-US");
        locale.setEnabled(false);
        add(locale);

        ComboBox buildNumber = new ComboBox();
        buildNumber.isRequired();
        buildNumber.setLabel("Select build");
        buildNumber.setEnabled(false);
        add(buildNumber);


        ComboBox osVersion = new ComboBox();
        osVersion.isRequired();
        osVersion.setLabel("Platform");
        osVersion.setEnabled(false);
        osVersion.setItems(osVersionList);
        add(osVersion);

        Checkbox autoOpenBuilds = new Checkbox();
        autoOpenBuilds.setEnabled(false);

        ComboBox installerType = new ComboBox();
        installerType.isRequired();
        installerType.setLabel("Installer");
        installerType.setEnabled(false);
        add(installerType);


        Anchor anc = new Anchor();
        anc.setText("Download build");
        anc.setEnabled(false);
        add(anc);


        errorMessage.setVisible(false);
        add(errorMessage);


        channelSelector.addValueChangeListener(event -> {
            osVersion.setValue("");
            versionInput.setValue("");
            locale.setValue("");
            autoOpenBuilds.setEnabled(true);

            if (channelSelector.getValue().toString().contains("Latest Nightly")) {
                versionInput.setLabel("");
                versionInput.setEnabled(false);
                buildNumber.clear();
                buildNumber.setEnabled(false);
                osVersion.setEnabled(true);
            } else {
                versionInput.setEnabled(true);
                buildNumber.clear();
                buildNumber.setEnabled(true);
                osVersion.setEnabled(false);
            }
        });



       versionInput.getElement().addEventListener("keydown", e->{
          buildNumber.clear();
       });

       locale.getElement().addEventListener("keydown", e->{
           buildNumber.clear();
           //osVersion.clear();
       });

        buildNumber.addFocusListener(event -> {
            buildNumber.clear();
            utill.parseHTMLBuildVersion(utill.buildPathForBuildVersion((String) channelSelector.getValue(), versionInput), buildNumber);
        });


        buildNumber.addValueChangeListener(event -> {
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
                    if (!eng.checkForInvalidString(versionInput.getValue(), "b")) {
                        setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                    } else {
                    System.out.println("Beta");
                    builds.put("betaVersion", versionInput.getValue());
                    utill.fileNN = versionInput.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }

            } else if (dropdownInput.contains("Release")) {
                if (eng.checkForInvalidString(versionInput.getValue(), "b")) {
                    setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                } else {
                    System.out.println("Release");
                    builds.put("releaseVersion", versionInput.getValue());
                    utill.fileNN = versionInput.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }
            } else if (dropdownInput.contains("ESR")) {
                    System.out.println("ESR");
                    builds.put("esrVersion", versionInput.getValue());
                    utill.fileNN = versionInput.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
            } else if (dropdownInput.contains("DevEd")) {
                if (!eng.checkForInvalidString(versionInput.getValue(), "b")) {
                    setErrorMessage("Build Download Error: Please double check you Build Version or Locale");
                } else {
                    System.out.println("DevEd");
                    builds.put("devedVersion", versionInput.getValue());
                    utill.fileNN = versionInput.getValue() + "-" + buildNumber.getValue() + "-" + locale.getValue();
                }
            }

            try {
                eng.osSelection = osVersion.getValue().toString();
                eng.typeOfFile = installerType.getValue().toString();
                eng.buildNumber = buildNumber.getValue().toString();

                System.out.println(buildNumber.getValue().toString());
                System.out.println(utill.fileNN);
            } catch (NullPointerException nl) {
                System.out.println("Nothing scary. Something is null");
            }


            try {
                eng.pathFoundation(builds, eng.buildNumber, eng.osSelection, locale.getValue(), eng.typeOfFile, utill.fileNN);
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
