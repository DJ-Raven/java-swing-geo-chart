package test;

/**
 *
 * @author RAVEN
 */
public class NewClass {

    public static void main(String[] args) {
        Object data[][][] = new Object[1][1][2];
        for (int i = 0; i < data.length; i++) {
            //  List<Coordinates> coordinates = new ArrayList<>();
            for (int j = 0; j < data[0].length; j++) {
                for (int k = 0; k < data[0][0].length; k++) {
                    System.out.println(data[i][j][k]);
                }
            }
        }
    }
}
