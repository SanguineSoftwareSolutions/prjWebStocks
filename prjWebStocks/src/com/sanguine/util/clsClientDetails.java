/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class clsClientDetails {

    public static HashMap<String, clsClientDetails> hmClientDtl;
    private static final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
    public String id;
    public String Client_Name;
    public Date installDate;
    public Date expiryDate;
    public int intPropertyMachine;
    public int intUserNo;
    
    private clsClientDetails(String id, String Client_Name,Date installDate,Date expiryDate) {
        this.id = id;
        this.Client_Name = Client_Name;
        this.installDate=installDate;
        this.expiryDate = expiryDate;
        
    }
    private clsClientDetails(String id, String Client_Name,Date installDate,Date expiryDate,int intPropertyMachine,int intUserNo) {
        this.id = id;
        this.Client_Name = Client_Name;
        this.installDate=installDate;
        this.expiryDate = expiryDate;
        this.intPropertyMachine = intPropertyMachine;
        this.intUserNo = intUserNo;
    }
    public static clsClientDetails createClientDetails(String id, String Client_Name,Date installDate,Date expiryDate) {
        return new clsClientDetails(id, Client_Name, installDate,expiryDate);
    }
    
    public static clsClientDetails createClientDetails(String id, String Client_Name,Date installDate,Date expiryDate,int intPropertyMachine,int intUserNo) {
        return new clsClientDetails(id, Client_Name, installDate,expiryDate,intPropertyMachine,intUserNo);
    }
    
    public static void funAddClientCodeAndName() {
        try {
        	
            hmClientDtl = new HashMap<String, clsClientDetails>();
            
            hmClientDtl.put("001.001", clsClientDetails.createClientDetails("001.001", "Quick Bite",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.002", clsClientDetails.createClientDetails("001.002", "Irish House",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.003", clsClientDetails.createClientDetails("001.003", "CONTEMPORARY KITCHENS PVT LTD",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.004", clsClientDetails.createClientDetails("001.004", "M/S PALI PRESIDENCY",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.005", clsClientDetails.createClientDetails("001.005", "M/S CORUM HOSPITALITY",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.006", clsClientDetails.createClientDetails("001.006", "M/S. BOMBAY BRONX (THE SLICE OF WINE HOSP. LTD)",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.007", clsClientDetails.createClientDetails("001.007", "AQAISTION FOOD THE SHOCK FINE",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.008", clsClientDetails.createClientDetails("001.008", "M/S BEDI A.M. PVT. LTD",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.009", clsClientDetails.createClientDetails("001.009", "M/S HOTEL SAHIL PVT. LTD",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.010", clsClientDetails.createClientDetails("001.010", "M/S HOTEL SKY LARK (GANGSTAR)",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.011", clsClientDetails.createClientDetails("001.011", "M/S LITTLE DARLING",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.012", clsClientDetails.createClientDetails("001.012", "M/S MANGI CAFES PVT LIMITED",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.013", clsClientDetails.createClientDetails("001.013", "M/S FOOD CULTURE",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.014", clsClientDetails.createClientDetails("001.014", "M/S BARBEQUE NATION",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.015", clsClientDetails.createClientDetails("001.015", "M/S TIAN RESTAURANT",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.016", clsClientDetails.createClientDetails("001.016", "M/S RICE BOAT RESTAURANT",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.017", clsClientDetails.createClientDetails("001.017", "M/S VEDANGI BEER SHOP",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.018", clsClientDetails.createClientDetails("001.018", "M/S SAMRAT RESTAURANT AND BAR",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.019", clsClientDetails.createClientDetails("001.019", "M/S KPT HOSPITALITY P. LTD.",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.020", clsClientDetails.createClientDetails("001.020", "M/S IVY WINE",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.021", clsClientDetails.createClientDetails("001.021", "M/S GOURMET INVESTMENT PVT. LTD.(PIZZA EXPS.)",dFormat.parse("2015-03-01"),dFormat.parse("2016-03-30")));
            hmClientDtl.put("001.022", clsClientDetails.createClientDetails("001.022", "M/S WODE HOUSE GYMKHANA",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.023", clsClientDetails.createClientDetails("001.023", "M/S CAFE PARIS",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.024", clsClientDetails.createClientDetails("001.024", "M/S CREATIVE HOTELS PVT LTD",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.025", clsClientDetails.createClientDetails("001.025", "M/S VESHNAVI HOSPITALITY PVT LTD",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.026", clsClientDetails.createClientDetails("001.026", "M/S. BARBEQUE NATION HOSPITALITY LTD.",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.027", clsClientDetails.createClientDetails("001.027", "M/S. OM SHIVAM CONSTRUCTIONS (BOMBAY BARBEQUE REST.)",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.028", clsClientDetails.createClientDetails("001.028", "M/S. ROSE GARDEN HOTEL P. LTD. (SHUBHANGEN HOTEL).",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.029", clsClientDetails.createClientDetails("001.029", "M/S. GOURMET INVESTMENT PVT LTD (PIZZA EXPRESS)",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.030", clsClientDetails.createClientDetails("001.030", "M/S.NIDO RESTAURANT & BAR",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.031", clsClientDetails.createClientDetails("001.031", "M/S GOA PORTUGUESA REST. PVT. LTD.",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            hmClientDtl.put("001.032", clsClientDetails.createClientDetails("001.032", "M/S. HOTEL METRO PALACE PVT. LTD.",dFormat.parse("2015-08-01"),dFormat.parse("2016-08-30")));
            
            hmClientDtl.put("074.001", clsClientDetails.createClientDetails("074.001", "THE POONA CLUB LTD",dFormat.parse("2016-01-21"),dFormat.parse("2017-06-21")));
            
            
            //WebStocks Clients           
            hmClientDtl.put("000.000", clsClientDetails.createClientDetails("000.000", "Demo Company",dFormat.parse("2014-06-19"),dFormat.parse("2017-10-22")));
            hmClientDtl.put("024.001", clsClientDetails.createClientDetails("024.001", "Eden Cake Shop",dFormat.parse("2014-09-23"),dFormat.parse("2018-12-31")));
            hmClientDtl.put("044.001", clsClientDetails.createClientDetails("044.001", "HOTEL KAMAL PVT. LTD.",dFormat.parse("2015-01-20"),dFormat.parse("2095-02-21")));
            hmClientDtl.put("048.001", clsClientDetails.createClientDetails("048.001", "SHREE SIDDHIVINAYAK FOODS",dFormat.parse("2015-01-20"),dFormat.parse("2095-02-21")));
            hmClientDtl.put("060.001", clsClientDetails.createClientDetails("060.001", "FLYING SAUCER SKY BAR",dFormat.parse("2015-07-01"),dFormat.parse("2016-07-30")));
            hmClientDtl.put("060.002", clsClientDetails.createClientDetails("060.002", "Flying Demo",dFormat.parse("2015-07-01"),dFormat.parse("2016-07-30")));
//            hmClientDtl.put("080.001", clsClientDetails.createClientDetails("080.001", "PRIMEBIZ HOSPILTALITY LLP",dFormat.parse("2016-01-16"),dFormat.parse("2017-01-16")));
            hmClientDtl.put("105.001", clsClientDetails.createClientDetails("105.001", "PRIMEBIZ HOSPILTALITY LLP",dFormat.parse("2016-01-16"),dFormat.parse("2017-01-16")));
            hmClientDtl.put("080.001", clsClientDetails.createClientDetails("080.001", "KAREEMS",dFormat.parse("2016-03-01"),dFormat.parse("2017-03-31")));
            hmClientDtl.put("092.001", clsClientDetails.createClientDetails("092.001", "Shree Sound Pvt Ltd", dFormat.parse("2016-02-15"), dFormat.parse("2018-12-28"))); // waters
            hmClientDtl.put("096.001", clsClientDetails.createClientDetails("096.001", "Red Consulting Pvt Ltd",dFormat.parse("2016-01-16"),dFormat.parse("2016-04-01")));
            hmClientDtl.put("097.001", clsClientDetails.createClientDetails("097.001", "GADGIL HOTELS PVT LTD",dFormat.parse("2016-01-30"),dFormat.parse("2017-01-30")));
            hmClientDtl.put("097.002", clsClientDetails.createClientDetails("097.002", "GADGIL HOTELS PVT LTD",dFormat.parse("2016-04-11"),dFormat.parse("2016-07-15")));
            hmClientDtl.put("103.001", clsClientDetails.createClientDetails("103.001", "BOMBAY HIGH", dFormat.parse("2016-03-05"), dFormat.parse("2018-08-01")));  //extend one week exted  from closed   again 2017-08-25   // renew 2018      
            hmClientDtl.put("104.001", clsClientDetails.createClientDetails("104.001", "Bakers Treat", dFormat.parse("2016-03-07"), dFormat.parse("2017-04-07")));// HO Unit
            hmClientDtl.put("106.001", clsClientDetails.createClientDetails("106.001", "Independence Brewing Co. Pvt Ltd", dFormat.parse("2016-03-21"), dFormat.parse("2017-03-21")));
            hmClientDtl.put("107.001", clsClientDetails.createClientDetails("107.001", "UNITED CRANE COMPONENTS PVT. LTD.", dFormat.parse("2016-03-30"), dFormat.parse("2017-09-30"))); // for checking
            hmClientDtl.put("109.001", clsClientDetails.createClientDetails("109.001", "Chemistry 101", dFormat.parse("2016-04-11"), dFormat.parse("2017-04-11")));
            hmClientDtl.put("110.001", clsClientDetails.createClientDetails("110.001", "CAKE SHOP", dFormat.parse("2016-01-23"), dFormat.parse("2016-05-30")));
            
            hmClientDtl.put("111.001", clsClientDetails.createClientDetails("111.001", "MERWANS CONFECTIONERS PVT LTD", dFormat.parse("2016-05-07"), dFormat.parse("2017-12-30")));//HO
            hmClientDtl.put("112.001", clsClientDetails.createClientDetails("112.001", "GBC MEGA MOTELS", dFormat.parse("2016-05-11"), dFormat.parse("2017-05-11")));//HO Carnival
            hmClientDtl.put("114.001", clsClientDetails.createClientDetails("114.001", "Dr. Asif Khan Wellness Clinic LLP", dFormat.parse("2016-05-14"), dFormat.parse("2017-05-14")));//HO
            hmClientDtl.put("117.001", clsClientDetails.createClientDetails("117.001", "THE PREM'S HOTEL", dFormat.parse("2016-08-01"), dFormat.parse("2018-08-01")));//HO
            hmClientDtl.put("124.001", clsClientDetails.createClientDetails("124.001", "ATITHYA DINNING LLP", dFormat.parse("2016-11-04"), dFormat.parse("2018-10-04"))); // one month extends
            hmClientDtl.put("127.001", clsClientDetails.createClientDetails("127.001", "Cumin Food & Beverage Pvt Ltd", dFormat.parse("2016-10-26"), dFormat.parse("2017-10-26")));
            
            hmClientDtl.put("131.001", clsClientDetails.createClientDetails("131.001", "KETTLE AND KEG CAFE", dFormat.parse("2016-11-23"), dFormat.parse("2018-11-23")));
            
            hmClientDtl.put("132.001", clsClientDetails.createClientDetails("132.001", "JBDD Hospitality LLP", dFormat.parse("2016-12-05"), dFormat.parse("2017-08-30")));
            hmClientDtl.put("136.001", clsClientDetails.createClientDetails("136.001","KINKI", dFormat.parse("2017-01-05"), dFormat.parse("2018-01-05")));//released on 05-01-2017 for 1 year
            hmClientDtl.put("141.001", clsClientDetails.createClientDetails("141.001","SANGUINE SOFTWARE SOLUTIONS PVT LTD", dFormat.parse("2017-01-30"), dFormat.parse("2099-01-30")));
            hmClientDtl.put("148.001", clsClientDetails.createClientDetails("148.001","MURPHIES", dFormat.parse("2017-02-16"), dFormat.parse("2018-02-16") , 4,3));//release on 16-02-2017 for 1 year
            hmClientDtl.put("151.001", clsClientDetails.createClientDetails("151.001","Bottle Street Restaurant & Lounge", dFormat.parse("2017-02-23"), dFormat.parse("2018-03-27")));//  renewed on 27-02-2017 for 1 month 
            hmClientDtl.put("159.001", clsClientDetails.createClientDetails("159.001","GREEN BOX VENTURES LLP", dFormat.parse("2017-02-17"), dFormat.parse("2018-02-17")));
            hmClientDtl.put("155.001", clsClientDetails.createClientDetails("155.001","CAVALLI THE LOUNGE", dFormat.parse("2017-03-24"), dFormat.parse("2018-03-24"))); // extend as pos
            
            hmClientDtl.put("159.001", clsClientDetails.createClientDetails("159.001","BIG PLATE CUISINES LLP", dFormat.parse("2017-06-02"), dFormat.parse("2018-06-02")));
            
            hmClientDtl.put("161.001", clsClientDetails.createClientDetails("161.001", "HOTEL GRAND CENTRAL",dFormat.parse("2017-04-18"),dFormat.parse("2018-01-18"))); 
            
            hmClientDtl.put("163.001", clsClientDetails.createClientDetails("163.001","KADAR KHAN'S SHEESHA", dFormat.parse("2017-05-15"), dFormat.parse("2018-05-15")));
            hmClientDtl.put("166.001", clsClientDetails.createClientDetails("166.001","SUNNYS WORLD", dFormat.parse("2017-05-01"), dFormat.parse("2025-09-01")));
            
            hmClientDtl.put("169.001", clsClientDetails.createClientDetails("169.001","A R HOSPITALITY", dFormat.parse("2017-05-10"), dFormat.parse("2018-05-10")));
            
            hmClientDtl.put("172.001", clsClientDetails.createClientDetails("172.001","DIOS HOTEL LLP", dFormat.parse("2017-05-25"), dFormat.parse("2018-08-25"))); // one year extend
            
            hmClientDtl.put("174.001", clsClientDetails.createClientDetails("174.001","KRD Eateries Pvt Ltd", dFormat.parse("2017-06-06"), dFormat.parse("2017-10-31")));  // krimsons
            hmClientDtl.put("175.001", clsClientDetails.createClientDetails("175.001", "TJS BREW WORKS", dFormat.parse("2017-06-06"), dFormat.parse("2018-06-06")));
            hmClientDtl.put("178.001", clsClientDetails.createClientDetails("178.001", "UNWIND", dFormat.parse("2017-07-27"), dFormat.parse("2018-07-27")));  
            hmClientDtl.put("181.001", clsClientDetails.createClientDetails("181.001", "RMV COMMUNICATION PVT LTD", dFormat.parse("2017-06-20"), dFormat.parse("2017-07-20"))); 
            hmClientDtl.put("184.001", clsClientDetails.createClientDetails("184.001", "BALAJI TRADERS PVT LTD", dFormat.parse("2017-07-03"), dFormat.parse("2017-09-03")));
            
            hmClientDtl.put("189.001", clsClientDetails.createClientDetails("189.001", "CLASSIC BANGLES", dFormat.parse("2017-08-31"), dFormat.parse("2018-01-06"))); // for demo
            
            hmClientDtl.put("193.001", clsClientDetails.createClientDetails("193.001", "PRECISION FOOD WORKS", dFormat.parse("2017-08-25"), dFormat.parse("2018-08-25")));
            hmClientDtl.put("194.001", clsClientDetails.createClientDetails("194.001", "SWIG", dFormat.parse("2017-09-11"), dFormat.parse("2018-10-11"))); // Name change SWIG 2.0 and license Extends
            
            hmClientDtl.put("197.001", clsClientDetails.createClientDetails("197.001", "REZBERRY RHINOCERES", dFormat.parse("2017-09-20"), dFormat.parse("2018-09-20")));//renewed on 31-10-2017 for 1 year till 20-09-2018
            hmClientDtl.put("211.001", clsClientDetails.createClientDetails("211.001", "CHEFS CORNER", dFormat.parse("2017-11-29"), dFormat.parse("2017-12-30")));  // demo
            
            hmClientDtl.put("217.001", clsClientDetails.createClientDetails("217.001", "BURNT CRUST HOSPITALITY PVT LTD", dFormat.parse("2017-12-13"), dFormat.parse("2018-01-13")));
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
