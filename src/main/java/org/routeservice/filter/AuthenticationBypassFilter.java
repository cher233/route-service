package org.routeservice.filter;

import org.routeservice.controller.RouteServiceController;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nofar on 01/08/2017.
 */
public class AuthenticationBypassFilter extends Filter {

    public AuthenticationBypassFilter(int filterId) {
        super(filterId);
    }

    @Override
    public List<String> CheckVulnerability(RequestEntity<?> request) {
        List<String> vulnerabilities = new ArrayList<>();
            URI uri = Filter.getFullUri(request.getHeaders());
            String path = uri.getRawPath();
            Pattern pattern = Pattern.compile(".*%([0-1][0-9]|20).*");
            Matcher matcher = pattern.matcher(path);
            boolean result = matcher.matches();
            if (result) {
                vulnerabilities.add(uri.toString());
            }
        return vulnerabilities;
    }
}
