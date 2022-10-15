package es.sofftek.jwt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoContoller {

	@RequestMapping("saludo")
	public String holaMundo(@RequestParam(value = "name", defaultValue = "Mundo") String nombre) {
		return "Hola " + nombre;
	}
}
