package org.routeservice.service;

import lombok.Setter;
import org.routeservice.entity.ServiceInstanceEntity;
import org.routeservice.repository.ServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Setter
    @Autowired
    PasswordEncoder passwordEncoder;

    @Setter
    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;

    public int Authenticate(String serviceId, String secret){
        ServiceInstanceEntity serviceInstance = serviceInstanceRepository.findFirstByServiceId(serviceId);
        if(serviceInstance == null || !passwordEncoder.matches(secret,serviceInstance.getPassword())) return -1;
        return serviceInstance.getId();
    }
}
