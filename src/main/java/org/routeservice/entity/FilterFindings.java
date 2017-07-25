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

package org.routeservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
//import org.cher.entities.Route;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by Cher on 13/07/2017.
 */

@Entity
@Builder
@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
@Table(name = "filter_findings", schema="route_service")
public class FilterFindings {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Setter
    @OneToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @NonNull
    @Setter
    @OneToOne
    @JoinColumn(name = "problem_id")
    private ProblemDescription problemDescription;

    @NonNull
    @Setter
    @OneToOne
    @JoinColumn(name = "additional_info")
    private AdditionalInfo additionalInfo;

    @NonNull
    @Column(name = "fixed", nullable = false)
    private boolean fixed;

}