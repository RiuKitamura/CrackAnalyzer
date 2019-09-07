package com.mhaa98.crackanalyzer;

import android.graphics.Bitmap;


public class GLCM {
    private Bitmap image;
    private int[][] grayLeveledMatrix;
    private int grayLevel;
    private int kolom;
    private int baris;
    private double[] fiturGLCM;
    private double meanx=0.0;
    private double meany=0.0;
    private double stdevx=0.0;
    private double stdevy=0.0;

    public GLCM(Bitmap img,int grayLevel) {
        this.image = img;
        this.grayLevel = grayLevel;
        this.baris=img.getHeight();
        this.kolom=img.getWidth();
        grayLeveledMatrix = new int[kolom][baris];
        fiturGLCM = new double[20];
    }

    public double[] extract() {
        this.createGrayLeveledMatrix();
        //0°
        int[][] cm0 = createCoOccuranceMatrix(0);
        double[][] cm0SN = normalizeMatrix(add(cm0, transposeMatrix(cm0)));

        //45°
        int[][] cm45 = createCoOccuranceMatrix(45);
        double[][] cm45SN = normalizeMatrix(add(cm45, transposeMatrix(cm45)));

        //90°
        int[][] cm90 = createCoOccuranceMatrix(90);
        double[][] cm90SN = normalizeMatrix(add(cm90, transposeMatrix(cm90)));

        //135°
        int[][] cm135 = createCoOccuranceMatrix(135);
        double[][] cm135SN = normalizeMatrix(add(cm135, transposeMatrix(cm135)));

//        this.contrast = (double) (calcContrast(cm0SN) + calcContrast(cm45SN) + calcContrast(cm90SN) + calcContrast(cm135SN)) / 4;
//        this.homogenity = (double) (calcHomogenity(cm0SN) + calcHomogenity(cm45SN) + calcHomogenity(cm90SN) + calcHomogenity(cm135SN)) / 4;
//        this.entropy = (double) (calcEntropy(cm0SN) + calcEntropy(cm45SN) + calcEntropy(cm90SN) + calcEntropy(cm135SN)) / 4;
//        this.energy = (double) (calcEnergy(cm0SN) + calcEnergy(cm45SN) + calcEnergy(cm90SN) + calcEnergy(cm135SN)) / 40;
//        this.dissimilarity = (double) (calcDissimilarity(cm0SN) + calcDissimilarity(cm45SN) + calcDissimilarity(cm90SN) + calcDissimilarity(cm135SN)) / 4;
//        this.correlation = (double) (calcCorrelation(cm0SN) + calcCorrelation(cm45SN) + calcCorrelation(cm90SN) + calcCorrelation(cm135SN)) / 4;

        fiturGLCM[0]=calcContrast(cm0SN);
        fiturGLCM[1]=calcContrast(cm45SN);
        fiturGLCM[2]=calcContrast(cm90SN);
        fiturGLCM[3]=calcContrast(cm135SN);

        fiturGLCM[4]=calcEntropy(cm0SN);
        fiturGLCM[5]=calcEntropy(cm45SN);
        fiturGLCM[6]=calcEntropy(cm90SN);
        fiturGLCM[7]=calcEntropy(cm135SN);

        fiturGLCM[8]=calcEnergy(cm0SN);
        fiturGLCM[9]=calcEnergy(cm45SN);
        fiturGLCM[10]=calcEnergy(cm90SN);
        fiturGLCM[11]=calcEnergy(cm135SN);

        fiturGLCM[12]=calcCorrelation(cm0SN);
        fiturGLCM[13]=calcCorrelation(cm45SN);
        fiturGLCM[14]=calcCorrelation(cm90SN);
        fiturGLCM[15]=calcCorrelation(cm135SN);

        fiturGLCM[16]=calcHomogenity(cm0SN);
        fiturGLCM[17]=calcHomogenity(cm45SN);
        fiturGLCM[18]=calcHomogenity(cm90SN);
        fiturGLCM[19]=calcHomogenity(cm135SN);


//        String data_txt = "";
//        for (int i=0;i<20;i++) {
//            if(i==19)
//                data_txt += fiturGLCM[i];
//            else
//                data_txt += fiturGLCM[i] + " ";
////            Log.e("FiturGLCM", String.valueOf(fiturGLCM[i]));
//        }
//        LoginActivity.mSQLiteHelper.insertDataGambar(data_txt);
        return fiturGLCM;
    }

