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
import lombok.Setter;
import org.routeservice.entity.Route;
import org.routeservice.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;

import java.util.List;

/**
 * Created by Cher on 13/07/2017.
 */
@AllArgsConstructor
public abstract class Filter implements Runnable{

    @Autowired
    @Setter
    private  PersistenceService service;

    @Getter
    private int filterId;

    @Setter
    private RequestEntity<?> requestEntity;

    @Setter
    private Route route;

    public Filter(int id){
        filterId = id;
    }

 /*   public void ActivateFilter(RequestEntity<?> request, Route route) {
        Map<String,String> problemsList =  CheckVulnerability(request);
        if(problemsList == null) {return ;}
        service.InsertIntoDB(route,filterId,problemsList,request);
    }
*/
    public abstract List<String> CheckVulnerability(RequestEntity<?> request);

    @Override
    public final void run() {
        try {
            RequestEntity<?> request = requestEntity;
            Route routeToCheck = route;
            List<String> problemsList =  CheckVulnerability(request);
            if(problemsList.isEmpty()) {return ;}
            Thread.sleep(5000);
            service.InsertIntoDB(routeToCheck,filterId,problemsList,request);
        }
        catch (Exception e) {
            //TODO write log...
        }
    }
}
