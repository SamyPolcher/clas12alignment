package org.clas.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jlab.groot.base.GStyle;

import org.clas.analysis.TrkSwim;
import org.clas.analysis.FiducialCuts;
import org.clas.analysis.ResolutionAnalysis;

public class Main {
    /** Main. */
    public static void main(String[] args) {
        // === Process input =======================================================================
        Map<Character, List<String>> params = new HashMap<>();
        if (IOHandler.parseArgs(args, params)) System.exit(1);

        String file = params.get('f').get(0);
        int nEvents = params.get('n') == null ? 0 : Integer.parseInt(params.get('n').get(0));

        // === Setup ===============================================================================
        setupGroot();
        // TODO. Add swim setup via terminal.
        double[] swmSetup = new double[]{-0.75, -1.0, -3.0}; // swim setup.
        int plotRan = 5; // Plotting range.
        int fitRan  = 4; // Fitting range.

        double[][] shArr = new double[Constants.FMTLAYERS][Constants.NVARS];
        // Update shArr based on alignment info given by user.
        for (int i = 0; i < shArr.length; ++i) {
            if (params.get('x') != null) shArr[i][0] = Double.parseDouble(params.get('x').get(i));
            if (params.get('y') != null) shArr[i][1] = Double.parseDouble(params.get('y').get(i));
            if (params.get('z') != null) shArr[i][2] = Double.parseDouble(params.get('z').get(i));
            if (params.get('X') != null) shArr[i][3] = Double.parseDouble(params.get('X').get(i));
            if (params.get('Y') != null) shArr[i][4] = Double.parseDouble(params.get('Y').get(i));
            if (params.get('Z') != null) shArr[i][5] = Double.parseDouble(params.get('Z').get(i));
        }
        FiducialCuts fCuts = new FiducialCuts();
        TrkSwim swim = new TrkSwim(swmSetup, shArr[0][4], shArr[0][5]);

        // === Alignment ===========================================================================
        List<Double> testShArr = new ArrayList<Double>();
        if (params.get('d') != null) {
            double inter = Double.parseDouble(params.get('d').get(0));
            double delta = Double.parseDouble(params.get('d').get(1));
            for (double i = -inter; i < 1.001*inter; i += delta) testShArr.add(i);
        }
        else testShArr.add(0.0);

        String var = params.get('v') == null ? null : params.get('v').get(0);

        // Print test shifts.
        if (var != null) {
            System.out.printf("SHIFTS TO BE TESTED:\n     %4s :", var);
            for (int i = 0; i < testShArr.size(); ++i) {
                System.out.printf(" %5.2f", testShArr.get(i));
            }
            System.out.printf("\n\n");
        }

        ResolutionAnalysis resAnls = new ResolutionAnalysis(file, nEvents, shArr, fCuts);
        resAnls.shiftAnalysis(var, testShArr, plotRan, fitRan, swmSetup);
    }

    /** Do basic groot setup to get fancy plots. */
    public static boolean setupGroot() {
        GStyle.getAxisAttributesX().setTitleFontSize(24);
        GStyle.getAxisAttributesX().setLabelFontSize(18);
        GStyle.getAxisAttributesY().setTitleFontSize(24);
        GStyle.getAxisAttributesY().setLabelFontSize(18);
        GStyle.getAxisAttributesZ().setLabelFontSize(14);
        GStyle.getAxisAttributesX().setLabelFontName("Arial");
        GStyle.getAxisAttributesY().setLabelFontName("Arial");
        GStyle.getAxisAttributesZ().setLabelFontName("Arial");
        GStyle.getAxisAttributesX().setTitleFontName("Arial");
        GStyle.getAxisAttributesY().setTitleFontName("Arial");
        GStyle.getAxisAttributesZ().setTitleFontName("Arial");
        GStyle.setGraphicsFrameLineWidth(1);
        GStyle.getH1FAttributes().setLineWidth(2);
        GStyle.getH1FAttributes().setOptStat("1111");

        return false;
    }
}
