package com.xmo.demo.java.dns;

public class DNSSetting {

    static {
        // the pice of code should be run before any code that try to using dns function
        String dnsServer = "192.168.128.10";
        if (!dnsServer.isEmpty()) {
            System.setProperty("sun.net.spi.nameservice.nameservers", dnsServer);
            System.setProperty("sun.net.spi.nameservice.provider.2", "dns,sun");
            System.setProperty("sun.net.spi.nameservice.provider.3", "default");

            String searchDomain = "default.svc.cluster.local svc.cluster.local cluster.local";
            if (!searchDomain.isEmpty()) {
                System.setProperty("sun.net.spi.nameservice.domain", searchDomain);
            }
        }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
