package club.zhcs.nail.controller;

import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Home", tags = "Home")
public class HomeController {

	@Autowired
	Dao dao;

	@GetMapping("/")
	public String index() {
		return "Hello Nail!";
	}

	@GetMapping("db")
	public Object db() {
		return dao.meta();
	}

}
