package com.books.integrate.spring.react.controller;

import java.util.*;

import com.books.integrate.spring.react.model.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books.integrate.spring.react.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TutorialController {

	@Autowired
	TutorialRepository tutorialRepository;

	@GetMapping("/tutorials/all")
	public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
		try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();

			if (title == null)
				tutorialRepository.findAll().forEach(tutorials::add);
			else
				tutorialRepository.findByTitle(title).forEach(tutorials::add);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/tutorials/getById/{id}")
	public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	//Nuevo, consulta por precio
	@GetMapping("tutorials/queryPrecio")
    public List<Tutorial> obtenerUsuarioPorPrioridad(@RequestParam("price") double price){
        return tutorialRepository.findByPrice(price);
    }


	@PostMapping("/tutorials/createTutorial")
	public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
		try {
			Tutorial _tutorial = tutorialRepository
					.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(),  false, tutorial.getPrice()));
			return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PutMapping("/tutorials/actualizarById/{id}")
	public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
		Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

		if (tutorialData.isPresent()) {
			Tutorial _tutorial = tutorialData.get();
			_tutorial.setTitle(tutorial.getTitle());
			_tutorial.setDescription(tutorial.getDescription());
			_tutorial.setPublished(tutorial.isPublished());
			return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	//Nuevo, actualizar tutorial por t??tulo
	@PutMapping("/tutorials/actualizarByTitulo/{title}")
	public ResponseEntity<Tutorial> updateTutorialByTitle(@PathVariable("title") String title, @RequestBody Tutorial tutorial) {
		List<Tutorial> tutorialData = tutorialRepository.findByTitle(title);

		if (!(tutorialData.isEmpty())) {
			//Tutorial _tutorial = tutorialData.get();
			tutorialData.stream().forEach((_tutorial -> {				
				_tutorial.setTitle(tutorial.getTitle());
				_tutorial.setDescription(tutorial.getDescription());
				_tutorial.setPublished(tutorial.isPublished());
				tutorialRepository.save(_tutorial);			
			}));
			return new ResponseEntity<>(HttpStatus.OK);			
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//HttpStatus
	@DeleteMapping("/tutorials/eliminarById/{id}")
	public ResponseEntity<String> deleteTutorial(@PathVariable("id") long id) {
		try {
			tutorialRepository.deleteById(id);
				return new ResponseEntity<>("Tutorials DELETE!! ",HttpStatus.NO_CONTENT);
			} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@DeleteMapping("/tutorials/deleteAll")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			tutorialRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

	}
	//Nuevo, delete mapping por t??tulo
	@DeleteMapping("tutorials/deleteByTitle/{title}")
	public ResponseEntity<HttpStatus> deleteByTitle(@PathVariable("title") String title) {
		try {
			List<Tutorial> t = tutorialRepository.findByTitle(title);
			if(!(t.isEmpty())){
				t.stream().forEach((tutorial -> {
					tutorialRepository.deleteById(tutorial.getId());
				}));				
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}else{
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}		
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

	}

	@GetMapping("/tutorials/published")
	public ResponseEntity<List<Tutorial>> findByPublished() {
		try {
			List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

			if (tutorials.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

}
