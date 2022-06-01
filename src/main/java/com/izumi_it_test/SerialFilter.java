package com.izumi_it_test;

public class SerialFilter {

    private int num;

    public boolean filtered(int arg) {
        if (this.num == arg) {
            return false;
        } else {
            this.num = arg;
        }
        return true;
    }
}
