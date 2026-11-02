package com.beedigital.beestore.service;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DELIBERATELY BAD CODE - for static-analysis demos (SonarQube, Checkstyle, PMD...).
 * Do not use in production.
 */
public class BadCodeExample {

    // SMELL: hardcoded credentials (security hotspot / vulnerability)
    public static String password = "admin123";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/beestore?user=root&password=root";
    String apiKey = "sk-live-1234567890abcdef";

    // SMELL: public mutable static field, non-final
    public static List<String> cache = new ArrayList<>();

    // SMELL: unused private field
    private int unusedCounter = 0;

    // SMELL: unused variable names, non-descriptive names
    public double calc(double a, double b, int c) {
        double x = 0;
        int unused = 42;
        // SMELL: magic numbers
        if (c == 1) {
            x = a * 1.19 + b * 0.07;
        } else if (c == 2) {
            x = a * 1.19 + b * 0.07 - 5;
        } else if (c == 3) {
            x = a * 1.19 + b * 0.07 - 10;
        } else if (c == 4) {
            x = a * 1.19 + b * 0.07 - 15;
        } else if (c == 5) {
            x = a * 1.19 + b * 0.07 - 20;
        }
        return x;
    }

    // SMELL: duplicated code (same logic as the next method)
    public double priceWithTaxForClient(double price, int qty) {
        double total = price * qty;
        if (total > 100) {
            total = total - total * 0.1;
        }
        if (total > 500) {
            total = total - total * 0.05;
        }
        total = total + total * 0.19;
        return total;
    }

    public double priceWithTaxForPartner(double price, int qty) {
        double total = price * qty;
        if (total > 100) {
            total = total - total * 0.1;
        }
        if (total > 500) {
            total = total - total * 0.05;
        }
        total = total + total * 0.19;
        return total;
    }

    // SMELL: SQL injection + resource leak + generic exception + System.out
    public List<String> findByName(String name) throws Exception {
        List<String> result = new ArrayList<>();
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM produits WHERE nom = '" + name + "'");
        while (rs.next()) {
            result.add(rs.getString("nom"));
            System.out.println("found: " + rs.getString("nom"));
        }
        return result;
    }

    // SMELL: empty catch block, catching Throwable, printStackTrace
    public void readFile(String path) {
        try {
            FileReader fr = new FileReader(new File(path));
            fr.read();
        } catch (Exception e) {
        }
        try {
            Thread.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // SMELL: too many parameters, long method, deep nesting, high cognitive complexity
    public String processOrder(String a, String b, String c, String d, String e,
                               int f, int g, int h, boolean i, boolean j) {
        String res = "";
        if (a != null) {
            if (b != null) {
                if (c != null) {
                    if (d != null) {
                        if (f > 0) {
                            if (g > 0) {
                                if (i) {
                                    if (j) {
                                        for (int k = 0; k < f; k++) {
                                            for (int l = 0; l < g; l++) {
                                                if (k % 2 == 0) {
                                                    res += a + b;
                                                } else if (l % 3 == 0) {
                                                    res += c + d;
                                                } else {
                                                    res += e;
                                                }
                                            }
                                        }
                                    } else {
                                        res = "no j";
                                    }
                                } else {
                                    res = "no i";
                                }
                            } else {
                                res = "no g";
                            }
                        } else {
                            res = "no f";
                        }
                    } else {
                        res = "no d";
                    }
                } else {
                    res = "no c";
                }
            } else {
                res = "no b";
            }
        } else {
            res = "no a";
        }
        return res;
    }

    // SMELL: commented-out code
    // public void oldMethod() {
    //     int a = 10;
    //     int b = 20;
    //     System.out.println(a + b);
    //     if (a > b) {
    //         return;
    //     }
    // }

    // SMELL: TODO / FIXME left in code
    // TODO: refactor this whole class
    // FIXME: this breaks when the list is empty

    // SMELL: string comparison with ==, null returned for collection, boxed comparisons
    public boolean isAdmin(String role) {
        if (role == "ADMIN") {
            return true;
        }
        return false;
    }

    public List<String> getNames(Map<String, String> m) {
        if (m == null) {
            return null;
        }
        List<String> names = new ArrayList<>();
        for (String key : m.keySet()) {
            names.add(m.get(key));
        }
        return names;
    }

    // SMELL: always-true / always-false conditions, redundant boolean literal, dead code
    public int deadCode(int v) {
        if (v > 0 || v <= 0) {
            return 1;
        }
        System.out.println("never printed");
        return 2;
    }

    // SMELL: deprecated API, string concatenation in loop, raw types, unused parameter
    @SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
    public String buildReport(List items, String unusedParam) {
        Date d = new Date(2026, 1, 1);
        HashMap map = new HashMap();
        String report = "";
        for (int i = 0; i < items.size(); i++) {
            report = report + items.get(i) + ",";
            map.put(i, items.get(i));
        }
        return report + d;
    }

    // SMELL: method that does nothing / empty method
    public void doNothing() {
    }

    // SMELL: duplicated string literal (repeated 4 times)
    public String describe(int n) {
        if (n == 1) return "Produit non trouvé avec l'ID";
        if (n == 2) return "Produit non trouvé avec l'ID";
        if (n == 3) return "Produit non trouvé avec l'ID";
        return "Produit non trouvé avec l'ID";
    }

    // SMELL: catches exception only to rethrow as generic, returns in finally
    @SuppressWarnings("finally")
    public int risky(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error");
        } finally {
            return -1;
        }
    }
}