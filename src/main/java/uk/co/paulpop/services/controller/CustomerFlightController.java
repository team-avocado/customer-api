package uk.co.paulpop.services.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.co.paulpop.services.exception.handler.HttpExceptionResponse;
import uk.co.paulpop.services.model.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@Api("Java Spring Service API")
@RestController
@RequestMapping("/api")
public class CustomerFlightController {

    @Autowired
    private CustomerFlightRepository customerFlightRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSpringServiceController.class);

    @PostMapping("/customerflight")
    public ResponseEntity addCustomerFlight (@RequestBody CustomerFlight customerFlightJson) {
        customerFlightRepository.save(customerFlightJson);
        System.out.println("POST CustomerFlight hit");
        return new ResponseEntity(customerFlightJson, HttpStatus.OK);
    }

    @GetMapping("/getcustomerflight")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String getCustomerInfo(){

        return customerFlightRepository.findAll().toString();
    }
}
