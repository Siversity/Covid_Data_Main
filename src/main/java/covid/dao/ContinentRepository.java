package covid.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import covid.entity.Continent;
import covid.entity.Country;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

// This will be AUTO IMPLEMENTED by Spring 

public interface ContinentRepository extends JpaRepository<Continent, String> {
    
    /*
    @Query("SELECT * "
		+ "FROM Continent c "
		+ "WHERE c.nameContinent = :nameContinent "
		+ "GROUP BY p.nom ")
    public List<Country> getCountries(String nameContinent);
    */
}
