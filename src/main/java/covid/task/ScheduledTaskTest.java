package covid.task;

import au.com.bytecode.opencsv.CSVReader;
import covid.WebApp;
import covid.dao.ContinentRepository;
import covid.dao.CountryRepository;
import covid.entity.Continent;
import covid.entity.Country;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableJpaRepositories(basePackages = {"covid.dao"})
@EntityScan(basePackages = {"covid.entity"})
@Component
public class ScheduledTaskTest {

    // DAO
    @Autowired
    private final ContinentRepository continentDAO;
    @Autowired
    private final CountryRepository countryDAO;

    // Constructeur
    public ScheduledTaskTest(ContinentRepository continentDAO, CountryRepository countryDAO) {
        this.continentDAO = continentDAO;
        this.countryDAO = countryDAO;
    }

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskTest.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // Méthode test
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
    }

    // Méthode téléchargement
    @Scheduled(cron = "*/10 * * * * *")
    public void downloadFile() {
        // Code
        try {
            // Téléchargement et stockage du fichier OWD
            // On crée un lien direct entre l'url et notre instance
            ReadableByteChannel readChannel = Channels.newChannel(new URL("https://covid.ourworldindata.org/data/owid-covid-data.csv").openStream());
            // On crée un chemin nommé "dataOWD.csv"
            FileOutputStream fileOS = new FileOutputStream("dataOWD.csv");
            // On stocke les données téléchargées dans le chemin
            FileChannel writeChannel = fileOS.getChannel();
            writeChannel
                    .transferFrom(readChannel, 0, Long.MAX_VALUE);

            // Traitement du fichier OWD
            // On transforme le chemin en fichier csv
            File fileOWD = new File("dataOWD.csv");
            // On crée une instance de lecture pour lire le csv
            FileReader frOWD = new FileReader(fileOWD);
            // On définit le séparateur (pour le csv : ",")
            CSVReader csvrOWD = new CSVReader(frOWD, ',');

            // Lecture du fichier OWD
            // On initialise un tableau et une variable vide qui va contenir les élements lus par le Reader
            List<String[]> dataOWD = new ArrayList<String[]>();
            String[] nextLine = null;
            // Si la ligne lue n'est pas vide
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

            // On supprime la 1ère ligne qui correspond au no des attributs
            dataOWD.remove(0);

            // Stockage des données du fichier OWD
            for (String[] oneData : dataOWD) {

                Continent continent = saveContinent(oneData);

                // Sauvegarde dans la BDD
                countryDAO.save(country);
                System.out.println("SYSTEM OUT " + countryDAO.findById(codeCountry));

            }
            System.out.println(countryDAO.findAll());

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WebApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float verificateur(String attribute) {
        float value = 0;
        if ((attribute.isBlank()) && (attribute.equals(null))) {
            value = Float.valueOf(attribute);
        }
        return value;
    }

    public Continent saveContinent(String[] oneData) {
        // Récupération des données
        String nameContinent = oneData[1];

        // Ajout des données
        Continent continent = new Continent();
        continent.setNameContinent(nameContinent);

        // Return
        return continent;
    }

    public Country saveCountry(String[] oneData) {
        // Récupération des données
        String codeCountry = oneData[0];
        String nameContinent = oneData[1];
        String nameCountry = oneData[2];
        float totalCases = verificateur(oneData[4]);
        float totalDeaths = verificateur(oneData[7]);
        float icuPatients = verificateur(oneData[17]);
        float hospPatients = verificateur(oneData[19]);
        float totalTests = verificateur(oneData[26]);
        float totalVaccinations = verificateur(oneData[34]);
        float fullyVaccinated = verificateur(oneData[36]);
        float stringencyIndex = Float.valueOf(stringStringencyIndex);
        
        /*
                
                // fullyVaccinated
                String stringFullyVaccinated = oneData[34];
                
                // stringencyIndex
                String stringStringencyIndex = oneData[34];
                
                // population
                String stringPopulation = oneData[34];
                float population = Float.valueOf(stringPopulation);
                // gdp
                String stringGdp = oneData[34];
                float gdp = Float.valueOf(stringGdp);
         */
        // Définition des attributs Country
        Country country = new Country();
        country.setCodeCountry(codeCountry);
        country.setContinent(nameContinent);
        country.setNameCountry(nameCountry);
        country.setTotalCases(totalCases);
        /*
                country.setTotalCases(totalCases);
                country.setTotalDeaths(totalDeaths);
                country.setIcuPatients(icuPatients);
                country.setHospPatients(hospPatients);
                country.setTotalTests(totalTests);
                country.setTotalVaccinations(totalVaccinations);
                country.setFullyVaccineted(fullyVaccinated);
                country.setStringencyIndex(stringencyIndex);
                country.setPopulation(population);
                country.setGdp(gdp);
         */
    }
}
