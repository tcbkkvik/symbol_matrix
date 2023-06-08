package comp.torcb.misc;

import java.util.Map;


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
        }).square().add(new SymbolMatrix(new double[][]{{x, y, z}}).outer());
    }

    public static void main(String[] args) {
        SymbolMatrix.PRINT_ALL = true;
        SymbolMatrix numRot = makeQRotMatrix(1, 1, 0, 0);
        System.out.println(numRot);
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


        var qm = SymbolMatrix.quaternionMatrix();

        var sm = new SymbolMatrix("c,-z,y",
                "z,c,-x",
                "-y,x,c")
                .square().add(new SymbolMatrix("x,y,z").outer())
                .desc("Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]");

        var sm2 = sm.assign(Map.of('x', .5, 'y', .5, 'c', .1));
        System.out.println(qm);
        System.out.println(sm2);
        System.out.println("done");
    }
}
