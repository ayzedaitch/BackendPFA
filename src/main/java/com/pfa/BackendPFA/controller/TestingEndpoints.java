package com.pfa.BackendPFA.controller;

import com.pfa.BackendPFA.entity.City;
import com.pfa.BackendPFA.entity.Monument;
import com.pfa.BackendPFA.model.MessageModel;
import com.pfa.BackendPFA.repository.CityRepository;
import com.pfa.BackendPFA.repository.MonumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class TestingEndpoints {

    @GetMapping("/tourist")
    public MessageModel helloTourist(){
        return new MessageModel("Salam Tourist");
    }
    @GetMapping("/admin")
    public MessageModel helloAdmin(){
        return new MessageModel("Salam Tourist");
    }

}
