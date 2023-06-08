package comp.torcb.misc;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class SymbolMatrix {
    public static volatile PrintStream PRINT_ALL_STREAM;
    private static volatile boolean PRINT_ALL_SKIP;
    private static final AtomicInteger SEQ = new AtomicInteger();
    private final int seq;
    private final SExpression[][] matrix;
    private String desc = "";
    private String label = "";

    public SymbolMatrix(int i, int j) {
        matrix = new SExpression[i][j];
        seq = SEQ.incrementAndGet();
    }

    public SymbolMatrix(String... rows) {
        matrix = new SExpression[rows.length][];
        for (int row = 0; row < rows.length; row++) {
            matrix[row] = Arrays.stream(rows[row].split(","))
                    .map(String::trim)
                    .map(SExpression::parse)
                    .toArray(SExpression[]::new);
        }
        seq = SEQ.incrementAndGet();
        label("");
    }

    public SymbolMatrix(double[][] rows) {
        matrix = new SExpression[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            double[] row = rows[i];
            matrix[i] = new SExpression[row.length];
            for (int j = 0; j < row.length; j++) {
                var se = matrix[i][j] = new SExpression();
                se.add(new Symbol(row[j], ""));
            }
        }
        seq = SEQ.incrementAndGet();
        label("numeric");
    }

    public SymbolMatrix(double... row) {
        this(new double[][]{row});
    }

    public int noRows() {
        return matrix.length;
    }

    public int noCols() {
        return matrix[0].length;
    }

    public SymbolMatrix mul(SymbolMatrix other) {
        int nr = noRows();
        int nc = other.noCols();
        var res = new SymbolMatrix(nr, nc);
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nc; j++) {
                var expr = res.matrix[i][j] = new SExpression();
                for (int k = 0; k < noCols(); k++) {
                    expr.add(matrix[i][k].mul(other.matrix[k][j]));
                }
            }
        }
        return res.label("mul(" + seq + "," + other.seq + ")");
    }

    public SymbolMatrix add(SymbolMatrix other) {
        if (noRows() != other.noRows() || noCols() != other.noCols())
            throw new IllegalArgumentException("Add: different dims");
        var res = new SymbolMatrix(noRows(), noCols());
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            var row2 = other.matrix[i];
            for (int j = 0; j < row.length; j++) {
                var cell = row[j];
                var cell2 = row2[j];
                SExpression value = cell.add(cell2);
                res.matrix[i][j] = value;
            }
        }
        return res.label("add(" + seq + "," + other.seq + ")");
    }

    public SymbolMatrix square() {
        PRINT_ALL_SKIP = true;
        return mul(this).label("square(" + seq + ")");
    }

    public static SymbolMatrix identity(int N) {
        PRINT_ALL_SKIP = true;
        return diagonal(N, new SExpression().add(new Symbol(1, "")))
                .label("identity(" + N + ")");
    }

    public static SymbolMatrix diagonal(int N, SExpression value) {
        var res = new SymbolMatrix(N, N);
        var zero = new SExpression().add(new Symbol());
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                res.matrix[i][j] = i == j ? value : zero;
            }
        }
        return res.label("diagonal(" + N + ")");
    }

    public SymbolMatrix transpose() {
        int nc = noCols();
        var res = new SymbolMatrix(nc, noRows());
        for (int r = 0; r < noRows(); r++) {
            for (int c = 0; c < nc; c++) {
                res.matrix[c][r] = matrix[r][c];
            }
        }
        return res.label("transpose(" + seq + ")");
    }

    public SymbolMatrix subMatrix(int[] rows, int[] cols) {
        var res = new SymbolMatrix(rows.length, cols.length);
        for (int i = 0; i < rows.length; i++) {
            int row = rows[i];
            for (int j = 0; j < cols.length; j++) {
                int col = cols[j];
                res.matrix[i][j] = matrix[row][col];
            }
        }
        return res.label(seq + ".sub(" + Arrays.toString(rows) + Arrays.toString(cols) + ")");
    }

    public SymbolMatrix subMatrix(int... ixs) {
        PRINT_ALL_SKIP = true;
        return subMatrix(ixs, ixs)
                .label(seq + ".sub(" + Arrays.toString(ixs) + ")");
    }

    public SymbolMatrix map(Function<SymbolMatrix, SymbolMatrix> mf) {
        return mf.apply(this);
    }

    public SymbolMatrix assign(Map<Character, Double> map) {
        var res = new SymbolMatrix(noRows(), noCols());
        for (int i = 0; i < matrix.length; i++) {
            var row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                res.matrix[i][j] = row[j].assign(map);
            }
        }
        String assignedChars = map.keySet().stream().map(String::valueOf).collect(Collectors.joining());
        return res.label(seq + ".assign(" + assignedChars + ")");
    }

    public SymbolMatrix assign(String mappedChars, double... values) {
        return assign(toMap(mappedChars, values));
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

    public static SymbolMatrix quaternionMatrix() {
        return new SymbolMatrix("c,-z,y",
                "z,c,-x",
                "-y,x,c")
                .square()
                .add(new SymbolMatrix("x,y,z")
                        .map(m -> m.transpose().mul(m)))
                .desc("Rotation matrix for quaternion vector [c,x,y,z]");
    }

    private SymbolMatrix label(String lb) {
        label = lb;
        if (!PRINT_ALL_SKIP && PRINT_ALL_STREAM != null) {
            PRINT_ALL_STREAM.print(this);
        }
        PRINT_ALL_SKIP = false;
        return this;
    }

    public SymbolMatrix desc(String d) {
        desc = d;
        if (PRINT_ALL_STREAM != null && desc != null && !desc.isEmpty()) {
            PRINT_ALL_STREAM.println("  desc(" + seq + "): " + desc);
        }
        return this;
    }

    @Override
    public String toString() {
        int sz = 11;
        for (var r : matrix) for (var c : r) sz = Math.max(sz, c.approxLen());
        final String form = " %" + sz + "s";
        String lb = label == null || label.isEmpty() ? "" : "=" + label;
        var sb = new StringBuilder("\nMatrix(" + seq + lb + "):\n");
        for (var row : matrix) {
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

}
