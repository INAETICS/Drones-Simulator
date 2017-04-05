package org.inaetics.isep;

public class D2Line {
    private D2Vector direction;
    private D2Vector base;

    public D2Line(D2Vector direction, D2Vector base) {
        this.direction = direction;
        this.base = base;
    }

    public D2Vector getDirection() {
        return direction;
    }

    public D2Vector getBase() {
        return base;
    }

    public D2Vector follow(double amount) {
        return this.getDirection().scale(amount).add(this.getBase());
    }

    public D2Vector intersection(D2Line other) {
        return null;
    }

    public boolean equals(D2Line other) {
        return this.getDirection().equals(other.getDirection()) && this.getBase().equals(other.getBase());
    }



    public static D2Vector intersection(D2Line v1, D2Line v2) {
        D2Vector d1 = v1.getDirection();
        double d1x = d1.getX();
        double d1y = d1.getY();
        D2Vector b1 = v1.getBase();
        double b1x = b1.getX();
        double b1y = b1.getY();

        D2Vector d2 = v2.getDirection();
        double d2x = d2.getX();
        double d2y = d2.getY();
        D2Vector b2 = v2.getBase();
        double b2x = b2.getX();
        double b2y = b2.getY();

        D2Vector result;

        double alpha_divisor = calc_alpha_intersection_divisor(v1, v2);


        if(b1.equals(b2)) {
            result = b1;
        } else if(alpha_divisor != 0) {
            double alpha_numerator = calc_alpha_intersection_numerator(v1, v2);
            result = v1.follow(alpha_numerator / alpha_divisor);

        // 2 vector direction == zero
        } else if(d1x == 0 && d1y != 0 && d2x == 0 && d2y != 0) {
            if(b1x == b2x) {

            } else {
                result = null;
            }
        } else if(d1x != 0 && d1y == 0 && d2x != 0 && d2y == 0) {
        } else if(d1x == 0 && d1y == 0 && d2x != 0 && d2y != 0) {
        } else if(d1x != 0 && d1y != 0 && d2x == 0 && d2y == 0) {

         // 3 vector direction == zero
        } else if (d1x != 0 && d1y == 0 && d2x == 0 && d2y == 0) {
        } else if (d1x == 0 && d1y != 0 && d2x == 0 && d2y == 0) {
        } else if (d1x == 0 && d1y == 0 && d2x != 0 && d2y == 0) {
        } else if (d1x == 0 && d1y == 0 && d2x == 0 && d2y != 0) {

         // 4 vector direction == zero
        } else if (d1x == 0 && d1y == 0 && d2x == 0 && d2y == 0) {
            if(b1.equals(b2)) {
                result = b1;
            } else {
                result = null;
            }
        } else {
            result = null;
        }

        return null;
    }

    //private static double calc_alpha_

    private static double calc_alpha_intersection_divisor(D2Line v1, D2Line v2) {
        D2Vector d1 = v1.getDirection();
        D2Vector b1 = v1.getBase();

        D2Vector d2 = v2.getDirection();
        D2Vector b2 = v2.getBase();

        return d2.getX() * d1.getY() - d1.getX() * d2.getY();
    }

    private static double calc_alpha_intersection_numerator(D2Line v1, D2Line v2) {
        D2Vector d1 = v1.getDirection();
        D2Vector b1 = v1.getBase();

        D2Vector d2 = v2.getDirection();
        D2Vector b2 = v2.getBase();

        return (d2.getY() * b1.getX() - b2.getX() * d2.getY() + d2.getX() * b2.getY() - d2.getX() * b1.getY());
    }
}
