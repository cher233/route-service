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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Cher on 10/06/2017.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "filter_info", schema="route_service")
public class FilterEntity {

        @JsonIgnore
        @Id
        @Column(name = "filter_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int filterId;

        @JsonProperty("Filter")
        @Column(name = "filter_name", nullable = false, length = 100)
        private String filterName;
}
