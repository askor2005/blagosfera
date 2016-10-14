package ru.radom.kabinet.utils;

/**
 * Created by ebelyaev on 20.08.2015.
 */
public class IpUtils {

    public static long stringIpToLongIp(String stringIp) {
        long[] ip = new long[4];
        String[] parts = stringIp.split("\\.");
        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(parts[i]);
        }

        long longIp = 0;
        for (int i = 0; i < 4; i++) {
            longIp += ip[i] << (24 - (8 * i));
        }
        return longIp;
    }

    public static String longIpTostringIp(long longIp) {
        String[] parts = new String[4];
        for (int i = 0; i < 4; i++) {
            parts[i] = String.valueOf((((longIp)>>(8*(4-i-1)))&0x000000ff));
        }
        return org.apache.commons.lang3.StringUtils.join(parts,".");
    }

   public static boolean isValidIPv4(String strignIp) {
       return (strignIp!=null) &&  strignIp.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   }
}
