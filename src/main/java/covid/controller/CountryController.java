package covid.controller;

import covid.dao.ContinentRepository;
import covid.dao.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/country")
public class CountryController {
    
    @Autowired
    private CountryRepository countryDAO;
    @Autowired
    private ContinentRepository continentDAO;
    
    @GetMapping(path = "europe")
    public String showEuropeanCountries(Model model) {
        model.addAttribute("europeanCountries", countryDAO.getEuropeanCountries());
        return "europeanCountries";
    }
}


