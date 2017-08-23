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

package org.routeservice.repository;

import org.routeservice.entity.FilterFindings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Cher on 13/07/2017.
 */
@Repository
public interface FilterFindingsRepository extends JpaRepository<FilterFindings, Integer> {

    /* Queries based on route */

    List<FilterFindings> findAllByRoute_RouteNameAndRoute_Service_Id(String name, int serviceId);

    List<FilterFindings> findAllByRoute_RouteNameAndProblemDescription_FilterEntity_FilterIdAndRoute_Service_Id(String route, int filterId, int serviceId);

    @Query("SELECT ff FROM FilterFindings ff where ff.route.service.id = :serviceId and ff.route.routeName = :route and ff.additionalInfo.timeOfProblem between :startDate and :endDate")
    List<FilterFindings> findAllByRoute_RouteNameAndAdditionalInfo_TimeOfProblem(@Param("serviceId") int serviceId, @Param("route") String route,
                                                                                 @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT ff FROM FilterFindings ff where ff.route.service.id = :serviceId and  ff.route.routeName = :route and ff.additionalInfo.timeOfProblem between :startDate and :endDate and ff.problemDescription.filterEntity.filterId = :filterId")
    List<FilterFindings> findAllByRoute_RouteNameAndAdditionalInfo_TimeOfProblemAndProblemDescription_FilterEntity_FilterId(@Param("serviceId") int serviceId, @Param("route") String route,
                                                                                 @Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("filterId") int filterId);

    /* Queries based on space */

    List<FilterFindings> findAllByRoute_Service_SpaceGuidAndRoute_Service_Id(String space, int serviceId);

    List<FilterFindings> findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndProblemDescription_FilterEntity_FilterId(int serviceID, String space, int filterId);

    @Query("SELECT ff FROM FilterFindings ff where ff.route.service.id = :serviceId and ff.route.service.spaceGuid = :space and ff.additionalInfo.timeOfProblem between :startDate and :endDate")
    List<FilterFindings> findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndAdditionalInfo_TimeOfProblem(@Param("serviceId") int serviceId, @Param("space") String space,
                                                                                                            @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT ff FROM FilterFindings ff where ff.route.service.id = :serviceId and  ff.route.service.spaceGuid = :space and ff.additionalInfo.timeOfProblem between :startDate and :endDate and ff.problemDescription.filterEntity.filterId = :filterId")
    List<FilterFindings> findAllByRoute_Service_IdAndRoute_Service_SpaceGuidAndAdditionalInfo_TimeOfProblemAndProblemDescription_FilterEntity_FilterId(@Param("serviceId") int serviceId, @Param("space") String space,
                                                                                                                                                       @Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("filterId") int filterId);
}
