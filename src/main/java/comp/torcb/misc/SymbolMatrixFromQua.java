package comp.torcb.misc;


public class SymbolMatrixFromQua {

    @SuppressWarnings("SameParameterValue")
    static SymbolMatrix makeQRotMatrix(double halfRotateRadians, double u, double v, double w) {
        double len = Math.sqrt(u * u + v * v + w * w);
        double sin = Math.sin(halfRotateRadians);
        double c = Math.cos(halfRotateRadians);
        double x = sin * u / len;
        double y = sin * v / len;
        double z = sin * w / len;
        return new SymbolMatrix(new double[][]{
                {c, -z, y},
                {z, c, -x},
                {-y, x, c}
        }).square().add(new SymbolMatrix(x, y, z)
                .map(m -> m.transpose().mul(m)));
    }

    public static void main(String[] args) {
        SymbolMatrix.PRINT_ALL_STREAM = System.out;
        makeQRotMatrix(1, 1, 0, 0)
                .mul(new SymbolMatrix(.1, .2, .3).transpose());

        new SymbolMatrix("c,-x,-y,-z",
                "-x,c,-z,y",
                "-y,z,c,-x",
                "-z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion_conjugate(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=-1")
                .subMatrix(1, 2, 3)
                .desc("Rotation_new SymbolMatrix, normal Quaternion rules (ii=jj=kk=-1)");

        new SymbolMatrix("c,x,y,z",
                "x,c,-z,y",
                "y,z,c,-x",
                "z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=1")
                .subMatrix(1, 2, 3)
                .desc("Rotation_new SymbolMatrix, modified Quaternion rules (ii=jj=kk=1)");

        SymbolMatrix.quaternionMatrix();

        var sm = new SymbolMatrix("c,-z,y",
                "z,c,-x",
                "-y,x,c")
                .square().add(new SymbolMatrix("x,y,z")
                        .map(m -> m.transpose().mul(m)))
                .desc("Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]");

        sm.assign("xyc", .5,  .5, .1);

        System.out.println("done");
    }
}
