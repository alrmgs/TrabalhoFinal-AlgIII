package gtfs.services;

import app.helper.GPSCoordinate;
import gtfs.entities.Route;
import gtfs.entities.Stop;
import gtfs.entities.Trip;

import java.util.ArrayList;
import java.util.Map;

public class Router {

    private Map<String, Stop> stops;
    private GPSCoordinate start;
    private GPSCoordinate end;

    public class Step {

        private Route route;
        private Stop stopOrigin;
        private Stop stop;

        public Step(Route route, Stop stopOrigin, Stop stop) {
            this.route = route;
            this.stopOrigin = stopOrigin;
            this.stop = stop;
        }

        public Route getRoute() {
            return route;
        }

        public Stop getStopOrigin() {
            return stopOrigin;
        }

        public Stop getStop() {
            return stop;
        }
    }

    public Router(GPSCoordinate start, GPSCoordinate end, Map<String, Stop> stops) {
        this.start = start;
        this.end = end;
        this.stops = stops;
    }

    public ArrayList<Step> route() {
        ArrayList<Step> steps = new ArrayList<>();

        findPath(nearStop(start), steps);

        return steps;
    }

    public void findPath(Stop stop, ArrayList<Step> steps) {
        findPath(stop, steps, 999999999);
    }

    public void findPath(Stop stopA, ArrayList<Step> steps, double minDisToTarget) {
        Stop nearestStop = null;
        Route routeUsed = null;

        double minDis = 999999999;

        for (Trip trip : stopA.getTrips()) {
            Stop stopB = trip.nearestStop(end);
            double dis = end.distance(stopB.getGPSCoordinate());

            if (dis < minDis) {
                nearestStop = stopB;
                routeUsed = trip.getRoute();
                minDis = dis;
            }
        }

        if (minDis < minDisToTarget) {
            steps.add(new Step(routeUsed, stopA, nearestStop));
            findPath(nearestStop, steps, minDis);
        } else {
            Stop closerStop = closerStopToTarget(nearestStop, minDisToTarget);

            if (null != closerStop) {
                findPath(closerStop, steps);
            }
        }
    }

    private Stop closerStopToTarget(Stop nearestStop, double minDisToTarget) {
        Stop closerStop = null;
        for (Stop stop : stops.values()) {
            double distance = stop.distance(nearestStop);

            distance += stop.distance(end);

            if (distance < minDisToTarget) {
                minDisToTarget = distance;
                closerStop = stop;
            }
        }

        return closerStop;
    }

    public Stop nearStop(GPSCoordinate place) {
        Stop stop = null;
        double minDis = 999999999;

        for (Stop s : stops.values()) {
            double dis = s.distance(place);

            if (dis < minDis) {
                stop = s;
                minDis = dis;
            }
        }

        return stop;
    }
}
