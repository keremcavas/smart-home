package com.kerem.launcher;

class HomeApps {
    private static int[][] appScore = new int[200][25];

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
}
