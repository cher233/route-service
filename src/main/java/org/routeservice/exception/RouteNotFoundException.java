package org.routeservice.exception;

public class RouteNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -914571358227517785L;

    public RouteNotFoundException(String route){
        super("End destination route: " + route+ ", was not bound to Service");
    }

}
