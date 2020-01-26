package com.bvg.publictransport.util;

import com.bvg.publictransport.api.LineInformation;

import java.util.Comparator;

public class LineInformationComparator implements Comparator<LineInformation> {

    private LineComparator lineComparator = new LineComparator();

    @Override
    public int compare(LineInformation l1, LineInformation l2) {

        if (l1 == null && l2 == null) {
            return 0;
        } else if (l1 == null) {
            return -1;
        } else if (l2 == null) {
            return 1;
        } else {
            return lineComparator.compare(l1.getLine(), l2.getLine());
        }
    }

}