    private void createGrayLeveledMatrix() {
        int[] pix = new int[kolom * baris];
        image.getPixels(pix, 0, kolom, 0, 0, kolom, baris);
//        int[][] I = {{41,50,56},{26,33,41},{39,40,40}};
//        int[][] R = {{68,77,84},{53,60,68},{66,67,67}};
//        int[][] G = {{29,38,45},{14,21,29},{27,28,28}};
//        int[][] B = {{32,41,48},{17,24,32},{30,31,31}};

        for (int y = 0; y < baris; y++){
            for (int x = 0; x < kolom; x++)
            {
//                int rgb = image.getPixel(y, x);
//                int R = (rgb >> 16) & 0xFF;
//                int G = (rgb >> 8) & 0xFF;
//                int B = (rgb & 0xFF);
                int index = y * image.getHeight() + x;
                int R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                int G = (pix[index] >> 8) & 0xff;
                int B = pix[index] & 0xff;
                int gr =(int)((R*0.2989) + (G*0.5870) + (B*0.1140))+1;
//                int gr =(int)((R[y][x]*0.2989) + (G[y][x]*0.5870) + (B[y][x]*0.1140))+1;

//                if (grayLevel > 0 && grayLevel < 255) {
//                    grayLeveledMatrix[y][x] = (int)gr * grayLevel / 255;// gr yg digunakan sebelumnya
//                } else {
                grayLeveledMatrix[y][x] = gr;
//                }
//                grayLeveledMatrix[y][x] = I[y][x];
//                Log.e("Matrix_R", String.valueOf("Matrix : "+R));
//                Log.e("Matrix_G", String.valueOf("Matrix : "+G));
//                Log.e("Matrix_B", String.valueOf("Matrix : "+B));
//                Log.e("Matrix_Gray", String.valueOf("Matrix : "+grayLeveledMatrix[y][x]));
            }
        }
    }


