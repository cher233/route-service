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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.routeservice.controller.RouteServiceController;
import org.routeservice.entity.Route;
import org.routeservice.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Cher on 13/07/2017.
 */
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Service
public abstract class Filter implements Runnable{

    @Setter
    @Autowired
    private  PersistenceService service;

    @Getter
    protected int filterId;

    @Setter
    private RequestEntity<?> requestEntity;

    @Setter
    private Route route;

    @Value("${sleepFilter}")
    @Setter
    private int sleep;

    public Filter(int id){
        filterId = id;
    }

    public abstract List<String> CheckVulnerability(RequestEntity<?> request);

    @Override
    public final void run() {
            log.debug("Filter started running...");
            RequestEntity<?> request = requestEntity;
            Route routeToCheck = route;
            List<String> problemsList =  CheckVulnerability(request);
            if(problemsList.isEmpty()) {
                return ;
            }
            long date = request.getHeaders().getDate();
            String origin = request.getHeaders().getOrigin();
            URI fullURI = getFullUri(request.getHeaders());
            //service = new PersistenceService();
            service.InsertIntoDB(routeToCheck,filterId,problemsList,date,fullURI,origin);
            log.debug("Filter finished running.");
        }

    public static URI getFullUri(HttpHeaders httpHeaders){
        URI uri = null;
        String url = httpHeaders.get(RouteServiceController.FORWARDED_URL).get(0);
        try {
            uri  = new URI(url);
            } catch (URISyntaxException e1) {
            log.error("Invalid uri found as destination!");
        }
        return uri;
    }
}
