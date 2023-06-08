package comp.torcb.misc;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SExpression {
    public final Map<String, Symbol> map = new TreeMap<>();

    public static SExpression parse(String s) {
        return new SExpression().add(Symbol.parse(s));
    }

    public SExpression mul(SExpression e) {
        var res = new SExpression();
        for (Symbol a : map.values())
            for (Symbol b : e.map.values())
                res.add(a.mul(b));
        return res;
    }

    public SExpression add(Symbol sym) {
        map.computeIfAbsent(sym.body, k -> new Symbol(0, sym.body)).num += sym.num;
        return this;
    }

    public SExpression add(SExpression e) {
        for (Symbol s : e.map.values()) add(s);
        return this;
    }

    public SExpression assign(Map<Character, Double> valueMap) {
        var res = new SExpression();
        for (var s : map.values()) {
            res.add(s.assign(valueMap));
        }
        return res;
    }

    public int approxLen() {
        int sz = 0;
        for (var s : map.values()) sz += Math.max(1, s.body.length()) + 1;
        return sz + 1;
    }

    @SuppressWarnings("unused")
    public Collection<Symbol> symbols() {
        return map.values();
    }

    @Override
    public String toString() {
        return map.values().stream().map(Symbol::toString).collect(Collectors.joining());
    }
}
