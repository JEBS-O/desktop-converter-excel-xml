import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.*;

public class XmlCreator {
    private File priceFile;
    private Map<String, Long> categories;
    private List<String> parametersName;

    private XmlCreator() {}

    public XmlCreator(File priceFile) {
        this.priceFile = priceFile;
        categories = fillCategoriesMap();
        parametersName = fillParametersList();
    }

    public String createXml() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(addStartOfFile());
        buffer.append(addShopInfo());
        buffer.append(addCurrencies());
        buffer.append(addCategories());
        buffer.append(addOffers());
        buffer.append(addEndOfFile());
        return buffer.toString();
    }


    public String addStartOfFile() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<yml_catalog date=\"" + DateCreator.getDateForXmlFile() + "\">\n" +
                "<shop>\n";
    }

    public String addShopInfo() {
        StringBuffer buffer = new StringBuffer();
        try {
            ExcelReader reader = new ExcelReader(priceFile, 1);
            buffer.append("<name>" + reader.getCellValue(0, 1) + "</name>\n");
            buffer.append("<company>" + reader.getCellValue(1, 1) + "</company>\n");
            buffer.append("<url>" + reader.getCellValue(2, 1) + "</url>\n\n");
        } catch(IllegalArgumentException e) {
            Graphic.setErrorLabel("Лист(2) в таблице не найдет (с информацией о магазине)");
            Logs.print(e.getMessage());
        }
        return buffer.toString();
    }

    public String addCurrencies() {
        StringBuffer buffer = new StringBuffer();
        try {
            ExcelReader reader = new ExcelReader(priceFile, 2);
            buffer.append("<currencies>\n");
            for (Row row : reader.getAllRows(1)) {
                buffer.append("<currency id=\"" + ExcelReader.getCellValueByIndex(row, 0) + "\" rate=\"" + ExcelReader.getCellValueByIndex(row, 1) + "\" />\n");
            }
            buffer.append("</currencies>\n\n");
        } catch(IllegalArgumentException e) {
            Graphic.setErrorLabel("Лист(3) в таблице не найдет (с информацией о валюте)");
            Logs.print(e.getMessage());
        }
        return buffer.toString();
    }

    public String addCategories() {
        StringBuffer buffer = new StringBuffer();
        try {
            ExcelReader reader = new ExcelReader(priceFile, 3);
            buffer.append("<categories>\n");
            for (Row row : reader.getAllRows(1)) {
                if (ExcelReader.getCellValueByIndex(row, 2).equals(""))
                    buffer.append("<category id=\"" + Long.parseLong(ExcelReader.getCellValueByIndex(row, 1)) + "\">" + ExcelReader.getCellValueByIndex(row, 0).split(" \\(")[0] + "</category>\n");
                else
                    buffer.append("<category id=\"" + Long.parseLong(ExcelReader.getCellValueByIndex(row, 1)) + "\" parentId=\"" + Long.parseLong(ExcelReader.getCellValueByIndex(row, 2)) + "\">" + ExcelReader.getCellValueByIndex(row, 0).split(" \\(")[0] + "</category>\n");
            }
            buffer.append("</categories>\n\n");
        } catch(IllegalArgumentException e) {
            Graphic.setErrorLabel("Лист(4) в таблице не найдет (с информацией о категориях)");
            Logs.print(e.getMessage());
        }
        return buffer.toString();
    }

    public String addOffers() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<offers>\n");
        try {
            ExcelReader reader = new ExcelReader(priceFile, 0);
            for (Row row : reader.getAllRows(1)) {
                System.out.println("Количество товаров: " + reader.getAllRows(1).size());
                String id = "" + Integer.parseInt(ExcelReader.getCellValueByIndex(row, 0));
                Logs.print(ExcelReader.getCellValueByIndex(row, 1));
                boolean available = ExcelReader.getCellValueByIndex(row, 1).toCharArray().length == 4;
                String url = ExcelReader.getCellValueByIndex(row, 6);
                String price = ExcelReader.getCellValueByIndex(row, 8);
                String priceOld = ExcelReader.getCellValueByIndex(row, 7);
                String currencyId = ExcelReader.getCellValueByIndex(row, 9);
                String categoryId = getCategoryIdByName(ExcelReader.getCellValueByIndex(row, 5));
                String stockQuantity = ExcelReader.getCellValueByIndex(row, 11);
                List<String> picturesList = fillPicturesList(ExcelReader.getCellValueByIndex(row, 10));
                String name = ExcelReader.getCellValueByIndex(row, 2);
                String vendor = ExcelReader.getCellValueByIndex(row, 4);
                String description = ExcelReader.getCellValueByIndex(row, 3);
                Map<String, String> parameters = fillParametersMap(row);


                buffer.append(addStartOfOffer(id, available));
                buffer.append(addUrl(url));
                buffer.append(addPrice(price));
                buffer.append(addPriceOld(priceOld));
                buffer.append(addCurrencyId(currencyId));
                buffer.append(addCategoryId(categoryId));
                buffer.append(addStockQuantity(stockQuantity));
                buffer.append(addPictures(picturesList));
                buffer.append(addName(name));
                buffer.append(addVendor(vendor));
                buffer.append(addDescription(description));
                buffer.append(addParameters(parameters));
                buffer.append(addEndOfOffer());
            }
        } catch(NullPointerException e) {
            e.printStackTrace();
            Logs.print(e.getMessage());
        } finally {
            buffer.append("</offers>\n");
            return buffer.toString();
        }
    }

    public String addEndOfFile() {
        return "</shop>\n" +
                "</yml_catalog>";
    }


    /*
    Inside`s functions
     */
    private Map<String, Long> fillCategoriesMap() {
        Map<String, Long> categories = new HashMap<>();
        System.out.println(new ExcelReader(priceFile, 3).getAllRows(1).size());
        for(Row row : new ExcelReader(priceFile, 3).getAllRows(1)) {
            categories.put(ExcelReader.getCellValueByIndex(row, 0), Long.parseLong(ExcelReader.getCellValueByIndex(row, 1)));
        }
        for(Map.Entry<String, Long> pair : categories.entrySet()) {
            System.out.println(pair.getKey());
            System.out.println(pair.getValue());
        }
        return categories;
    }

    private List<String> fillParametersList() {
        List<String> parameters = new ArrayList<>();
        Row row = new ExcelReader(priceFile,0).getAllRows(0).get(0);

        for(int i = 12;i < row.getLastCellNum();i++) {
            parameters.add(ExcelReader.getCellValueByIndex(row, i));
        }

        return parameters;
    }

    private Map<String, String> fillParametersMap(Row offerRow) {
        Map<String, String> parametersMap = new HashMap<String, String>();

        for(int i=12;i<offerRow.getLastCellNum();i++) {
            String value = ExcelReader.getCellValueByIndex(offerRow, i);
            if(!value.equals("")) {
                parametersMap.put(parametersName.get(i-12), value);
            }
        }
        return parametersMap;
    }

    private List<String> fillPicturesList(String pictures) {
        return Arrays.asList(pictures.split(", "));
    }

    private String getCategoryIdByName(String categoryName) {
        return "" + categories.get(categoryName);
    }


    /*
    Offer`s functions
     */
    private String addStartOfOffer(String id, boolean available) {
        return "<offer id=\"" + id + "\" available=\"" + available + "\">\n";
    }
    private String addUrl(String URL) {
        if(!URL.equals(""))
            return "<url>" + URL + "</url>\n";
        else
            return "";
    }
    private String addPrice(String price) {
        return "<price>" + price + "</price>\n";
    }
    private String addPriceOld(String priceOld) {
        if(!priceOld.equals(""))
            return "<price_old>" + priceOld + "</price_old>\n";
        else
            return "";
    }
    private String addCurrencyId(String currencyId) {
        return "<currencyId>" + currencyId + "</currencyId>\n";
    }
    private String addCategoryId(String categoryId) {
        return "<categoryId>" + categoryId + "</categoryId>\n";
    }
    private String addPictures(List<String> pictures) {
        StringBuffer buffer = new StringBuffer();
        for(String picture : pictures) {
            buffer.append("<picture>");
            buffer.append(picture);
            buffer.append("</picture>\n");
        }
        return buffer.toString();
    }
    private String addName(String name) {
        return "<name>" + name + "</name>\n";
    }
    private String addVendor(String vendor) {
        return "<vendor>" + vendor + "</vendor>\n";
    }
    private String addDescription(String description) {
        return "<description>\n<![CDATA[<p>" + description.replaceAll("\n","</p><p>") + "</p>]]>\n</description>\n";
        //return "<description>\n<![CDATA[\n" + description + "\n]]>\n</description>\n";
    }
    private String addStockQuantity(String stockQuantity) {
        return "<stock_quantity>" + stockQuantity + "</stock_quantity>\n";
    }
    private String addParameters(Map<String, String> parameters) {
        StringBuffer buffer = new StringBuffer();
        for(Map.Entry<String, String> pair : parameters.entrySet()) {
            if(pair.getValue().contains(";")) {
                buffer.append("<param name=\"" + pair.getKey() + "\"><![CDATA[" + pair.getValue().replaceAll(";","</br>") + "]]></param>\n");
            }
            else {
                buffer.append("<param name=\"" + pair.getKey() + "\">" + pair.getValue() + "</param>\n");
            }
        }
        return buffer.toString();
    }
    private String addEndOfOffer() {
        return "</offer>\n\n";
    }
}
