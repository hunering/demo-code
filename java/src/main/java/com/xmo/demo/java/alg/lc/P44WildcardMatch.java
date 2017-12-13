package com.xmo.demo.java.alg.lc;

public class P44WildcardMatch {

    public static void main(String[] args) {
        //String source = "aaabbbaabaaaaababaabaaabbabbbbbbbbaabababbabbbaaaaba";
        //String pattern = "a*******b";
        String source = "a";
        String pattern = "aa";
        P44WildcardMatch m = new P44WildcardMatch();
        System.out.println(m.isMatch(source, pattern));

    }

    public boolean isMatch(String s, String p) {
        boolean[][] f = new boolean[s.length() + 1][p.length() + 1];
        f[0][0] = true;
        for (int j = 1; j <= p.length(); j++) {
            if (p.charAt(j - 1) == '*') {
                f[0][j] = f[0][j - 1];
            } else {
                f[0][j] = false;
            }
        }

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= p.length(); j++) {
                switch (p.charAt(j - 1)) {
                case '*':
                    if (f[i - 1][j - 1]) {
                        f[i][j] = true;
                    } else if (f[i - 1][j]) {
                        f[i][j] = true;
                    } else if (f[i][j - 1]) {
                        f[i][j] = true;
                    }
                    break;
                case '?':
                    if (f[i - 1][j - 1]) {
                        f[i][j] = true;
                    }
                    break;
                default:
                    if (p.charAt(j - 1) == s.charAt(i - 1) && f[i-1][j-1]) {
                        f[i][j] = true;
                    }
                    break;
                }
            }
        }

        return f[s.length()][p.length()];
    }

    public boolean isMatch_slow(String s, String p) {
        // System.out.println("S: " +s + "; P: " + p);
        String token = getNextToken(p);
        String pattern = null;
        String source = null;
        switch (token) {
        case "*":
            pattern = p.substring(token.length(), p.length());
            if (s.isEmpty()) {
                return isMatch(s, pattern);
            } else {
                for (int i = 0; i <= s.length(); i++) {
                    source = s.substring(i, s.length());
                    if (isMatch(source, pattern)) {
                        return true;
                    } else {

                    }
                }
            }
            return false;
        // break;
        case "?":
            pattern = p.substring(1, p.length());
            if (s.isEmpty()) {
                return false;
            } else {
                source = s.substring(1, s.length());
            }
            return isMatch(source, pattern);
        case "":
            if (s.isEmpty()) {
                return true;
            } else {
                return false;
            }
        default:
            if (s.startsWith(token)) {
                if (token.length() < p.length()) {
                    pattern = p.substring(token.length(), p.length());
                } else {
                    pattern = "";
                }
                if (token.length() < s.length()) {
                    source = s.substring(token.length(), s.length());
                } else {
                    source = "";
                }
                return isMatch(source, pattern);
            } else {
                return false;
            }
        }
    }

    String getNextToken(String p) {
        if (p.length() == 0) {
            return "";
        }
        // String previous = null;
        for (int i = 0; i < p.length(); i++) {
            if (p.charAt(i) == '*') {
                return "*";
            } else if (p.charAt(i) == '?') {
                return "?";
            } else {
                // previous = previous+p.charAt(i);
                return p.substring(0, 1);
            }
        }

        return p;
    }
}
