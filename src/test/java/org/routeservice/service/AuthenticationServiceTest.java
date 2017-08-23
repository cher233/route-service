package org.routeservice.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.routeservice.entity.ServiceInstanceEntity;
import org.routeservice.repository.ServiceInstanceRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    private ServiceInstanceRepository serviceInstanceRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationService authenticationService;

    @Before
    public void init() {
        authenticationService = new AuthenticationService();
        serviceInstanceRepository = mock(ServiceInstanceRepository.class);
        authenticationService.setServiceInstanceRepository(serviceInstanceRepository);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationService.setPasswordEncoder(passwordEncoder);
    }

    @Test
    public void authenticateNoSuchService() {
        when(serviceInstanceRepository.findFirstByServiceId("1")).thenReturn(null);
        int result = authenticationService.Authenticate("1","a");
        Assert.assertEquals(result,-1);
    }

    @Test
    public void authenticatePasswordDoesNotMatch() {
        when(serviceInstanceRepository.findFirstByServiceId("1")).thenReturn(mock(ServiceInstanceEntity.class));
        when(passwordEncoder.matches("a","b")).thenReturn(false);
        int result = authenticationService.Authenticate("1","a");
        Assert.assertEquals(result,-1);
    }

    @Test
    public void authenticate() {
        ServiceInstanceEntity serviceInstanceEntity = mock(ServiceInstanceEntity.class);
        when(serviceInstanceRepository.findFirstByServiceId("1")).thenReturn(serviceInstanceEntity);
        when(serviceInstanceEntity.getPassword()).thenReturn("a");
        when(passwordEncoder.matches("a","a")).thenReturn(true);
        int result = authenticationService.Authenticate("1","a");
        Assert.assertNotEquals(result,-1);
    }

}