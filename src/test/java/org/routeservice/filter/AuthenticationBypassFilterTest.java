package org.routeservice.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.routeservice.service.PersistenceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import java.util.List;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Nofar on 01/08/2017.
 */
public class AuthenticationBypassFilterTest {

    private Filter filter;

    private RequestEntity<?> requestEntity;

    private List<String> list;

    @Before
    public void init(){
        filter = new AuthenticationBypassFilter(2);
        filter.setSleep(500);
        filter.setService(mock(PersistenceService.class));
        requestEntity = mock(RequestEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        list = mock(List.class);
        when(requestEntity.getHeaders()).thenReturn(headers);
        when(headers.get(any())).thenReturn(list);
    }

    @Test
    public void checkVulnerabilityWithProblem() throws Exception {
        when(list.get(0)).thenReturn("https://www.bla.com/%20connectivity-mgmt/ovpn/ping");
        List<String> problems = filter.CheckVulnerability(requestEntity);
        Assert.assertTrue(!problems.isEmpty());
    }

    @Test
    public void checkVulnerabilityWithAnotherProblem() throws Exception {
        when(list.get(0)).thenReturn("https://www.bla.com/b/%20/b");
        List<String> problems = filter.CheckVulnerability(requestEntity);
        Assert.assertTrue(!problems.isEmpty());
    }

    @Test
    public void checkVulnerabilityWithoutProblem() throws Exception {
        when(list.get(0)).thenReturn("https://www.bla.com/b");
        List<String> problems = filter.CheckVulnerability(requestEntity);
        Assert.assertTrue(problems.isEmpty());
    }

}
