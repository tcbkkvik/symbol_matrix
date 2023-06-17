package comp.torcb.algebra.matrix;


import java.util.Arrays;

import static comp.torcb.algebra.matrix.Symbol.ERR_DELTA;

@SuppressWarnings("SpellCheckingInspection")
public class SymMatrixDemo {

    public static void main(String[] args) {
        // Quaternion = rotation axis
        var q1 = Quaternion.fromAngleVector(Math.PI / 12, 1, 0, 0);
        var q2 = Quaternion.fromAngleVector(Math.PI / 6, 1, 0, 0);
        var q1xq1 = q1.mul(q1);
        assert q1xq1.equals(q2);
        System.out.println("\nInput " + q1);

        // -> rotation Matrix
        SymMatrix rot1 = q1.toRotationMatrix().desc("Rotate_1 (numeric alt)");
        SymMatrix rot2a = Quaternion.symRotationMatrix().desc("Rotate_2 (symbolic alt)");
        SymMatrix rot2 = rot2a.assign("cxyz", q1.doubles()).desc("Rotate_2, assigned to numeric values");
        SymMatrix rot1_conj = q1.conjugate().toRotationMatrix().desc("-Rotate_1 (conjugate opposite)");
        System.out.println(rot1);
        System.out.println(rot2a);
        System.out.println(rot2);
        assert rot1.equals(rot2);

        // -> Vector transformation
        double[] vectorIn = {.1, .2, .3};
        var vmIn = new SymMatrix(vectorIn);
        double[] vector_alt1 = vmIn.mul(rot1).toNumeric()[0];
        double[] vector_alt2 = vmIn.mul(rot2).toNumeric()[0];
        double[] vector_alt1_conj = new SymMatrix(vector_alt1)
                .mul(rot1_conj).toNumeric()[0];

        System.out.println("vectorIn: " + Arrays.toString(vectorIn));
        System.out.println("Rotate_1(vectorIn): " + Arrays.toString(vector_alt1));
        System.out.println("Rotate_2(vectorIn): " + Arrays.toString(vector_alt2));
        for (int i = 0; i < vectorIn.length; i++) {
            assert Math.abs(vectorIn[i] - vector_alt1_conj[i]) < ERR_DELTA;
        }
        assert Arrays.equals(vector_alt1, vector_alt2);

        SymMatrix.PRINT_ALL_STREAM = System.out;

        new SymMatrix("c,-x,-y,-z",
                "-x,c,-z,y",
                "-y,z,c,-x",
                "-z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=-1")
                .slice(1, 2, 3)
                .desc("Rotation_new SymbolMatrix, normal Quaternion rules (ii=jj=kk=-1)");

        new SymMatrix("c,x,y,z",
                "x,c,-z,y",
                "y,z,c,-x",
                "z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=1")
                .slice(1, 2, 3)
                .desc("Rotation_new SymbolMatrix, modified Quaternion rules (ii=jj=kk=1)")
                .assign("xyc", .6, -.52, .13);

        System.out.println("done");
    }
}
