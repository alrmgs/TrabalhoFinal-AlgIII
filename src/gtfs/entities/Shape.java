package gtfs.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import app.helper.GPSCoordinate;

public class Shape extends EntityBase {

    private List<GPSCoordinate> vertexList;

    public Shape(String id) {
        super(id);
        vertexList = new LinkedList<GPSCoordinate>();
    }

    public void appendPoint(GPSCoordinate point) {
        vertexList.add(point);
    }

    @Override
    public String toString() {
        String res = "";
        int seq = 1;
        for (GPSCoordinate c : vertexList) {
            res += String.format(Locale.US, "%s,%.8g,%.8g,%d\n",
                    getId(), c.latitude, c.longitude, seq++);
        }
        return res;
    }
}
