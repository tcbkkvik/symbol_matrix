package comp.torcb.algebra.matrix;

import lombok.NonNull;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SymMatrix {
    public static volatile PrintStream PRINT_ALL_STREAM;
    private static volatile boolean PRINT_ALL_SKIP;
    private static final AtomicInteger SEQ = new AtomicInteger();
    private final int seq;
    private final SymExpression[][] matrix;
    private String desc = "";
    private String label = "";

    public SymMatrix(int i, int j) {
        matrix = new SymExpression[i][j];
        seq = SEQ.incrementAndGet();
    }

    public SymMatrix(String... rows) {
        matrix = new SymExpression[rows.length][];
        for (int row = 0; row < rows.length; row++) {
            matrix[row] = Arrays.stream(rows[row].split(","))
                    .map(String::trim)
                    .map(SymExpression::parse)
                    .toArray(SymExpression[]::new);
        }
        seq = SEQ.incrementAndGet();
        label("");
    }

    public SymMatrix(double[][] rows) {
        matrix = new SymExpression[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            double[] row = rows[i];
            matrix[i] = new SymExpression[row.length];
            for (int j = 0; j < row.length; j++) {
                var se = matrix[i][j] = new SymExpression();
                se.add(new Symbol(row[j], ""));
            }
        }
        seq = SEQ.incrementAndGet();
        label("numeric");
    }

    public SymMatrix(double... row) {
        this(new double[][]{row});
    }

    public int noRows() {
        return matrix.length;
    }

    public int noCols() {
        return matrix[0].length;
    }

    public SymMatrix mul(SymMatrix other) {
        int nr = noRows();
        int nc = other.noCols();
        var res = new SymMatrix(nr, nc);
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nc; j++) {
                var expr = res.matrix[i][j] = new SymExpression();
                for (int k = 0; k < noCols(); k++) {
                    expr.add(matrix[i][k].mul(other.matrix[k][j]));
                }
            }
        }
        return res.label("mul(" + seq + "," + other.seq + ")");
    }

    public SymMatrix mul(double factor) {
        var res = new SymMatrix(noRows(), noCols());
        for (int i = 0; i < noRows(); i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                res.matrix[i][j] = matrix[i][j].mul(factor);
            }
        }
        return res.label(seq + ".mul(" + factor + ")");
    }

    public SymMatrix add(SymMatrix other) {
        return add(other, 1);
    }

    public SymMatrix add(SymMatrix other, double factor) {
        if (noRows() != other.noRows() || noCols() != other.noCols())
            throw new IllegalArgumentException("Add: different dims");
        var res = new SymMatrix(noRows(), noCols());
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            var row2 = other.matrix[i];
            for (int j = 0; j < row.length; j++) {
                res.matrix[i][j] = row[j].add(row2[j], factor);
            }
        }
        return res.label("add(" + seq + "," + other.seq + ")");
    }

    public SymMatrix square() {
        PRINT_ALL_SKIP = true;
        return mul(this).label("square(" + seq + ")");
    }

    public static SymMatrix identity(int N) {
        PRINT_ALL_SKIP = true;
        return diagonal(N, new SymExpression().add(new Symbol(1, "")))
                .label("identity(" + N + ")");
    }

    public static SymMatrix diagonal(int N, SymExpression value) {
        var res = new SymMatrix(N, N);
        var zero = new SymExpression().add(new Symbol());
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                res.matrix[i][j] = i == j ? value : zero;
            }
        }
        return res.label("diagonal(" + N + ")");
    }

    public SymMatrix transpose() {
        int nc = noCols();
        var res = new SymMatrix(nc, noRows());
        for (int r = 0; r < noRows(); r++) {
            for (int c = 0; c < nc; c++) {
                res.matrix[c][r] = matrix[r][c];
            }
        }
        return res.label("transpose(" + seq + ")");
    }

    public SymMatrix slice(int[] rows, int[] cols) {
        var res = new SymMatrix(rows.length, cols.length);
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            for (int j = 0; j < cols.length; j++) {
                res.matrix[i][j] = matrix[row][cols[j]];
            }
        }
        return res.label(seq + ".sub(" + Arrays.toString(rows) + Arrays.toString(cols) + ")");
    }

    public SymMatrix slice(int... ixs) {
        PRINT_ALL_SKIP = true;
        return slice(ixs, ixs)
                .label(seq + ".sub(" + Arrays.toString(ixs) + ")");
    }

    public SymMatrix map(Function<SymMatrix, SymMatrix> mf) {
        return mf.apply(this);
    }

    public SymMatrix assign(Map<Character, Double> map) {
        var res = new SymMatrix(noRows(), noCols());
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                res.matrix[i][j] = row[j].assign(map);
            }
        }
        String assignedChars = map.keySet().stream().map(String::valueOf).collect(Collectors.joining());
        return res.label(seq + ".assign(" + assignedChars + ")");
    }

    public SymMatrix assign(String mappedChars, double... values) {
        PRINT_ALL_SKIP = true;
        return assign(toMap(mappedChars, values))
                .label(seq + ".assign(" + mappedChars + ", " + Arrays.toString(values) + ")");
    }

    private static Map<Character, Double> toMap(String mappedChars, double... values) {
        var chars = mappedChars.toCharArray();
        if (chars.length != values.length)
            throw new IllegalArgumentException("length differs");
        Map<Character, Double> map = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            map.put(chars[i], values[i]);
        }
        return map;
    }

    public double[][] toNumeric() {
        double[][] res = new double[noRows()][noCols()];
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                res[i][j] = row[j].toNumeric();
            }
        }
        return res;
    }

    private SymMatrix label(@NonNull String lb) {
        label = lb;
        if (!PRINT_ALL_SKIP && PRINT_ALL_STREAM != null) {
            PRINT_ALL_STREAM.print(this);
        }
        PRINT_ALL_SKIP = false;
        return this;
    }

    public SymMatrix desc(String d) {
        desc = d;
        if (PRINT_ALL_STREAM != null && desc != null && !desc.isEmpty()) {
            PRINT_ALL_STREAM.println("  desc(" + seq + "): " + desc);
        }
        return this;
    }

    public SymExpression[][] getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        int sz = 11;
        for (var r : matrix) for (var c : r) sz = Math.max(sz, c.approxLen());
        final String form = " %" + sz + "s";
        String lb = label == null || label.isEmpty() ? "" : "=" + label;
        var sb = new StringBuilder("\nMatrix(" + seq + lb + "):\n");
        for (SymExpression[] row : matrix) {
            for (int c = 0; c < row.length; c++) {
                if (c > 0) sb.append(",");
                sb.append(form.formatted(row[c]));
            }
            sb.append(";\n");
        }
        if (desc != null && !desc.isEmpty()) {
            //noinspection StringConcatenationInsideStringBufferAppend
            sb.append("  desc(" + seq + "): " + desc + "\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymMatrix other = (SymMatrix) o;
        if (!(noRows() == other.noRows()
              && noCols() == other.noCols())) {
            return false;
        }
        for (int i = 0; i < matrix.length; i++) {
            SymExpression[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                SymExpression s1 = row[j];
                SymExpression s2 = other.matrix[i][j];
                if (!s1.equals(s2)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matrix.length);
    }
}
