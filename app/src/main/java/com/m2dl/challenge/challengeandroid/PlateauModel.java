package com.m2dl.challenge.challengeandroid;

/**
 * Created by Hyalis on 22/01/2015.
 */
public class PlateauModel {
    private EnumSurfaceType[][] plateau;

    public PlateauModel(){
        this.plateau = new EnumSurfaceType[1920][1054];
        for (int i = 0; i < 1920; i++) {
            for (int j = 0; j < 1054; j++) {
                this.plateau[i][j] = EnumSurfaceType.VIDE;
            }
        }
    }

    public void setSurface(int x, int y, EnumSurfaceType surface){
        // TODO gerer les limites (bords)
        x = x + 4;
        y = y - 4;
        for (int i = x; i < x + 5; i++) {
            for (int j = y; j < y + 5; j++) {
                this.plateau[i][j] = surface;
            }
        }
    }

    public boolean isAMur(int i, int j) {
        return this.plateau[i][j] == EnumSurfaceType.MUR;
    }
}
