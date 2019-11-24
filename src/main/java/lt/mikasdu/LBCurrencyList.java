package lt.mikasdu;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LBCurrencyList {
    private HashMap<String, Currency> currencyList = new HashMap<String, Currency>();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    LBCurrencyList() {
        generateCurrencyList();
    }


    void compareRatesByDates(String dateFrom, String dateTo) {
        updateRatesByDate(dateTo);
        for (Map.Entry<String, Currency> entry : currencyList.entrySet()) {
            String key = entry.getKey();
            currencyList.get(key).swapDateAndRate();
        }
        updateRatesByDate(dateFrom);

        for (Map.Entry<String, Currency> entry : currencyList.entrySet()) {
            String key = entry.getKey();
            Currency currency = currencyList.get(key);
            if (currency.getRateFrom() != null || currency.getRateTo() != null) {
                System.out.println("Name: " + currency.getShortName() +
                        " Full Name: " + currency.getFullEngName() +
                        " DateFrom: " + currency.getDateFrom() +
                        " RateFrom: " + currency.getRateFrom() +
                        " DateTo: " + currency.getDateTo() +
                        " Rate To: " + currency.getRateTo() +
                        " Rate Dif: " + currency.showRateDiff()
                );
            }
        }
    }

    public void showRateByDateForCurrency(String currency, String date) {
        updateRatesByDate(date);
        Currency tempCurrency = currencyList.get(currency);
        if (tempCurrency != null) {
            System.out.println("Name: " + tempCurrency.getShortName() +
                    " Full Name: " + tempCurrency.getFullEngName() +
                    " Date: " + tempCurrency.getDateFrom() +
                    " Rate: " + tempCurrency.getRateFrom()
            );
        } else {
            System.out.println("Pagal įvestą valiutos kodą nieko nerasta.");
        }
    }

    public void showRateByDate(String date) {
        updateRatesByDate(date);
        printOneDayList();
    }

    public void showTodayRates() {
        Date today = Calendar.getInstance().getTime();
        updateRatesByDate(format.format(today));
        printOneDayList();
    }

    private void printOneDayList() {
        for (Map.Entry<String, Currency> entry : currencyList.entrySet()) {
            String key = entry.getKey();
            Currency currency = currencyList.get(key);
            if (currency.getRateFrom() != null) {
                System.out.println("Name: " + currency.getShortName() +
                        " Full Name: " + currency.getFullEngName() +
                        " Date: " + currency.getDateFrom() +
                        " Rate: " + currency.getRateFrom()
                );
            }
        }
    }

    void updateRatesByDate(String date) {
        System.setProperty("http.agent", "Chrome");
        try {
            URL url = new URL("http://www.lb.lt//webservices/fxrates/FxRates.asmx/getFxRates?tp=EU&dt=" + date);
            Document doc = getDocument(url);
            NodeList nList = doc.getElementsByTagName("FxRate");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nNode;
                    String currencyShortName = el.getElementsByTagName("Ccy").item(1).getTextContent();
                    String currencyRate = el.getElementsByTagName("Amt").item(1).getTextContent();
                    String currencyDate = el.getElementsByTagName("Dt").item(0).getTextContent();
                    Currency currency = this.currencyList.get(currencyShortName);
                    currency.setRateFrom(currencyRate);
                    currency.setDateFrom(currencyDate);
                    currencyList.put(currencyShortName, currency);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void generateCurrencyList() {
        System.setProperty("http.agent", "Chrome");
        try {
            URL url = new URL("http://www.lb.lt//webservices/fxrates/FxRates.asmx/getCurrencyList?");
            Document doc = getDocument(url);
            NodeList nList = doc.getElementsByTagName("CcyNtry");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nNode;
                    String shortName = el.getElementsByTagName("Ccy").item(0).getTextContent();
                    String fullEngName = el.getElementsByTagName("CcyNm").item(1).getTextContent();
                    Currency currency = new Currency(shortName, fullEngName);
                    this.currencyList.put(shortName, currency);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Document getDocument(URL url) throws IOException, ParserConfigurationException, SAXException {
        URLConnection conn = url.openConnection();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(conn.getInputStream());
        doc.getDocumentElement().normalize();
        return doc;
    }

}
