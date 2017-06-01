import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class Main extends Application {
    private Text nevField;
    private Map<String , String> nevek;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws IOException {
        setupLogging();
        readNames();
        primaryStage.setTitle("Soltvadkerti Tó - Nyilvántartás");

        GridPane grid = new GridPane();
        //grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label vk = new Label("Vonalkód:");
        grid.add(vk, 0, 0);

        TextField vkTextField = new TextField();
        grid.add(vkTextField, 1, 0);

        vkTextField.setOnAction(event -> {
            LOGGER.info("VK - " + vkTextField.getText());
            if(vkTextField.getText()== null || vkTextField.getText().trim().length()==0){
                return;
            }
            String nev = calcNev(vkTextField.getText());
            LOGGER.info("NEV - " + nev);
            nevField.setText(nev);
            try {
                writeFile(vkTextField.getText(), nev);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "file write exc. ", e);
            }
            vkTextField.setText(null);
        });

        Text vkNev = new Text("Vonalkódhoz tartozó név:");
        grid.add(vkNev, 0, 4);

        nevField = new Text();
        nevField.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        grid.add(nevField, 1, 4);

        Scene scene = new Scene(grid, 500, 175);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setupLogging() throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        Handler fileHandler  = new FileHandler("c:\\vadkeritTo.log", true);

        SimpleFormatter simpleFormatter = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);

        LOGGER.addHandler(fileHandler);
        fileHandler.setLevel(Level.ALL);
        LOGGER.setLevel(Level.ALL);
        LOGGER.config("Configuration done.");
    }

    private void readNames() {
        nevek = new HashMap<>();
        String csvFile = "c:\\names.csv";
        String line = "";
        String cvsSplitBy = ",";
        LOGGER.info("reading nevek.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[]  sor= line.split(cvsSplitBy);
                nevek.put( sor[0], sor[1]);
                LOGGER.info( sor[0] + " -- " + sor[1]);
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "file read exc. ", e);
        }
    }

    private void writeFile(String vk, String nev) throws IOException {
        String csvFile = "c:\\data.csv";
        FileWriter writer = new FileWriter(csvFile, true);
        String date = new SimpleDateFormat("YYYY.MM.dd. HH:mm:ss").format(new Date());
        CSVUtils.writeLine(writer, Arrays.asList(vk, nev, date));

        writer.flush();
        writer.close();
    }


    private String calcNev(String text) {
        if(text !=null){
            if(nevek.containsKey(text)){
                return nevek.get(text);
            }
        }

        return "ismeretlen-"+text;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
