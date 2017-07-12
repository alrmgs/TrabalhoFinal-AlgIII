package gtfs.entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Service extends EntityBase {

    private boolean[] available;

    private LocalDate startDate;
    private LocalDate endDate;

    public enum Weekday {
        MONDAY(0),
        TUESDAY(1),
        WEDNESDAY(2),
        THURSDAY(3),
        FRIDAY(4),
        SATURDAY(5),
        SUNDAY(6);
        public final int index;

        private Weekday(int index) {
            this.index = index;
        }
    }

    public Service(String id) {
        super(id);
        this.available = new boolean[7];
        for (int i = 0; i < 7; i++) {
            this.available[i] = true;
        }
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setAvailable(Weekday day, boolean value) {
        this.available[day.index] = value;
    }

    public boolean isAvailable(Weekday day) {
        return this.available[day.index];
    }

    @Override
    public String toString() {
        String sdate = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String edate = endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String res = "";
        for (boolean b : available) {
            res += (b ? "1," : "0,");
        }
        return String.format("%s,%s%s,%s", getId(), res, sdate, edate);
    }
}
