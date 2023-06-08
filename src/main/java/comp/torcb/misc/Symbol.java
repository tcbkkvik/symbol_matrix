package comp.torcb.misc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Symbol {
    static final Pattern PAT = Pattern.compile("([-+]?)([0-9]*[.]?[0-9]*)([a-zA-Z]*)");
    static final DecimalFormat DECIMALFORMAT;

    static {
        var ds = new DecimalFormatSymbols();
        ds.setDecimalSeparator('.');
        DECIMALFORMAT = new DecimalFormat("#.#######", ds);
    }

    double num;
    final String body;

    public Symbol(double num, String body) {
        this.num = num;
        char[] arr = body.toCharArray();
        Arrays.sort(arr);
        this.body = new String(arr);
    }

    public Symbol() {
        this(0, "");
    }

    public Symbol assign(Map<Character, Double> map) {
        char[] arr = body.toCharArray();
        var symbol = new StringBuilder();
        double product = num;
        for (char c : arr) {
            var val = map.get(c);
            if (val != null) {
                product *= val;
            } else {
                symbol.append(c);
            }
        }
        return new Symbol(product, symbol.toString());
    }

    public static Symbol parse(String s) {
        var matcher = PAT.matcher(s);
        if (matcher.find()) {
            var body = matcher.group(3);
            return new Symbol(sgn(matcher.group(1)) * number(matcher.group(2), body.isBlank() ? 0 : 1), body);
        }
        return new Symbol();
    }

    public Symbol mul(Symbol other) {
        return new Symbol(num * other.num, body + other.body);
    }

    private static int sgn(String sg) {
        return sg != null && sg.contains("-") ? -1 : 1;
    }

    private static double number(String no, double def) {
        return no == null || no.isBlank() ? def : Double.parseDouble(no);
    }

    public double getNum() {
        return num;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        double s = Math.abs(num);
        return num == 0 ? "" :
                (num < 0 ? "-" : "+")
                + (s != 1 || body.isEmpty() ? DECIMALFORMAT.format(s) : "")
                + body;
    }

    public static void main(String[] args) {
        Symbol[] parsed = Stream.of("2", "-2", "-a", "", "3b")
                .map(Symbol::parse).toArray(Symbol[]::new);
        var s = new Symbol(2, "ab");
        var s2 = s.assign(Map.of('a', 3.2));
        System.out.println(Arrays.toString(parsed));
    }
}
