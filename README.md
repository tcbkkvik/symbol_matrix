# symbol_matrix
Simple symbolic&amp;numeric matrix ops..

Made just for fun, but it works

Symbolic example:
new SymbolMatrix("c,-z,y",
                "z,c,-x",
                "-y,x,c")
                .square().add(new SymbolMatrix("x,y,z").outer())
                .desc("Rotation_new SymbolMatrix, based on Quaternion vector [c,x,y,z]");
                
Numeric example:
new SymbolMatrix(new double[][]{
        {c, -z, y},
        {z, c, -x},
        {-y, x, c}
}).square().add(new SymbolMatrix(new double[][]{{x, y, z}}).outer());