    private int[][] createCoOccuranceMatrix(int angle) { //distance = 1
        int[][] temp = new int[grayLevel+1][grayLevel+1];
        int startRow = 0;
        int startColumn = 0;
        int endColumn = 0;

        boolean validAngle = true;
        switch (angle) {
            case 0:
                startRow = 0;
                startColumn = 0;
                endColumn = grayLeveledMatrix[0].length-2;
                break;
            case 45:
                startRow = 1;
                startColumn = 0;
                endColumn = grayLeveledMatrix[0].length-2;
                break;
            case 90:
                startRow = 1;
                startColumn = 0;
                endColumn = grayLeveledMatrix[0].length-1;
                break;
            case 135:
                startRow = 1;
                startColumn = 1;
                endColumn = grayLeveledMatrix[0].length-1;
                break;
            default:
                validAngle = false;
                break;
        }

        if (validAngle) {
            for (int i = startRow; i < grayLeveledMatrix.length; i++) {
                for (int j = startColumn; j <= endColumn; j++) {
                    switch (angle) {
                        case 0:
                            temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i][j+1]]++;
                            break;
                        case 45:
                            temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j+1]]++;
                            break;
                        case 90:
                            temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j]]++;
                            break;
                        case 135:
                            temp[grayLeveledMatrix[i][j]][grayLeveledMatrix[i-1][j-1]]++;
                            break;
                    }
                }
            }
        }
        return temp;
    }

    private int[][] transposeMatrix(int [][] m){
        int[][] temp = new int[m[0].length][m.length];
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[0].length; j++){
                temp[j][i] = m[i][j];
            }
        }
        return temp;
    }

    private int[][] add(int [][] m1, int [][] m2){
        int[][] temp = new int[m1[0].length][m1.length];
        for (int i = 0; i < m1.length; i++){
            for (int j = 0; j < m1[0].length; j++){
                temp[j][i] = m1[i][j] + m2[i][j];
            }
        }
        return temp;
    }

    private int getTotal(int [][] m){
        int temp = 0;
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[0].length; j++){
                temp += m[i][j];
            }
        }
        return temp;
    }

    private double[][] normalizeMatrix(int [][] m){
        double[][] temp = new double[m[0].length][m.length];
        int total = getTotal(m);
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[0].length; j++){
                temp[j][i] = (double) m[i][j] / total;
            }
        }
        return temp;
    }

    void doBasicStats(double[][] matrix){
        double [] px = new double [matrix.length];
        double [] py = new double [matrix[0].length];
        meanx=0.0;
        meany=0.0;
        stdevx=0.0;
        stdevy=0.0;

        // Px(i) and Py(j) are the marginal-probability matrix; sum rows (px) or columns (py)
        // First, initialize the arrays to 0
        for (int i=0;  i<matrix.length; i++){
            px[i] = 0.0;
            py[i] = 0.0;
        }

        // sum the glcm rows to Px(i)
        for (int i=0;  i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                px[i] += matrix [i][j];
            }
        }

        // sum the glcm rows to Py(j)
        for (int j=0;  j<matrix.length; j++) {
            for (int i=0; i<matrix[0].length; i++) {
                py[j] += matrix [i][j];
            }
        }

        // calculate meanx and meany
        for (int i=0;  i<matrix.length; i++) {
            meanx += (i*px[i]);
            meany += (i*py[i]);
        }

        // calculate stdevx and stdevy
        for (int i=0;  i<matrix.length; i++) {
            stdevx += ((Math.pow((i-meanx),2))*px[i]);
            stdevy += ((Math.pow((i-meany),2))*py[i]);
        }
        stdevx=Math.sqrt(stdevx);
        stdevy=Math.sqrt(stdevy);
    }

    private double calcContrast(double[][] matrix) {
        double temp = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                temp = (Math.pow((i - j),2) * matrix[i][j]) + temp;
            }
        }
        return temp;
    }

    private double calcHomogenity(double[][] matrix) {
        double temp = 0;
        double temp2;
        double hasil = 0;
        double[][] Homogenity = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length-1; i++) {
            for (int j = 0; j < matrix[0].length-1; j++) {
                temp = matrix[i][j];
                if (temp == 0){
                    Homogenity[i][j] = 0;
                }else{
                    temp2 = temp/(1+Math.abs(i-j));
                    Homogenity[i][j] = temp2;
                }
                hasil = hasil + Homogenity[i][j];
            }
        }
        return hasil;
    }

    private double calcEntropy(double[][] matrix) {
        double temp;
        double temp2;
        double temp3;
        double hasil = 0;
        double[][] Entropy = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length-1; i++) {
            for (int j = 0; j < matrix[0].length-1; j++) {
                temp2 = matrix[i][j];
                if (temp2 == 0){
                    Entropy[i][j] = 0;
                }else{
                    temp = -(temp2);
                    temp3 = Math.log10(temp2);
                    Entropy[i][j] = temp*temp3;
                }
                hasil = hasil + Entropy[i][j];
            }
        }
        return hasil;
    }

    private double calcEnergy(double[][] matrix) {
        double temp = 0;
        double temp2;
        double temp3;
        double hasil = 0;
        double[][] Energy = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length-1; i++) {
            for (int j = 0; j < matrix[0].length-1; j++) {
                temp2 = matrix[i][j];
                if (temp2 == 0){
                    Energy[i][j] = 0;
                }else{
                    temp3 = Math.pow(temp2, 2);
                    Energy[i][j] = temp3;
                }
                hasil = hasil + Energy[i][j];
            }
        }
        return hasil;
    }

    private double calcDissimilarity(double[][] matrix) {
        double temp = 0;
        for (int i = 0; i < matrix.length-1; i++) {
            for (int j = 0; j < matrix[0].length-1; j++) {
                temp += matrix[i][j] * Math.abs(i-j);
            }
        }
        return temp;
    }

    private double calcCorrelation(double[][] matrix) {
        doBasicStats(matrix);
        double correlation=0.0;

        for (int i=0;  i<matrix.length; i++) {
            for (int j=0; j<matrix[0].length; j++) {
                correlation += (((i-meanx)*(j-meany))*matrix[i][j])/( stdevx*stdevy);
            }
        }
        return correlation;
    }

//    public double getContrast() {
//        return contrast;
//    }
//    public double getHomogenity() {
//        return homogenity;
//    }
//    public double getEntropy() {
//        return entropy;
//    }
//    public double getEnergy() {
//        return energy;
//    }
//    public double getDissimilarity() {
//        return dissimilarity;
//    }
//    public double getCorrelation() {
//        return correlation;
//    }

    public double[] getFiturGLCM() {
        return fiturGLCM;
    }
}