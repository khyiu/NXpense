package nxpense.controller;

import javax.servlet.http.HttpServletRequest;

import nxpense.service.api.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account")
public class AccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	@ResponseBody
	public String createNewAccount(HttpServletRequest request, @RequestParam String email, @RequestParam char[] password, @RequestParam char[] passwordRepeat) {
		// TODO add Spring validation
		userService.createUser(email, password, passwordRepeat);

		StringBuilder redirection = new StringBuilder()
			.append(request.getContextPath())
			.append("/view/home.html");
		
		LOGGER.info("User [{}] has been created and is automatically logged in", email);
		
		return redirection.toString();
	}

}
