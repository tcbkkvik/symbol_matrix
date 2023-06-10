package comp.torcb.algebra.matrix;


public class SymMatrixDemo {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        SymMatrix.PRINT_ALL_STREAM = System.out;
        Quaternion numQt = Quaternion.normalize(Math.PI / 2, 1, 0, 0);
        Quaternion numQt2 = numQt.mul(numQt);
        double[][] nMat = numQt
                .toRotationMatrix()
                .mul(new SymMatrix(.1, .2, .3).transpose())
                .transpose()
                .toNumeric();

        new SymMatrix("c,-x,-y,-z",
                "-x,c,-z,y",
                "-y,z,c,-x",
                "-z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion_conjugate(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=-1")
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
