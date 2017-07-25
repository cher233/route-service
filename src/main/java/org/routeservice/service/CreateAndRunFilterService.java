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
import org.routeservice.controller.RouteServiceController;
import org.routeservice.entity.FilterToRoute;
import org.routeservice.entity.Route;
import org.routeservice.filter.Filter;
import org.routeservice.repository.FilterToRouteRepository;
import org.routeservice.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CreateAndRunFilterService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private FilterToRouteRepository filterToRouteRepository;

    public RequestEntity<?> CreateAndRunFilters(RequestEntity<?> request) {

        Route routeToRun = extractRoute(request.getHeaders());
        List<Integer> filterIndexList = checkForFilters(routeToRun);
        //TODO use Filterfactory and get filter list
        List<Filter> filterList = null;
        runFilter(filterList, request, routeToRun);
        return request;
    }

    private Route extractRoute(HttpHeaders headers) {
        URI uri = headers.remove(RouteServiceController.FORWARDED_URL).stream()
            .findFirst()
            .map(URI::create)
            .orElseThrow(() -> new IllegalStateException(String.format("No %s header present", RouteServiceController.FORWARDED_URL)));
        String host = uri.getHost();
        String protocol = uri.getScheme();
        String routeName = new StringBuilder().append(protocol).append(host).toString();
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
