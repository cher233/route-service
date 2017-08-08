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

package org.routeservice.factory;

import org.routeservice.filter.AuthenticationBypassFilter;
import org.routeservice.filter.DirectoryTraversalFilter;
import org.routeservice.filter.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FilterFactory {

    public static List<Filter> CreateFilters(List<Integer> filterIdList)
    {
        List<Filter> filterList  = new ArrayList<>();
        if(filterIdList.get(0) == 0){
            filterIdList.clear();
            Collections.addAll(filterIdList, 1, 2, 3, 4);
        }
        for(Integer filterId: filterIdList){
            filterList.add(generateFilter(filterId));
        }
        return filterList;
    }

    private static Filter generateFilter(int filterId){
        switch (filterId)
        {
            case 1:
                return new DirectoryTraversalFilter();
            case 2:
                return new AuthenticationBypassFilter();
        }
        return null;
    }

}
