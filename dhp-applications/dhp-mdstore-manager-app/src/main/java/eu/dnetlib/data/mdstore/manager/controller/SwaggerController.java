package eu.dnetlib.data.mdstore.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SwaggerController {

	@RequestMapping(value = { "/apidoc", "/api-doc", "/doc", "/swagger" }, method = RequestMethod.GET)
	public String apiDoc() {
		return "redirect:swagger-ui.html";
	}
}
