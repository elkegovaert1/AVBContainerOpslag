public class Main {
    public static void main(String[] args) {

        //----------------------------------------- JAR ARGUMENTS -----------------------------------------
        if(args.length == 2){
            Yard yard = Reader.read(args[0]);
            yard.calculateEffectiveCraneBorders();
            yard.checkYard();
            Writer.write(args[1], yard);
        }

        //--------------------------------------------- LAB 4 ---------------------------------------------

        //L400_W120_H3_Q2_C120_S3(new)
        //L400_W120_H3_Q2_C180_S2(new)
        Yard yard = Reader.read("data/example3.ysi");
        yard.calculateEffectiveCraneBorders();

        yard.checkContainers();
        yard.checkYard();
        yard.checkContainers();

        Writer.write("data/example3.yso", yard);


        //--------------------------------------------- LAB 3 ---------------------------------------------

        /*
        //L400_W120_H3_Q2_C120_S3
        //L400_W120_H3_Q2_C180_S2
        Yard yard = Reader.read("data/L400_W120_H3_Q2_C120_S3.ysi");
        yard.checkContainers(); // visuele controle van yard
        yard.checkYard();
        yard.checkContainers(); // visuele controle van yard
        Writer.write("data/L400_W120_H3_Q2_C120_S3.yso", yard);
         */

        //--------------------------------------------- LAB 2 ---------------------------------------------

        /*
        Yard yard = Reader.read("data/L500_W60_H3_Q1_C10.ysi");
        yard.checkContainers(); // visuele controle van yard
        yard.checkYard();
        yard.checkContainers(); // visuele controle van yard
        Writer.write("data/L500_W60_H3_Q1_C10.yso", yard);
        */


        //--------------------------------------------- LAB 1 ---------------------------------------------

        /*
        Yard yard = Reader.read("data/L40_W24_H3_Q1_C3.ysi");
        Writer.write("data/L40_W24_H3_Q1_C3.yso", yard);

        Yard yard1 = Reader.read("data/L100_W48_H3_Q1_C4.ysi");
        Writer.write("data/L100_W48_H3_Q1_C4.yso", yard1);

        Yard yard2 = Reader.read("data/L1000_W240_H3_Q1_C1000.ysi");
        Writer.write("data/L1000_W240_H3_Q1_C1000.yso", yard2);

        Yard yard3 = Reader.read("data/example.ysi");
        Writer.write("data/example.yso", yard3);
        */

    }
}


