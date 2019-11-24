package lt.mikasdu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Currency {
    private String shortName;
    private String fullEngName;
    private Double rateFrom;
    private Date dateFrom;
    private Date dateTo;
    private Double rateTo;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");


    Currency(String shortName, String fullEngName) {
        this.shortName = shortName;
        this.fullEngName = fullEngName;
    }

    void swapDateAndRate() {
        this.dateTo = this.dateFrom;
        this.rateTo = this.rateFrom;
    }

    Double showRateDiff() {
        return rateTo - rateFrom;
    }

    Double getRateFrom() {
        return rateFrom;
    }

    void setRateFrom(String rateFrom) {
        this.rateFrom = Double.parseDouble(rateFrom);
    }


    public String getDateTo() {
        return formatter.format(this.dateTo);
    }

    Double getRateTo() {
        return rateTo;
    }

    String getDateFrom() {
        return formatter.format(this.dateFrom);
    }

    void setDateFrom(String dateFrom) {
        this.dateFrom = dateFormat(dateFrom);
    }

    private Date dateFormat(String date) {
        Date d = new Date();
        try {
            d = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    String getShortName() {
        return shortName;
    }

    String getFullEngName() {
        return fullEngName;
    }

}
