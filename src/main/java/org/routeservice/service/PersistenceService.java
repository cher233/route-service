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
import org.routeservice.repository.AdditionalInfoRepository;
import org.routeservice.repository.FilterFindingsRepository;
import org.routeservice.repository.FilterRepository;
import org.routeservice.repository.ProblemDescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cher on 13/07/2017.
 */

@Component
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

    public void InsertIntoDB(Route routeToInsert, int filerId, List<String> problemList, long dateOfrequest,
                             URI fullUri,String originIp){
        AdditionalInfo additionalInfo = createAdditionalInfo(dateOfrequest, originIp,fullUri);
        if(additionalInfoRepository.save(additionalInfo)!=null){
            List<FilterFindings> filterFindingsList = new ArrayList<>();
            for(String problem: problemList){
                filterFindingsList.add(createFilterFindings(routeToInsert,additionalInfo,createProblemDescription(filerId, problem)));
            }
            filterFindingsRepository.save(filterFindingsList);
        }
    }

        private FilterFindings createFilterFindings(Route routeToInsert, AdditionalInfo additionalInfo,
                                                    ProblemDescription problemDescription) {
        FilterFindings filterFindings = FilterFindings.builder().
                additionalInfo(additionalInfo).
                route(routeToInsert).
                problemDescription(problemDescription).
                fixed(false).
                build();
        return filterFindings;
    }

    private ProblemDescription createProblemDescription(int filterId, String problem) {
        ProblemDescription problemDescription=null;
        FilterEntity filter = filterRepository.getOne(filterId);
            problemDescription = ProblemDescription.builder().
                    filterEntity(filter).
                    description(problem).
                    build();
        problemDescriptionRepository.save(problemDescription);
        return problemDescription;
    }

    private AdditionalInfo createAdditionalInfo(long date, String originIp, URI fullUri) {
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

    public List<FilterFindings> getAllProblemsForRoute(String route){
        return null;
    }


}
