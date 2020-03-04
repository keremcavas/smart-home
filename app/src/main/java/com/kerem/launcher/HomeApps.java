package com.kerem.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

class HomeApps {
    private static int[][] appScore = new int[200][25];
    private final String fileName = "scores";

    void onAppClicked(int appID, int hour, boolean clickedOnList) {
        int score;
        if (clickedOnList) {
            score = 64;
        } else {
            score = 16;
        }
        appScore[appID][hour] += score;
        for (int i=1; i<=4; i++) {
            appScore[appID][(24 + hour + i) % 24] += (score / i);
            appScore[appID][(24 + hour - i) % 24] += (score / i);
        }
    }

    void reSync() {
        for (int i=0; i<200; i++) {
            for (int j=0; j<25; j++) {
                appScore[i][j] /= 2;
            }
        }
        syncToFile();
    }

    static int[] getHomeApps(int hour) {
        int[] homeApps = new int[6];
        for (int i=0; i<200; i++) {
            int j=4;
            while (j>=0 && appScore[i][hour] > appScore[homeApps[j]][hour]) {
                homeApps[j+1] = homeApps[j];
                homeApps[j] = i;
                j--;
            }
        }
        return homeApps;
    }

    static int getScore(int appID, int hour) {
        return appScore[appID][hour];
    }

    void syncFromFile() {
        File file = new File(fileName);
        byte[] b = new byte[4];
        try {
            FileInputStream fileIn = new FileInputStream(file);
            for (int i=0; i<200; i++) {
                for (int j = 0; j < 25; j++) {
                    if (fileIn.read(b) == -1) {
                        break;
                    }
                    appScore[i][j] = ByteBuffer.wrap(b).getInt();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            Scanner scanner = new Scanner(new File(fileName));
            int i=0, j=0;
            while (scanner.hasNextInt()) {
                appScore[i][j] = scanner.nextInt();
                j++;
                j %= 25;
                if (j == 0) i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         */
    }

    private void syncToFile() {
        File file = new File(fileName);
        try {
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file, false);
            for (int i=0; i<200; i++) {
                for (int j = 0; j < 25; j++) {
                    //fileOut.write(appScore[i][j]);
                    fileOut.write(ByteBuffer.allocate(4).putInt(appScore[i][j]).array());
                }
            }
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
