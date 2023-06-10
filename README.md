# symbol_matrix
###  Simple symbolic &amp; numeric matrix multiplication++



Usage example: 
```
        // Quaternion = rotation axis
        var q1 = Quaternion.fromAngleVector(Math.PI / 12, 1, 0, 0);
        var q2 = Quaternion.fromAngleVector(Math.PI / 6, 1, 0, 0);
        var q1x2 = q1.mul(q1);
        assert q1x2.equals(q2);
        System.out.println("\nInput " + q1);

        // -> rotation Matrix
        SymMatrix rot1 = q1.toRotationMatrix().desc("Rotate_1 (numeric alt)");
        SymMatrix rot2a = Quaternion.symRotationMatrix().desc("Rotate_2 (symbolic alt)");
        SymMatrix rot2 = rot2a.assign("cxyz", q1.doubles()).desc("Rotate_2, assigned to numeric values");
        System.out.println(rot1);
        System.out.println(rot2a);
        System.out.println(rot2);

        // -> Vector transformation
        double[] vectorIn = {.1, .2, .3};
        var vmIn = new SymMatrix(vectorIn).transpose();
        double[] vector_alt1 = rot1.mul(vmIn).transpose().toNumeric()[0];
        double[] vector_alt2 = rot2.mul(vmIn).transpose().toNumeric()[0];
        System.out.println("vectorIn: " + Arrays.toString(vectorIn));
        System.out.println("Rotate_1(vectorIn): " + Arrays.toString(vector_alt1));
        System.out.println("Rotate_2(vectorIn): " + Arrays.toString(vector_alt2));

        SymMatrix.PRINT_ALL_STREAM = System.out;

        new SymMatrix("c,-x,-y,-z",
                "-x,c,-z,y",
                "-y,z,c,-x",
                "-z,-y,x,c")
                .square()
                .desc("row[0]=Quaternion_conjugate(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=-1")
                .slice(1, 2, 3)
                .desc("Rotation_new SymbolMatrix, normal Quaternion rules (ii=jj=kk=-1)");

```

Output:
```
Input Quaternion[c=0.9659258262890683, x=0.25881904510252074, y=0.0, z=0.0]

Matrix(9=add(5,8)):
          +1,           0,           0;
           0,  +0.8660254,        -0.5;
           0,        +0.5,  +0.8660254;
  desc(9): Rotate_1 (numeric alt)


Matrix(15=add(11,14)):
  +cc+xx-yy-zz,      -2cz+2xy,      +2cy+2xz;
      +2cz+2xy,  +cc-xx+yy-zz,      -2cx+2yz;
      -2cy+2xz,      +2cx+2yz,  +cc-xx-yy+zz;
  desc(15): Rotate_2 (symbolic alt)


Matrix(16=15.assign(cxyz, [0.9659258262890683, 0.25881904510252074, 0.0, 0.0])):
          +1,           0,           0;
           0,  +0.8660254,        -0.5;
           0,        +0.5,  +0.8660254;
  desc(16): Rotate_2, assigned to numeric values

vectorIn: [0.1, 0.2, 0.3]
Rotate_1(vectorIn): [0.1, 0.02320508075688779, 0.3598076211353316]
Rotate_2(vectorIn): [0.1, 0.02320508075688779, 0.3598076211353316]

Matrix(23):
          +c,          -x,          -y,          -z;
          -x,          +c,          -z,          +y;
          -y,          +z,          +c,          -x;
          -z,          -y,          +x,          +c;

Matrix(24=square(23)):
  +cc+xx+yy+zz,          -2cx,          -2cy,          -2cz;
          -2cx,  +cc+xx-yy-zz,      -2cz+2xy,      +2cy+2xz;
          -2cy,      +2cz+2xy,  +cc-xx+yy-zz,      -2cx+2yz;
          -2cz,      -2cy+2xz,      +2cx+2yz,  +cc-xx-yy+zz;
  desc(24): row[0]=Quaternion_conjugate(c,x,y,z), row[1,2,3]=(i,j,k)*row[0] where ii=jj=kk=-1

Matrix(25=24.sub([1, 2, 3])):
  +cc+xx-yy-zz,      -2cz+2xy,      +2cy+2xz;
      +2cz+2xy,  +cc-xx+yy-zz,      -2cx+2yz;
      -2cy+2xz,      +2cx+2yz,  +cc-xx-yy+zz;
  desc(25): Rotation_new SymbolMatrix, normal Quaternion rules (ii=jj=kk=-1)

```