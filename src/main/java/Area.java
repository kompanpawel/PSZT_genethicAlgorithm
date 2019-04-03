import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

import java.util.*;

class Area {
    private ArrayList<Point> array = new ArrayList<>();
    private int minRandCoord, maxRandCoord, numberOfVerts;
    private Random random = new Random();
    static final int N = 4;

    Area (int minRandCoord, int maxRandCoord, int numberOfVerts) {
        this.minRandCoord = minRandCoord;
        this.maxRandCoord = maxRandCoord;
        this.numberOfVerts = numberOfVerts;
    }

    private static void getCofactor(int[][] mat,
                                    int[][] temp, int p, int q, int n) {
        int i = 0, j = 0;

        // Looping for each element of
        // the matrix
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                // Copying into temporary matrix
                // only those element which are
                // not in given row and column
                if (row != p && col != q) {
                    temp[i][j++] = mat[row][col];

                    // Row is filled, so increase
                    // row index and reset col
                    //index
                    if (j == n - 1) {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }

    static int determinantOfMatrix(int mat[][], int n) {
        int D = 0; // Initialize result

        // Base case : if matrix contains single
        // element
        if (n == 1)
            return mat[0][0];

        // To store cofactors
        int temp[][] = new int[N][N];

        // To store sign multiplier
        int sign = 1;

        // Iterate for each element of first row
        for (int f = 0; f < n; f++)
        {
            // Getting Cofactor of mat[0][f]
            getCofactor(mat, temp, 0, f, n);
            D += sign * mat[0][f]
                    * determinantOfMatrix(temp, n - 1);

            // terms are to be added with
            // alternate sign
            sign = -sign;
        }

        return D;
    }

    ArrayList<Point> generateRandomPoints() {
        ArrayList<Point> randomCoordsList = new ArrayList<Point>();
        for (int i = 0; i < this.numberOfVerts; i++) {
            Point point = new Point(random.nextInt(this.maxRandCoord - this.minRandCoord), random.nextInt(this.maxRandCoord - this.minRandCoord));
            randomCoordsList.add(point);
        }

        this.array = randomCoordsList;
        return randomCoordsList;
    }

    void closeLineToPolygon() {
        Point a = this.array.get(0);
        Point b = this.array.get(array.size()-1);
        if (!a.equals(b)) {
            this.array.add(a);
        }
    }

    Point findLeftmostPoint() {
        Point leftmostPoint = this.array.get(0);
        Point leftmost_x = this.array.get(0);
        for (Point point: this.array) {
            int x = point.getX();
            if(x < leftmost_x.getX()) {
                leftmost_x.setX(x);
                leftmostPoint = point;
            }
        }
        return leftmostPoint;
    }

    Point findRightmostPoint() {
        Point rightmostPoint = this.array.get(0);
        Point rightmost_x = this.array.get(0);
        for (Point point : this.array) {
            int x = point.getX();
            if(rightmost_x == null || x > rightmost_x.getX()) {
                rightmost_x.setX(x);
                rightmostPoint = point;
            }
        }
        return rightmostPoint;
    }
    int isPointAboveTheLine(Point point, Point[] linePoints) {
        int px = point.getX();
        int py = point.getY();

        Point P1 = linePoints[0];
        Point P2 = linePoints[1];

        int P1x = P1.getX();
        int P1y = P1.getY();
        int P2x = P2.getX();
        int P2y = P2.getY();

        int[][] array = new int[2][2];
        array[0][0] = P1x - px;
        array[1][0] = P1y - py;
        array[0][1] = P2x - px;
        array[1][1] = P2y - py;
        int det = determinantOfMatrix(array, 2);
        int sign = 0;
        if(det < 0) {
            sign = -1;
            return sign;
        }
        if(det > 0) {
            sign = 1;
            return sign;
        }
        return sign;
    }

    Object[] sortArrayIntoABC(Point[] linePoints) {
        ArrayList<Point> A = new ArrayList<>();
        ArrayList<Point> B = new ArrayList<>();
        ArrayList<Point> C = new ArrayList<>();
        for (Point point :this.array) {
            int sign = this.isPointAboveTheLine(point, linePoints);
            if (sign == 0)
                C.add(point);
            else if (sign == 1)
                B.add(point);
            else if (sign == -1)
                A.add(point);
        }
        return new Object[]{A, B, C};
    }

    void sortAndMergeABCArrays(ArrayList<Point> A, ArrayList<Point> B, ArrayList<Point> C) {
        ArrayList<Point> A_C_array = new ArrayList<Point>();
        A_C_array.addAll(A);
        A_C_array.addAll(C);
        A_C_array.sort(Comparator.comparing(Point::getX));
        B.sort(Comparator.comparing(Point::getX).reversed());
        ArrayList<Point> merged = new ArrayList<>();
        merged.addAll(A_C_array);
        merged.addAll(B);
        this.array = merged;
    }

    Polygon2D buildPolygon() {
        Polygon2D area = new SimplePolygon2D();
        for (Point point :
                this.array) {
            area.addVertex(new Point2D(point.getX(), point.getY()));
        }
        return area;
    }
}
