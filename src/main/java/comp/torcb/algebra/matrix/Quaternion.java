package comp.torcb.algebra.matrix;

public record Quaternion(double c, double x, double y, double z) {
    public static Quaternion fromAngleVector(double halfTurnRad, double u, double v, double w) {
        double k = Math.sin(halfTurnRad) / Math.sqrt(u * u + v * v + w * w);
        return new Quaternion(Math.cos(halfTurnRad),
                k * u,
                k * v,
                k * w);
    }

    public SymMatrix toRotationMatrix() {
        return new SymMatrix(new double[][]{
                {c, -z, y},
                {z, c, -x},
                {-y, x, c}
        }).square().add(new SymMatrix(x, y, z).map(m -> m.transpose().mul(m)));
    }

    public static SymMatrix symRotationMatrix() {
        return new SymMatrix(
                "c,-z,y",
                "z,c,-x",
                "-y,x,c"
        ).square().add(new SymMatrix("x,y,z").map(m -> m.transpose().mul(m)))
                .desc("Rotation matrix for quaternion [c,x,y,z]");
    }

    public double[] doubles() {
        return new double[]{c, x, y, z};
    }

    public Quaternion conjugate() {
        return new Quaternion(c, -x, -y, -z);
    }

    public Quaternion mul(Quaternion o) {
        double[] row = new SymMatrix(o.c, o.x, o.y, o.z)
                .mul(new SymMatrix(new double[][]{
                        {c, x, y, z},
                        {-x, c, -z, y},
                        {-y, z, c, -x},
                        {-z, -y, x, c}
                })).toNumeric()[0];
        return new Quaternion(row[0], row[1], row[2], row[3]);
    }

}
