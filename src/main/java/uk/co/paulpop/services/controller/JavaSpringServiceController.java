package uk.co.paulpop.services.controller;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class JavaSpringServiceController {

    @Value("${AWS_ACCESS_ID:nope}") // value after ':' is the default
    String AWS_ACCESS_ID;

    @Value("${AWS_SECRET_KEY:nopetwo}")
    String AWS_SECRET_KEY;

    @Autowired
    private CustomerRepository customerRepository;
    private CustomerFlightRepository customerFlightRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSpringServiceController.class);

    @GetMapping("/getcustomerinfo")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
        @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
        @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public String getCustomerInfo(){
        return customerRepository.findAll().toString();
    }

    @PostMapping("/customerinfo")
    public ResponseEntity addCustomerInfo (@RequestBody Customer customerJson) {
        customerRepository.save(customerJson);
        System.out.println("POST Customer info hit");
        return new ResponseEntity(customerJson, HttpStatus.OK);
    }

    @PostMapping("/flightUpdates")
    public ResponseEntity getFlightUpdateResponse (HttpServletRequest request) {

        System.out.println("HIT getFlightUpdateResponse");
        System.out.println(request.toString());

        String flightUpdateResponseString = "";

        try {
            flightUpdateResponseString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException i) {
            i.printStackTrace();
        }

        System.out.println("RESULTS " + flightUpdateResponseString);

        JSONObject flightUpdateResponseJson = new JSONObject(flightUpdateResponseString);

        System.out.println("FlightId: " + flightUpdateResponseJson.get("Message").toString());

        System.out.println("FlightId next line: " + flightUpdateResponseJson.get("Message").toString());

        ArrayList<Integer> customersToNotify = getCustomersIdsOnFlight(Integer.parseInt(flightUpdateResponseJson.get("Message").toString()));

        System.out.println("Customers to Notify");


        try {
            AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(Regions.EU_WEST_1).build();
            SendEmailRequest emailRequest = new SendEmailRequest()
                .withDestination(
                    new Destination().withToAddresses("mng@and.digital"))
                .withMessage(new Message()
                    .withBody(new Body()
                        .withHtml(new Content()
                            .withCharset("UTF-8").withData("YOU'RE NOT GETTING HOME"))
                        .withText(new Content()
                            .withCharset("UTF-8").withData("EVER AGAIN")))
                    .withSubject(new Content()
                        .withCharset("UTF-8").withData("Lots of love, CityJet")))
                .withSource("jsimpson@and.digital");
                // Comment or remove the next line if you are not using a
                // configuration set
                //.withConfigurationSetName(CONFIGSET);
            emailRequest.setRequestCredentials(new BasicAWSCredentials(AWS_ACCESS_ID, AWS_SECRET_KEY));


            client.sendEmail(emailRequest);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: "
                + ex.getMessage());
        }
        // notifyCustomers(customersToNotify);

        return new ResponseEntity("Yaaaay.", HttpStatus.OK);
    }

    private ArrayList<Integer> getCustomersIdsOnFlight(int flightId) {

        System.out.println("in getCustomersIdsOnFlight");
        System.out.println("flightId: " + flightId);
        ArrayList<Integer> customerIds = new ArrayList<Integer>();

        ArrayList<Integer> flightIds = new ArrayList<Integer>();
        flightIds.add(flightId);

        System.out.println("added flightId to arrayList");

//        Iterable<CustomerFlight> customerFlightIds = customerFlightRepository.findAll(flightIds);

//        Iterable<CustomerFlight> customerFlightIds =

        System.out.println("got cusomterFlightIds");

//        for(CustomerFlight s:customerFlightIds) {
//            System.out.println("In for loop");
//            customerIds.add(s.getCustomerId());
//        }
        return null;
    }

    private void notifyCustomers(ArrayList<Integer> customerIds) {
        System.out.println("in notifyCustomers");

        Iterable<Customer> customersInfo = customerRepository.findAll(customerIds);

        for(Customer customer:customersInfo) {
            int customerId = customer.getCustomerId();
            String customerName = customer.getName();
            String customerEmail = customer.getEmail();

            System.out.println("Customer Id: " + customerId);
            System.out.println("Customer Name: " + customerName);
            System.out.println("Customer Email: " + customerEmail);

            // TO DO - send email to notify customer using Amazon SES
        }
    }

}
