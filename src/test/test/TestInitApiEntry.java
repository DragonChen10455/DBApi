package test;

import db.DBApiEntry;

public class TestInitApiEntry {
    public static void main(String[] args) {
        String host = Globals.HOST;
        int port = Globals.port;
        DBApiEntry entry = DBApiEntry.initApiEntry(host,port);

        //修改命名空间，支持多租户
        entry.setNameSpace(1);
    }

}
