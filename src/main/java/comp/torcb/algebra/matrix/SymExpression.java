package comp.torcb.algebra.matrix;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SymExpression {
    private static final Pattern PAT = Pattern.compile("( ?[+-]?[0-9.a-zA-Z]+)");
    public final Map<String, Symbol> map = new TreeMap<>();

    public static SymExpression parse(String s) {
        SymExpression se = new SymExpression();
        Matcher matcher = PAT.matcher(s);
        while (matcher.find()) {
            se.add(Symbol.parse(matcher.group(1)));
        }
        return se;
    }

    public SymExpression mul(SymExpression e) {
        var res = new SymExpression();
        for (Symbol a : map.values())
            for (Symbol b : e.map.values())
                res.add(a.mul(b));
        return res;
    }

    public SymExpression mul(double factor) {
        var res = new SymExpression();
        for (Symbol s : map.values())
            res.add(new Symbol(s.num * factor, s.body));
        return res;
    }

    public SymExpression add(Symbol sym) {
        return add(sym, 1);
    }

    public SymExpression sub(Symbol sym) {
        return add(sym, -1);
    }

    public SymExpression add(Symbol sym, double factor) {
        map.computeIfAbsent(sym.body, k -> new Symbol(0, sym.body)).num += sym.num * factor;
        return this;
    }

    public SymExpression add(SymExpression e) {
        return add(e, 1);
    }

    public SymExpression sub(SymExpression e) {
        return add(e, -1);
    }

    public SymExpression add(SymExpression e, double factor) {
        for (Symbol s : e.map.values()) add(s, factor);
        return this;
    }

    public SymExpression assign(Map<Character, Double> valueMap) {
        var res = new SymExpression();
        for (var s : map.values()) {
            res.add(s.assign(valueMap));
        }
        return res;
    }

    public double toNumeric() {
        return map.size() != 1 ? Double.NaN
                : map.values().stream()
                .map(Symbol::getNumeric)
                .findFirst().orElse(Double.NaN);
    }

    public int approxLen() {
        int sz = 0;
        for (var s : map.values()) sz += Math.max(1, s.body.length()) + 1;
        return sz + 1;
    }

    public Collection<Symbol> symbols() {
        return map.values();
    }

    @Override
    public String toString() {
        String s = map.values().stream().map(Symbol::toString).collect(Collectors.joining());
        return s.isEmpty() ? "0" : s;
    }

    public long nonZeroSymbols() {
        return map.values().stream()
                .filter(s -> Math.abs(s.num) > 1e-7)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymExpression other = (SymExpression) o;
        if (other.nonZeroSymbols() != nonZeroSymbols()) {
            return false;
        }
        for (Symbol s1 : map.values()) {
            if (Math.abs(s1.num) < 1e-7) continue;
            Symbol s2 = other.map.get(s1.body);
            if (!s1.equals(s2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map.size());
    }
}
