package org.routeservice.service;

import org.junit.*;
import org.junit.runner.RunWith;
/*import org.cher.entities.FilterEntity;
import org.cher.entities.FilterToRoute;
import org.cher.entities.Route;
import org.cher.entities.ServiceInstanceEntity;*/
import org.routeservice.entity.*;
import org.routeservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PersistenceServiceIT {
    private EmbeddedDatabase db;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private ServiceInstanceRepository serviceRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private FilterFindingsRepository filterFindingsRepository;

    @Autowired
    private AdditionalInfoRepository additionalInfoRepository;

    @Autowired
    private ProblemDescriptionRepository problemDescriptionRepository;

    @Before
    public void setUp() {
        db = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("db.sql")
                .build();

    }

    @Test
    public void testInsertAdditionalInfo(){
        Date date = new Date();
        AdditionalInfo additionalInfo = AdditionalInfo.builder().
                destinationUrl("testDest").
                sourceUrl("testSource").
                timeOfProblem(date).
                build();
        additionalInfoRepository.save(additionalInfo);
        Assert.assertNotNull(additionalInfoRepository.findFirstByDestinationUrl("testDest"));
    }

    @Test
    public void testInsertProblemDescription(){
        ProblemDescription problemDescription = ProblemDescription.builder().
                description("problem").
                filterEntity(filterRepository.getOne(1)).
                build();
        problemDescriptionRepository.save(problemDescription);
        Assert.assertNotNull(problemDescriptionRepository.findFirstByFilterEntity_FilterId(1));
    }

    @Test
    public void testInsertToDB() throws URISyntaxException {
        enterRoute(enterServiceInstance());
        insert();
        List<FilterFindings> filterFindings = filterFindingsRepository.findByRoute_RouteName("https://sdfdsf.com");
        Assert.assertTrue(!filterFindings.isEmpty());
    }

    private void insert() throws URISyntaxException {
        Route route = routeRepository.findFirstByRouteName("https://sdfdsf.com");
        List<String> problemList = new ArrayList<>();
        problemList.add("problem");
        problemList.add("problem2");
        URI uri = new URI("https://sdfdsf.com/b");
        persistenceService.InsertIntoDB(route,1,problemList,1,uri,"origin");
    }

    private ServiceInstanceEntity enterServiceInstance() {
        return serviceRepository.save(ServiceInstanceEntity.builder().
                serviceId("1").
                planId("plan").
                organizationGuid("org").
                spaceGuid("space").
                build());
    }

    private  void enterRoute(ServiceInstanceEntity service)
    {
        Route routeToSave = Route.builder().
                routeName("https://sdfdsf.com").
                service(service).
                bindingId("1").
                build();
        routeRepository.save(routeToSave);
    }

    private void deleteAllRoutesAndServices() {
        List<ServiceInstanceEntity> entityList = serviceRepository.findAll();
        List<Route> routeList = routeRepository.findAll();
        routeRepository.delete(routeList);
        serviceRepository.delete(entityList);
    }

    @After
    public void tearDown() {
        problemDescriptionRepository.deleteAll();
        additionalInfoRepository.deleteAll();
        filterFindingsRepository.deleteAll();
        deleteAllRoutesAndServices();
        db.shutdown();
    }


}