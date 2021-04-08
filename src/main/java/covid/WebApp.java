package covid;

import au.com.bytecode.opencsv.CSVReader;
import covid.dao.CountryRepository;
import covid.entity.Country;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApp {

    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);

        // Timer
        Timer timer = new Timer();
        TimerTask dailyTask = new TimerTask() {
            // On télécharge le fichier toutes les 24h
            private CountryRepository countryDAO;

            @Override
            public void run() {

                // Code
                try {
                    // Téléchargement et stockage du fichier OWD
                    ReadableByteChannel readChannel = Channels.newChannel(new URL("https://covid.ourworldindata.org/data/owid-covid-data.csv").openStream());
                    FileOutputStream fileOS = new FileOutputStream("dataOWD.csv");
                    FileChannel writeChannel = fileOS.getChannel();
                    writeChannel
                            .transferFrom(readChannel, 0, Long.MAX_VALUE);

                    // Traitement du fichier OWD
                    File fileOWD = new File("dataOWD.csv");
                    FileReader frOWD = new FileReader(fileOWD);
                    CSVReader csvrOWD = new CSVReader(frOWD, ',');

                    // Lecture du fichier OWD
                    List<String[]> dataOWD = new ArrayList<String[]>();
                    String[] nextLine = null;
                    while ((nextLine = csvrOWD.readNext()) != null) {
                        int size = nextLine.length;
                        if (size == 0) {
                            continue;
                        }
                        String debut = nextLine[0].trim();
                        if (debut.length() == 0 && size == 1) {
                            continue;
                        }
                        if (debut.startsWith("#")) {
                            continue;
                        }
                        dataOWD.add(nextLine);
                    }

                    // Stockage des données du fichier OWD
                    for (String[] oneData : dataOWD) {

                        String codeCountry = oneData[0];
                        System.out.println(codeCountry);

                        Country country = new Country();
                        country.setCodeCountry(codeCountry);

                        countryDAO.save(country);

                    }

                } catch (Exception ex) {
                    Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        // Timer de 24h
        timer.schedule(dailyTask, 01, 60 * 60);
    }
}
