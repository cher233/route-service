package org.routeservice.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.routeservice.entity.FilterEntity;
import org.routeservice.entity.FilterToRoute;
import org.routeservice.entity.Route;
import org.routeservice.exception.RouteNotFoundException;
import org.routeservice.filter.DirectoryTraversalFilter;
import org.routeservice.filter.Filter;
import org.routeservice.repository.FilterRepository;
import org.routeservice.repository.FilterToRouteRepository;
import org.routeservice.repository.RouteRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CreateAndRunFilterServiceTest {

    CreateAndRunFilterService createAndRunFilterService;

    private RouteRepository routeRepository;

    private FilterRepository filterRepository;

    private FilterToRouteRepository filterToRouteRepository;

    private RequestEntity request;

    @Before
    public void init(){
        request = mock(RequestEntity.class);
        List<Filter> filterList = new ArrayList<>();
        filterList.add(mock(DirectoryTraversalFilter.class));
        createAndRunFilterService = new CreateAndRunFilterService();
        createAndRunFilterService.setNumberOfThreads(5);
        createAndRunFilterService.setDefaultFilter(0);
        routeRepository = mock(RouteRepository.class);
        createAndRunFilterService.setRouteRepository(routeRepository);
        filterRepository = mock(FilterRepository.class);
        createAndRunFilterService.setFilterRepository(filterRepository);
        filterToRouteRepository = mock(FilterToRouteRepository.class);
        createAndRunFilterService.setFilterToRouteRepository(filterToRouteRepository);
        createAndRunFilterService.setFilterList(filterList);
    }

    @Test(expected = RouteNotFoundException.class)
    public void CreateAndRunFiltersWithoutRouteTest(){
        HttpHeaders headers = mock(HttpHeaders.class);
        List<String> headerList = new ArrayList<>();
        headerList.add("https://www.bla.com");
        when(request.getHeaders()).thenReturn(headers);
        when(headers.get("X-CF-Forwarded-Url")).thenReturn(headerList);
        when(routeRepository.findFirstByRouteName("https://www.bla.com")).thenReturn(null);
        createAndRunFilterService.CreateAndRunFilters(request);
    }

    @Test
    public void CreateAndRunFiltersWithFiltersTest(){
        HttpHeaders headers = mock(HttpHeaders.class);
        HashSet<Integer> filters = new HashSet<>();
        filters.add(1);
        Route route = mock(Route.class);
        List<String> headerList = new ArrayList<>();
        headerList.add("www.bla.com");
        List<FilterToRoute> filterToRouteList =new ArrayList<>();
        FilterToRoute filterToRoute = mock(FilterToRoute.class);
        FilterEntity filter = mock(FilterEntity.class)
                ;       filterToRouteList.add(filterToRoute);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.get("X-CF-Forwarded-Url")).thenReturn(headerList);
        when(routeRepository.findFirstByRouteName("https://www.bla.com")).thenReturn(route);
        when(route.getRouteId()).thenReturn(1);
        when(filterToRouteRepository.findAllByRoute_RouteId(1)).thenReturn(filterToRouteList);
        when(filterToRoute.getFilter()).thenReturn(filter);
        when(filter.getFilterId()).thenReturn(1);
        when(filter.getFilterName()).thenReturn("directory_traversal");
        when(route.getRouteName()).thenReturn("https://www.bla.com");
        RequestEntity assertRequest = createAndRunFilterService.CreateAndRunFilters(request);
        spy(createAndRunFilterService).checkForFilters(route);
        spy(createAndRunFilterService).runFilter(filters,request,route);
        Assert.assertNotNull(assertRequest);
    }

    @Test
    public void CreateAndRunFiltersWithoutFiltersTest(){
        HttpHeaders headers = mock(HttpHeaders.class);
        HashSet<Integer> filters = new HashSet<>();
        filters.add(1);
        Route route = mock(Route.class);
        List<String> headerList = new ArrayList<>();
        headerList.add("www.bla.com");
        List<FilterToRoute> filterToRouteList =new ArrayList<>();
        FilterToRoute filterToRoute = mock(FilterToRoute.class);
        FilterEntity filter = mock(FilterEntity.class);
        filterToRouteList.add(filterToRoute);
        when(request.getHeaders()).thenReturn(headers);
        when(headers.get("X-CF-Forwarded-Url")).thenReturn(headerList);
        when(routeRepository.findFirstByRouteName("https://www.bla.com")).thenReturn(route);
        when(route.getRouteId()).thenReturn(1);
        when(filterToRouteRepository.findAllByRoute_RouteId(1)).thenReturn(null);
        when(filterRepository.countAllByFilterIdGreaterThan(0)).thenReturn((long) 1);
        when(filter.getFilterId()).thenReturn(1);
        when(route.getRouteName()).thenReturn("https://www.bla.com");
        RequestEntity assertRequest = createAndRunFilterService.CreateAndRunFilters(request);
        spy(createAndRunFilterService).checkForFilters(route);
        spy(createAndRunFilterService).runFilter(filters,request,route);
        Assert.assertNotNull(assertRequest);
    }



}