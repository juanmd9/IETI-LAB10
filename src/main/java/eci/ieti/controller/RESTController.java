package eci.ieti.controller;


import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import eci.ieti.data.TodoRepository;
import eci.ieti.data.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RequestMapping("api")
@RestController
public class RESTController {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    TodoRepository todoRepository;

   //TODO inject components (TodoRepository and GridFsTemplate)

    @RequestMapping("/files/{filename}")
    public ResponseEntity<InputStreamResource> getFileByName(@PathVariable String filename) {
        try {
            GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("filename").is(filename)));
            GridFsResource resource = gridFsTemplate.getResource(file.getFilename());
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(resource.getContentType()))
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @CrossOrigin("*")
    @PostMapping("/files")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            return "http://localhost:8080/api/files/"+file.getOriginalFilename();
        } catch (Exception e) {
            return "No se pudo guardar el archivo "+file.getOriginalFilename();
        }
    }

    @CrossOrigin("*")
    @PostMapping("/todo")
    public Todo createTodo(@RequestBody Todo todo) {
        //TODO implement method
        todoRepository.save(todo);
        return todo;
    }

    @CrossOrigin("*")
    @GetMapping("/todo")
    public List<Todo> getTodoList() {
        //TODO implement method
        return todoRepository.findAll();
    }

}
