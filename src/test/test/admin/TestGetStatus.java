package test.admin;

import db.DBApiEntry;
import db.DBVal;
import db.Point;
import test.Globals;

import java.util.HashMap;

public class TestGetStatus {
    public static void main(String[] args) {
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host, port);

        long testBegin = System.currentTimeMillis();
        String response = entry.getStatus();
        long testEnd = System.currentTimeMillis();
        System.out.println(String.format("spend %d (ms)", testEnd-testBegin));

        boolean isShowResult = true;
        if(isShowResult) {
            System.out.println(response);
        }
    }
}
