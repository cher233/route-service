package org.routeservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nofar on 01/08/2017.
 */
@Slf4j
@Component
@Scope("prototype")
public class AuthenticationBypassFilter extends Filter {

    @Autowired
    public AuthenticationBypassFilter() {
        filterId = 2;
    }

    @Override
    public List<String> CheckVulnerability(RequestEntity<?> request) {
        log.debug("Started checking authentication bypass.");
        List<String> vulnerabilities = new ArrayList<>();
        URI uri = Filter.getFullUri(request.getHeaders());
        String path = uri.getRawPath();
        Pattern pattern = Pattern.compile(".*%([0-1][0-9]|20).*");
        Matcher matcher = pattern.matcher(path);
        boolean result = matcher.matches();
        if (result) {
            log.info("Found authentication bypass.");
            vulnerabilities.add(uri.toString());
        }
        else
            log.info("No authentication bypass found.");
        log.debug("Finished checking authentication bypass.");
        return vulnerabilities;
    }
}
