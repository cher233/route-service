package org.routeservice.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.routeservice.entity.*;
import org.routeservice.service.AuthenticationService;
import org.routeservice.service.PersistenceService;
import static org.hamcrest.core.Is.is;import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilterFindingsControllerTest {

    private AuthenticationService authenticationService;

    private PersistenceService persistenceService;

    private FilterFindingsController filterFindingsController;

    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    Route exampleRoute ;

    AdditionalInfo exampleAdditionalInfo;

    ProblemDescription exampleProblemDescription;

    private List<FilterFindings> findings;


    private Route buildExampleRoute(){
        return Route.builder()
                .routeName("https://bla.com")
                .service(new ServiceInstanceEntity())
                .bindingId("1")
                .build();
    }

    private AdditionalInfo buildExampleAdditionalInformation(){
        return AdditionalInfo.builder()
                .timeOfProblem(new Date(1000))
                .destinationUrl("www.bla.com")
                .build();
    }

    private ProblemDescription buildProblemDescription(){
        return ProblemDescription.builder()
                .description("test")
                .filterEntity(new FilterEntity(1,"directory_traversal"))
                .build();
    }

    @Before
    public void init(){
        filterFindingsController = new FilterFindingsController();
        authenticationService = mock(AuthenticationService.class);
        filterFindingsController.setAuthenticationService(authenticationService);
        persistenceService = mock(PersistenceService.class);
        filterFindingsController.setPersistenceService(persistenceService);
        mockMvc = MockMvcBuilders.standaloneSetup(filterFindingsController).build();
        findings = new ArrayList<>();
        exampleRoute = buildExampleRoute();
        exampleAdditionalInfo = buildExampleAdditionalInformation();
        exampleProblemDescription =buildProblemDescription();
        FilterFindings find1 = FilterFindings.builder()
                .route(exampleRoute)
                .fixed(false)
                .additionalInfo(exampleAdditionalInfo)
                .problemDescription(exampleProblemDescription)
                .build();
        findings.add(find1);


    }

    @Test
    public void TestGetByRoute() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(1);
        when(persistenceService.GetInformationByRoute(1,"https://a",null,null,0,0)).thenReturn((List)findings);
        mockMvc.perform(get("/finding/route/a")
                .header("serviceGuid","a")
                .header("secret","a"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].route.Bound_route", is(exampleRoute.getRouteName())))
                .andExpect(jsonPath("$[0].additionalInfo.Destination_url", is(exampleAdditionalInfo.getDestinationUrl())))
                .andExpect(jsonPath("$[0].problemDescription.Problem", is(exampleProblemDescription.getDescription())))
                .andExpect(jsonPath("$[0].problemDescription.filterEntity.Filter", is(exampleProblemDescription.getFilterEntity().getFilterName())));
    }

    @Test
    public void TestGetByRouteWithNoAuthorization() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(-1);
        mockMvc.perform(get("/finding/route/a")
                .header("serviceGuid","a")
                .header("secret","a"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void TestGetByRouteWithNWrongParameters() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(1);
        mockMvc.perform(get("/finding/route/a")
                .header("serviceGuid","a")
                .header("secret","a").param("startDate","1"))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void TestGetBySpace() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(1);
        when(persistenceService.GetInformationBySpace(1,"a",null,null,0,0)).thenReturn((List)findings);
        mockMvc.perform(get("/finding/space/a")
                .header("serviceGuid","a")
                .header("secret","a"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].route.Bound_route", is(exampleRoute.getRouteName())))
                .andExpect(jsonPath("$[0].additionalInfo.Destination_url", is(exampleAdditionalInfo.getDestinationUrl())))
                .andExpect(jsonPath("$[0].problemDescription.Problem", is(exampleProblemDescription.getDescription())))
                .andExpect(jsonPath("$[0].problemDescription.filterEntity.Filter", is(exampleProblemDescription.getFilterEntity().getFilterName())));
    }

    @Test
    public void TestGetBySpaceWithNoAuthorization() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(-1);
        mockMvc.perform(get("/finding/space/a")
                .header("serviceGuid","a")
                .header("secret","a"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void TestGetBySpaceWithNWrongParameters() throws Exception{
        when(authenticationService.Authenticate(any(),any())).thenReturn(1);
        mockMvc.perform(get("/finding/space/a")
                .header("serviceGuid","a")
                .header("secret","a").param("startDate","1"))
                .andExpect(status().isBadRequest());
    }
}