package uk.ac.dcs.cs39440.marslandergame;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Lander {

    private List<String> verticesList;
    private List<String> faceList;

    public Lander(Context context) {
        verticesList = new ArrayList<>();
        faceList = new ArrayList<>();
    }
}
