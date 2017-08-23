package org.routeservice.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.routeservice.entity.FilterFindings;
import org.routeservice.service.AuthenticationService;
import org.routeservice.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestOperations;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/finding")

public class FilterFindingsController {
    @Autowired
    PersistenceService persistenceService;

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = "/route/{route}", method = RequestMethod.GET , produces="application/json")
    @ResponseBody
    public ResponseEntity<?> GetFindingsByRoute(@PathVariable("route") String route,
                                                @RequestHeader("serviceGuid") String serviceGuid,
                                                @RequestHeader("secret") String secret,
                                                @RequestParam(value = "startDate",required = false) Date startDate,
                                                @RequestParam(value = "endDate",required = false) Date endDate,
                                                @RequestParam(value = "filter",required = false, defaultValue = "0") int filter){

        int serviceId  = authenticationService.Authenticate(serviceGuid, secret);
        if(serviceId == -1) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        int ParametersCheck = checkForNullParameters(startDate, endDate, filter);
        if(ParametersCheck == -1) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        route = "https://" + route;
        List<FilterFindings> response = persistenceService.GetInformationByRoute(serviceId, route, startDate, endDate,
                filter,ParametersCheck);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

   @RequestMapping(value = "/space/{space}", method = RequestMethod.GET , produces="application/json")
    public ResponseEntity<?> GetFindingsBySpace(@PathVariable("space") String space,
                                   @RequestHeader("serviceGuid") String serviceGuid,
                                   @RequestHeader("secret") String secret,
                                   @RequestParam(value = "startDate",required = false) Date startDate,
                                   @RequestParam(value = "endDate",required = false ) Date endDate,
                                   @RequestParam(value = "filter",required = false, defaultValue = "0") int filter){
       int serviceId  = authenticationService.Authenticate(serviceGuid, secret);
       if(serviceId == -1) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
       int ParametersCheck = checkForNullParameters(startDate, endDate, filter);
       if(ParametersCheck == -1) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
       List<FilterFindings> response = persistenceService.GetInformationBySpace(serviceId, space, startDate, endDate,
               filter,ParametersCheck);
       return new ResponseEntity<>(response,HttpStatus.OK);

   }



    private int checkForNullParameters(Date startDate, Date endDate, int filter){
        if(startDate == null && endDate == null && filter==0) return 0;
        else if(startDate != null && endDate != null && filter==0)return 1;
        else if(startDate == null && endDate == null && filter !=0)return 2;
        else if(startDate != null && endDate != null && filter !=0) return 3;
        else return -1;
    }

}
