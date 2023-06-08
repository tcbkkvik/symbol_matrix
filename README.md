# symbol_matrix
###  Simple symbolic&amp;numeric matrix ops..

Made for fun, but works..

Symbolic example: 
```
new SymbolMatrix("c,-z,y",
                "z,c,-x",
                "-y,x,c")
                .square().add(new SymbolMatrix("x,y,z").outer())
                .desc("Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]");
```
Numeric example:
```       
new SymbolMatrix(new double[][]{
        {c, -z, y},
        {z, c, -x},
        {-y, x, c}
}).square().add(new SymbolMatrix(new double[][]{{x, y, z}}).outer());
```


Example output:
```
Matrix(18=add(14,17) quaternionMatrix):
  +cc+xx-yy-zz,      -2cz+2xy,      +2cy+2xz;
      +2cz+2xy,  +cc-xx+yy-zz,      -2cx+2yz;
      -2cy+2xz,      +2cx+2yz,  +cc-xx-yy+zz;
  desc(18): Rotation matrix for quaternion vector [c,x,y,z]

Matrix(25=24.assign(ycx)):
    +0.01-zz,   +0.5-0.2z,      +0.1+z;
   +0.5+0.2z,    +0.01-zz,      -0.1+z;
      -0.1+z,      +0.1+z,    -0.49+zz;
```