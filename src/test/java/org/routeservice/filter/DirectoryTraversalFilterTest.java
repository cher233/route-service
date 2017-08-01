/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.routeservice.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.routeservice.service.PersistenceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import java.util.List;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DirectoryTraversalFilterTest {

    private Filter filter;

    private RequestEntity<?> requestEntity;

    private List<String> list;

    @Before
    public void init(){
        filter = new DirectoryTraversalFilter(1);
        filter.setService(mock(PersistenceService.class));
        requestEntity = mock(RequestEntity.class);
        HttpHeaders headers = mock(HttpHeaders.class);
        list = mock(List.class);
        when(requestEntity.getHeaders()).thenReturn(headers);
        when(headers.get(any())).thenReturn(list);
    }

    @Test
    public void checkVulnerabilityWithProblem() throws Exception {
        when(list.get(0)).thenReturn("https://www.bla.com/b/%2e%2e%2fb");
        List<String> problems = filter.CheckVulnerability(requestEntity);
        Assert.assertTrue(!problems.isEmpty());
    }

    @Test
    public void checkVulnerabilityWithAnotherProblem() throws Exception {
        when(list.get(0)).thenReturn("https://www.bla.com/b/../b");
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