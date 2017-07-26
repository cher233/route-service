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

import org.routeservice.controller.RouteServiceController;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class DirecoryTraversalFilter extends Filter {

    public DirecoryTraversalFilter(int filterId) {
        super(filterId);
    }

    @Override
    public List<String> CheckVulnerability(RequestEntity<?> request) {
        List<String> vulnerabilities = new ArrayList<>();
        String url = request.getHeaders().get(RouteServiceController.FORWARDED_URL).get(0);
        url = url.replaceAll("%2e",".").replaceAll("%2f","/");
        try {
            URI uri  = new URI(url);
            URI uriToComapre = uri.normalize();
            if(!uri.equals(uriToComapre)){
                vulnerabilities.add(url);
            }
        } catch (URISyntaxException e) {
            //TODO PRINT LOG
        }
        return vulnerabilities;
    }
}
