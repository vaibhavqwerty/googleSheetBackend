package com.example.demo.controller;

import com.example.demo.service.GoogleSheetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@Slf4j
public class SheetController {


    @Autowired
    private GoogleSheetsService googleSheetsService;


    @RequestMapping(value="/getRequest",
            method = RequestMethod.GET,
            produces= MediaType.TEXT_HTML_VALUE)
    public ResponseEntity getRequest(){
        try{
            return new ResponseEntity("Get request response", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/saveRequest",
            method = RequestMethod.POST,
            produces= MediaType.TEXT_HTML_VALUE)
    public ResponseEntity postRequest(@RequestParam String username, @RequestParam String number){
        try{
            googleSheetsService.addRow(username,number);
            return new ResponseEntity("Successfully saved to sheet for "+username+" "+number, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}