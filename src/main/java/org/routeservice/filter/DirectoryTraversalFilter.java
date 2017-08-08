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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.routeservice.controller.RouteServiceController;
import org.routeservice.service.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope("prototype")
public class DirectoryTraversalFilter extends Filter {

    @Autowired
    public DirectoryTraversalFilter() {
        filterId = 2;
    }

    @Override
    public List<String> CheckVulnerability(RequestEntity<?> request) {
        log.debug("Started checking directory traversal.");
        List<String> vulnerabilities = new ArrayList<>();
        URI uri = decodeUrl(request.getHeaders());
        URI uriToCompare = uri.normalize();
        if (!uri.equals(uriToCompare)) {
            log.info("Found directory traversal.");
            vulnerabilities.add(uri.toString());
        }
            log.debug("Finished checking directory traversal.");
            return vulnerabilities;
        }

    private URI decodeUrl(HttpHeaders httpHeaders){
        URI uri = Filter.getFullUri(httpHeaders);
        URI decodedUri = null;
        String url = uri.getSchemeSpecificPart().replaceAll("^//","").replaceAll(" ","");
        try { decodedUri = new URI(url); }
        catch (URISyntaxException e) {log.error("Invalid Uri: {}",e.getReason()); }
        return decodedUri;
        }
}


