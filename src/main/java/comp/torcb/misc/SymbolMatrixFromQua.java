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

/*
Code:
    new SymbolMatrix("c,-z,y",
            "z,c,-x",
            "-y,x,c")
            .square().add(new SymbolMatrix("x,y,z").outer())
            .desc("Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]");

-> Output:

Matrix(14):
   +c,   -z,   +y;
   +z,   +c,   -x;
   -y,   +x,   +c;

Matrix(15=square(14)):
  +cc-yy-zz,    -2cz+xy,    +2cy+xz;
    +2cz+xy,  +cc-xx-zz,    -2cx+yz;
    -2cy+xz,    +2cx+yz,  +cc-xx-yy;

Matrix(16):
   +x,   +y,   +z;

Matrix(17=transpose(16)):
   +x;
   +y;
   +z;

Matrix(18=mul(17,16)):
  +xx,  +xy,  +xz;
  +xy,  +yy,  +yz;
  +xz,  +yz,  +zz;

Matrix(19=add(15,18)):
  +cc+xx-yy-zz,      -2cz+2xy,      +2cy+2xz;
      +2cz+2xy,  +cc-xx+yy-zz,      -2cx+2yz;
      -2cy+2xz,      +2cx+2yz,  +cc-xx-yy+zz;
  desc(19): Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]
*/