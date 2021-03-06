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

import lombok.extern.slf4j.Slf4j;
import org.routeservice.entity.*;
import org.routeservice.filter.Filter;
import org.routeservice.repository.AdditionalInfoRepository;
import org.routeservice.repository.FilterFindingsRepository;
import org.routeservice.repository.FilterRepository;
import org.routeservice.repository.ProblemDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cher on 13/07/2017.
 */

@Service
@Transactional
@Slf4j
public class PersistenceService {

    @Autowired
    private FilterFindingsRepository filterFindingsRepository;

    @Autowired
    private ProblemDescriptionRepository problemDescriptionRepository;

    @Autowired
    private AdditionalInfoRepository additionalInfoRepository;

    @Autowired
    private FilterRepository filterRepository;

    public void InsertIntoDB(Route routeToInsert, int filerId, List<String> problemList, long dateOfRequest,
                             URI fullUri,String originIp){
        log.info("Entering Problems to DB...");
        AdditionalInfo additionalInfo = createAdditionalInfo(dateOfRequest, originIp,fullUri);
        if(additionalInfoRepository.save(additionalInfo)!=null){
            log.info("Saved additional info entity: {}", additionalInfo.toString());
            List<FilterFindings> filterFindingsList = new ArrayList<>();
            for(String problem: problemList){
                log.debug("Creating Problem List");
                filterFindingsList.add(createFilterFindings(routeToInsert,additionalInfo,createProblemDescription(filerId, problem)));
            }
            log.debug("Started Saving...");
            filterFindingsRepository.save(filterFindingsList);
            log.info("Saved: {} To DB",filterFindingsList.toString());
        }
    }

    private FilterFindings createFilterFindings(Route routeToInsert, AdditionalInfo additionalInfo,
                                                    ProblemDescription problemDescription) {
        log.debug("Creating filter findings entity.");
        FilterFindings filterFindings = FilterFindings.builder().
                additionalInfo(additionalInfo).
                route(routeToInsert).
                problemDescription(problemDescription).
                fixed(false).
                build();
        return filterFindings;
    }

    private ProblemDescription createProblemDescription(int filterId, String problem) {
        log.debug("Creating Problem description entity.");
        ProblemDescription problemDescription;
        FilterEntity filter = filterRepository.getOne(filterId);
        problemDescription = ProblemDescription.builder().
                filterEntity(filter).
                description(problem).
                build();
        log.debug("Saving problem description entity...");
        problemDescriptionRepository.save(problemDescription);
        log.info("Saved problem description entity: {}",problemDescription.toString());
        return problemDescription;
    }

    private AdditionalInfo createAdditionalInfo(long date, String originIp, URI fullUri) {
        log.debug("Creating additional information entity.");
        Date timeOfRequest;
        if(date != -1) {
            timeOfRequest = new Date(date * 1000);
        }else{ timeOfRequest = new Date(); }
        String SourceIp = originIp;
        String Uri = fullUri.toString();
        AdditionalInfo additionalInfoEntity = AdditionalInfo.builder().
                destinationUrl(Uri).
                sourceUrl(SourceIp).
                timeOfProblem(timeOfRequest).
                build();
        return additionalInfoEntity;
    }


    public List<FilterFindings> GetInformationByRoute(int serviceid, String route, Date startDate,  Date endDate,
                                                      int filter, int parameters){
        switch (parameters){
            case 0:
                return filterFindingsRepository.findAllByRoute_RouteNameAndRoute_Service_Id(route, serviceid);
            case 1:
                return filterFindingsRepository.findAllByRoute_RouteNameAndAdditionalInfo_TimeOfProblem(serviceid, route, startDate, endDate);
            case 2:
                return filterFindingsRepository.findAllByRoute_RouteNameAndProblemDescription_FilterEntity_FilterIdAndRoute_Service_Id(route, filter, serviceid);
            case 3:
                return filterFindingsRepository.findAllByRoute_RouteNameAndAdditionalInfo_TimeOfProblemAndProblemDescription_FilterEntity_FilterId(serviceid, route, startDate, endDate,filter);
        }
        return null;
    }

     public List<FilterFindings> GetInformationBySpace(int serviceid, String space, Date startDate,  Date endDate, int filter,int parameters){
         switch (parameters){
             case 0:
                 return filterFindingsRepository.findAllByRoute_Service_SpaceGuidAndRoute_Service_Id(space,serviceid);
             case 1:
                 return filterFindingsRepository.findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndAdditionalInfo_TimeOfProblem(serviceid,space,startDate,endDate);
             case 2:
                 return filterFindingsRepository.findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndProblemDescription_FilterEntity_FilterId(serviceid,space,filter);
             case 3:
                 return filterFindingsRepository.findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndAdditionalInfo_TimeOfProblemAndProblemDescription_FilterEntity_FilterId(
                         serviceid,space,startDate,endDate,filter);
         }
         return null;
    }


}
