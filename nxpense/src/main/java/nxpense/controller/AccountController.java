package nxpense.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
public class AccountController {
    
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public void createNewAccount(@RequestParam String email, @RequestParam char[] password, @RequestParam char[] passwordRepeat) {
	// todo
	System.out.println(">>> create new account");
    }
    
}
