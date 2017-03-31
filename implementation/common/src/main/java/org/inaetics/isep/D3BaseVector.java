package org.inaetics.isep;

public class D3BaseVector {
    private D3Vector base;
    private D3Vector direction;

    public D3BaseVector() {
        this(1,0,0, 1,0,0);
    }

    public D3BaseVector(double dx, double dy, double dz, double x, double y, double z) {
        this(new D3Vector(dx, dy, dz), new D3Vector(x, y, z));
    }

    public D3BaseVector(D3Vector direction, D3Vector base) {
        this.base = base;
        this.direction = direction;
    }

    public D3Vector getDirection() {
        return this.direction;
    }

    public D3Vector getBase() {
        return this.base;
    }

    private double calcIntersectionAlpha( double dxa, double dya,
                                          double xa,  double ya,
                                          double dxb, double dyb,
                                          double xb, double yb
                                        ) {

        double term1Divisor = Math.pow(dxa, 2) * dyb - dxa * dxb * dya;
        double term1Numerator = (xb - xa) * dya * dxb + (ya - yb) * dxb * dxa;
        double term2 = (xb - xa) / dxa;

        return (term1Numerator / term1Divisor) + term2;
    }

    private double calcIntersectionBeta( double dxa, double dya,
                                         double xa,  double ya,
                                         double dxb, double dyb,
                                         double xb, double yb
                                       ) {
        double divisor = dxa * dyb - dxb * dya;
        double numerator = (xb - xa) * dya + (ya - yb) * dxa;

        return numerator / divisor;
    }

    public D3Vector intersection(D3BaseVector v) {
        //double alpha;
        double beta_xy = 0;
        double beta_yz = 0;
        D3Vector result = null;
        boolean hasIntersection = false;

        D3Vector d1 = this.direction;
        D3Vector p1 = this.base;
        D3Vector d2 = v.getDirection();
        D3Vector p2 = v.getBase();




        if((d1.getX() * d2.getY() != d2.getX() * d1.getY()) && (d1.getY() * d2.getZ() != d2.getY() * d1.getZ())) {
            /*
            alpha = calcIntersectionAlpha( d1.getX(), d1.getY(),
                                           p1.getX(), p1.getY(),
                                           d2.getX(), d2.getY(),
                                           p2.getX(), p2.getY()
                                         );
            */
            beta_xy = calcIntersectionBeta( d1.getX(), d1.getY(),
                                         p1.getX(), p1.getY(),
                                         d2.getX(), d2.getY(),
                                         p2.getX(), p2.getY()
                                        );
            /*
            alpha = calcIntersectionAlpha( d1.getY(), d1.getZ(),
                                           p1.getY(), p1.getZ(),
                                           d2.getY(), d2.getZ(),
                                           p2.getY(), p2.getZ()
                                         );
            */
            beta_yz = calcIntersectionBeta( d1.getY(), d1.getZ(),
                                         p1.getY(), p1.getZ(),
                                         d2.getY(), d2.getZ(),
                                         p2.getY(), p2.getZ()
                                        );

            if(beta_xy == beta_yz) {
                hasIntersection = true;
            }
        }

        if(hasIntersection) {
            double x = d2.getX() * beta_xy + p2.getX();
            double y = d2.getY() * beta_xy + p2.getY();
            double z = d2.getZ() * beta_xy + p2.getZ();

            result = new D3Vector(x, y, z);
        }

        return result;
    }

}
