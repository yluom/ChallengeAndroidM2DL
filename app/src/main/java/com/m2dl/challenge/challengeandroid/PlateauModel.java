package com.m2dl.challenge.challengeandroid;

/**
 * Created by Hyalis on 22/01/2015.
 */
public class PlateauModel {
    private EnumSurfaceType[][] plateau;

    public PlateauModel() {
        this.plateau = new EnumSurfaceType[38][21];
        for (int i = 0; i < 38; i++) {
            for (int j = 0; j < 21; j++) {
                this.plateau[i][j] = EnumSurfaceType.VIDE;
            }
        }
    }

    public void setSurface(int x, int y, EnumSurfaceType surface){
        // TODO gerer les limites (bords)
        x = x / 50;
        y = y /50;
        if(x < 38 && y < 21){
            this.plateau[x][y] = surface;
        }
    }

    public boolean isAMur(int i, int j) {
        if(i < 38 && j < 21) {
            return this.plateau[i][j] == EnumSurfaceType.MUR;
        }
        return false;
    }
}
