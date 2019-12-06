package com.mahmoudshabat.restaurants.utils;




public abstract class Constant {

    public static final String PACKAGE_NAME = "com.mahmoudshabat.restaurants";
    public static final String DYNAMIC_LINKS_PREFIX  = "https://shabatrestaurants.page.link";


    public static final int LOCATION_REQUEST = 100 ;
    public static final int GPS_REQUEST = 101 ;


    public interface ERROR_CODES {
        public static final int NOT_FOUND = 404;
    }

    public enum SORT {
        ASC,
        DESC
    }


}
