package nxpense.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
public class AccountController {
	private Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public void createNewAccount(@RequestParam String email, @RequestParam char[] password, @RequestParam char[] passwordRepeat) {
    	LOGGER.info("Requesting account creation for email = {}", email);
    }
    
}
