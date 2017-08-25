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

package org.routeservice.service;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.routeservice.entity.FilterToRoute;
import org.routeservice.entity.Route;
import org.routeservice.exception.RouteNotFoundException;
import org.routeservice.filter.Filter;
import org.routeservice.repository.FilterRepository;
import org.routeservice.repository.FilterToRouteRepository;
import org.routeservice.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Cher on 13/07/2017.
 */

@Service
@Slf4j
public class CreateAndRunFilterService {

    @Autowired
    @Setter
    List<Filter> filterList;

    @Autowired
    @Setter
    private RouteRepository routeRepository;

    @Autowired
    @Setter
    private FilterRepository filterRepository;

    @Autowired
    @Setter
    private FilterToRouteRepository filterToRouteRepository;

    @Value("${threads}")
    @Setter
    private int numberOfThreads;

    public RequestEntity<?> CreateAndRunFilters(RequestEntity<?> request) {

        log.info("Searching for route...");
        Route routeToRun = extractRoute(request.getHeaders());
        log.info("Extracting filters from DB...");
        HashSet<Integer> filterIndexList = checkForFilters(routeToRun);
        log.info("Activating filters...");
        runFilter(filterIndexList, request, routeToRun);
        log.info("Finished validating request.");
        return request;
    }

    private Route extractRoute(HttpHeaders headers) {
        log.debug("Constructing route...");
        URI uri = Filter.getFullUri(headers);
        String protocol = uri.getScheme();
        String routeName;
        if(protocol==null){
            routeName="https://"+uri.toString();
        }
        else{
            routeName = new StringBuilder().append(protocol).append("://").append(uri.getHost()).toString();
        }
        log.debug("Retrieving route from DB...");
        Route routeToCheck = routeRepository.findFirstByRouteName(routeName);
        if(routeToCheck == null) {
          throw new RouteNotFoundException(String.format("unregistered route", routeName));
        }
        log.info("Found Route: {}",routeName);
        return routeToCheck;
    }

    protected HashSet<Integer> checkForFilters(Route routeToCheck) {
        HashSet<Integer> filtersPerRoute;
        log.debug("Searching filters in DB...");
        List<FilterToRoute> filterToRouteList = filterToRouteRepository.findAllByRoute_RouteId(routeToCheck.getRouteId());
        if(filterToRouteList != null && filterToRouteList.get(0).getFilter().getFilterId()!= 0) {
            filtersPerRoute = new HashSet<>();
            for (FilterToRoute filter : filterToRouteList) {
                log.info("Selected filter:{} for route:{}",filter.getFilter().getFilterName(),routeToCheck.getRouteName());
                filtersPerRoute.add(filter.getFilter().getFilterId());
            }
        }
        else
            filtersPerRoute = convertDefaultFilterToFilterList();
        log.debug("Finished retrieving filters for route: {}", routeToCheck.getRouteName());
        return filtersPerRoute;
    }

    private HashSet<Integer> convertDefaultFilterToFilterList(){
        log.info("Generating default filters...");
        HashSet<Integer> setToReturn = new HashSet<>();
        long amountOfFilters = filterRepository.countAllByFilterIdGreaterThan(0);
        for(int i = 1 ; i <= amountOfFilters ; i++) setToReturn.add(i);
        return setToReturn;
    }

    protected void runFilter(HashSet<Integer> filterIdList, RequestEntity<?> request, Route route) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        log.debug("Running Filters...");
        for(Filter filter: filterList){
            if(filterIdList.contains(filter.getFilterId())) {
                filter.setRoute(route);
                filter.setRequestEntity(request);
                executor.execute(filter);
            }
        }
        executor.shutdown();
    }

}
