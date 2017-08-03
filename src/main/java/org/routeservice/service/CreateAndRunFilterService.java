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

/*import org.cher.entities.FilterEntity;
import org.cher.entities.FilterToRoute;
import org.cher.entities.Route;*/
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.routeservice.controller.RouteServiceController;
import org.routeservice.entity.FilterToRoute;
import org.routeservice.entity.Route;
import org.routeservice.factory.FilterFactory;
import org.routeservice.filter.Filter;
import org.routeservice.repository.FilterToRouteRepository;
import org.routeservice.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.ArrayList;
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
    private RouteRepository routeRepository;

    @Autowired
    private FilterToRouteRepository filterToRouteRepository;

    @Value("${threads}")
    @Setter
    private int numberOfThreads;

    public RequestEntity<?> CreateAndRunFilters(RequestEntity<?> request) {

        Route routeToRun = extractRoute(request.getHeaders());
        List<Integer> filterIndexList = checkForFilters(routeToRun);
        List<Filter> filterList = FilterFactory.CreateFilters(filterIndexList);
        runFilter(filterList, request, routeToRun);
        return request;
    }

    private Route extractRoute(HttpHeaders headers) {
        URI uri = Filter.getFullUri(headers);
        String host = uri.getHost();
        String protocol = uri.getScheme();
        String routeName;
        if(protocol==null){
            routeName=host;
        }
        else{
            routeName = new StringBuilder().append(protocol).append("://").append(host).toString();
        }
        Route routeToCheck = routeRepository.findDistinctFirstByRouteName(routeName);
        if(routeToCheck == null) {
          throw new IllegalStateException(String.format("unregisters route", routeName));
        }
        return routeToCheck;
    }

    private List<Integer> checkForFilters(Route routeToCheck) {
        List<Integer> filtersPerRoute = new ArrayList<>();
        List<FilterToRoute> filterToRouteList = filterToRouteRepository.findAllByRoute_RouteId(routeToCheck.getRouteId());
        if(filterToRouteList != null) {
            for (FilterToRoute filter : filterToRouteList) {
                filtersPerRoute.add(filter.getFilter().getFilterId());
            }
        }
        return filtersPerRoute;
    }

    private void runFilter(List<Filter> filterList, RequestEntity<?> request, Route route) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(Filter filter: filterList){
            filter.setRoute(route);
            filter.setRequestEntity(request);
            executor.execute(filter);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //TODO something
            }
        }
    }

}
