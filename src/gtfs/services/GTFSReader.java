package gtfs.services;

import app.helper.CSVReader;
import app.helper.GPSCoordinate;

import gtfs.entities.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static gtfs.entities.Service.Weekday.*;

public class GTFSReader {

    public static Map<String, Stop> loadStops(String filename)
            throws FileNotFoundException {
        Map<String, Stop> stops = new HashMap<>();

        CSVReader reader = new CSVReader(filename, ",");
        while (reader.hasNext()) {
            String id = reader.next();
            reader.skipNext();
            String name = reader.next();
            reader.skipNext();
            double lat = reader.nextDouble();
            double lon = reader.nextDouble();
            stops.put(id, new Stop(id, name, lat, lon));
        }
        return stops;
    }

    public static Map<String, Route> loadRoutes(String filename)
            throws FileNotFoundException {
        Map<String, Route> routes = new HashMap<>();

        CSVReader reader = new CSVReader(filename, ",");
        while (reader.hasNext()) {
            String id = reader.next();
            reader.skipNext();
            String shortname = reader.next();
            String longname = reader.next();
            reader.skipNext(5);
            routes.put(id, new Route(id, shortname, longname));
        }

        return routes;
    }

    public static Map<String, Shape> loadShapes(String filename)
            throws FileNotFoundException {
        Map<String, Shape> shapes = new HashMap<>();
        CSVReader reader = new CSVReader(filename, ",");
        String id = null, oldid = "";
        Shape s = null;
        while (reader.hasNext()) {
            id = reader.next();
            if (!id.equals(oldid)) {
                if (s != null) {
                    shapes.put(id, s);
                }
                s = new Shape(id);
                oldid = id;
            }
            double lat = reader.nextDouble();
            double lon = reader.nextDouble();
            s.appendPoint(new GPSCoordinate(lat, lon));
            reader.nextInt();
        }
        if (s != null) {
            shapes.put(id, s);
        }
        return shapes;
    }

    public static Map<String, Service> loadServices(String filename)
            throws FileNotFoundException {
        Map<String, Service> services = new HashMap<>();
        CSVReader reader = new CSVReader(filename, ",");
        while (reader.hasNext()) {
            String id = reader.next();
            Service service = new Service(id);
            service.setAvailable(MONDAY, (reader.nextInt() == 1));
            service.setAvailable(TUESDAY, (reader.nextInt() == 1));
            service.setAvailable(WEDNESDAY, (reader.nextInt() == 1));
            service.setAvailable(THURSDAY, (reader.nextInt() == 1));
            service.setAvailable(FRIDAY, (reader.nextInt() == 1));
            service.setAvailable(SATURDAY, (reader.nextInt() == 1));
            service.setAvailable(SUNDAY, (reader.nextInt() == 1));
            service.setStartDate(StringToDate(reader.next()));
            service.setEndDate(StringToDate(reader.next()));
            services.put(id, service);
        }
        return services;
    }

    private static LocalDate StringToDate(String data) {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(data, format);
    }

    public static Map<String, Trip> loadTrips(String filename,
            Map<String, Route> routes, Map<String, Service> services,
            Map<String, Shape> shapes)
            throws FileNotFoundException {
        Map<String, Trip> trips = new HashMap<>();
        CSVReader reader = new CSVReader(filename, ",");
        while (reader.hasNext()) {
            String route_id = reader.next();
            String service_id = reader.next();
            String id = reader.next();
            reader.skipNext(2);
            int dir = reader.nextInt();
            reader.skipNext();
            String shape_id = reader.next();
            boolean w = (reader.nextInt() == 1);
            for (int i = 9; i < reader.getRecordSize(); i++) {
                reader.skipNext();
            }

            Route r = searchObject(routes, route_id);
            Service s = searchObject(services, service_id);
            Shape sh = searchObject(shapes, shape_id);

            trips.put(id, new Trip(id, r, s, sh, dir, w));
        }
        return trips;
    }

    private static <K, T extends EntityBase>
            T searchObject(Map<K, T> map, String id) {
        return map.get(id);
    }

    public static void loadStopTimes(String filename, Map<String, Trip> trips,
            Map<String, Stop> stops)
            throws FileNotFoundException {
        CSVReader reader = new CSVReader(filename, ",");
        String lastTripId = null;
        Trip lastTrip = null;
        while (reader.hasNext()) {
            String trip_id = reader.next();
            reader.skipNext(2);
            String stop_id = reader.next();
            reader.skipNext();
            if (lastTrip == null || !trip_id.equals(lastTripId)) {
                Trip trip = searchObject(trips, trip_id);
                lastTrip = trip;
            }
            Stop stop = searchObject(stops, stop_id);
            stop.addTrip(lastTrip);
            lastTrip.addStop(stop);
        }
    }
}
