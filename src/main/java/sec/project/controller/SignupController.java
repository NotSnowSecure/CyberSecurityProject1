package sec.project.controller;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        return "done";
    }
    
    @RequestMapping(value = "/signups", method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("signups", signupRepository.findAll());
        return "signups";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminlogin() {
        return "admin";
    }
    
    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String AdminLogin(@RequestParam String username, @RequestParam String password) {
        try {
            // Open connection to a database -- do not alter this code
            String databaseAddress = "jdbc:h2:file:./database";
            
            
            Connection connection = DriverManager.getConnection(databaseAddress, "sa", "");
            
            try {
                // If database has not yet been created, insert content
                RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
                RunScript.execute(connection, new FileReader("sql/database-import.sql"));
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
            
            // Add the code that reads the Agents from the database
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT username, password from Admins where username = '"+username+"' and password = '"+password+"'");
            // and prints them here

                if(resultSet.next()){
                    return "redirect:/signups";
                }
                
            
            
            return "redirect:/form";
                
            }
            
            
            
            
         catch (SQLException ex) {
            Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "form"; //placeholder
    }
    
}
