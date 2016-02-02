 package castalia;

 /**
  * From: http://www.stat.ubc.ca/~ruben/ILAS_2009/ilas_applets/continuous/Dist.java
  */
 public final class Dist
{
    //No Constructor
//Constants
    public static final double SQRT2PI=Math.sqrt(6.283185307179586);
    public static final double DBL_EPSILON=2.2204460492503131e-16;
    public static final double M_LN_SQRT_2PI= 0.918938533204672741780329736406;
//Class methods

    /** Standard normal density */
    public static double dnorms(double z)
    {  double pdf= Math.exp(-z * z / 2.0);
        pdf /= SQRT2PI;
        return pdf;
    }

    /** standard normal cdf */
    public static double pnorms(double x)
    {
        double c[]= {
                0.39894151208813466764, 8.8831497943883759412,
                93.506656132177855979, 597.27027639480026226,
                2494.5375852903726711, 6848.1904505362823326,
                11602.651437647350124, 9842.7148383839780218,
                1.0765576773720192317e-8 };

        double d[]= {
                22.266688044328115691, 235.38790178262499861,
                1519.377599407554805, 6485.558298266760755,
                18615.571640885098091, 34900.952721145977266,
                38912.003286093271411, 19685.429676859990727 };

        double p[] = {
                0.21589853405795699, 0.1274011611602473639,
                0.022235277870649807, 0.001421619193227893466,
                2.9112874951168792e-5, 0.02307344176494017303 };

        double q[] = {
                1.28426009614491121, 0.468238212480865118,
                0.0659881378689285515, 0.00378239633202758244,
                7.29751555083966205e-5 };

        double a[] = {
                2.2352520354606839287, 161.02823106855587881,
                1067.6894854603709582, 18154.981253343561249,
                0.065682337918207449113 };

        double b[] = {
                47.20258190468824187, 976.09855173777669322,
                10260.932208618978205, 45507.789335026729956} ;

        double one = 1.0;
        double half = 0.5;
        double zero = 0.0;
        double sixten = 1.6;
        double sqrpi = 1./SQRT2PI;
        double thrsh = 0.66291;
        double root32 = 5.6568542494923806;
        final double DBL_EPSILON=2.2204460492503131e-16;
        //final double DBL_MIN=2.2250738585072014e-308; ?

        double xden, temp, xnum, result, ccum;
        double del, min, eps, xsq;
        double y;
        int i;

        eps = DBL_EPSILON * .5;
        min = Double.MIN_VALUE; //DBL_MIN;
        y = Math.abs(x);
        if (y <= thrsh) {
	/* Evaluate pnorm for |z| <= 0.66291 */
            xsq = zero;
            if (y > eps) { xsq = x * x; }
            xnum = a[4] * xsq; xden = xsq;
            for (i = 1; i <= 3; ++i)
            {  xnum = (xnum + a[i - 1]) * xsq;
                xden = (xden + b[i - 1]) * xsq;
            }
            result = x * (xnum + a[3]) / (xden + b[3]);
            temp = result; result = half + temp;
            ccum = half - temp;
        }
        else if (y <= root32) {
    	/* Evaluate pnorm for 0.66291 <= |z| <= sqrt(32) */
            xnum = c[8] * y; xden = y;
            for (i = 1; i <= 7; ++i)
            {  xnum = (xnum + c[i - 1]) * y;
                xden = (xden + d[i - 1]) * y;
            }
            result = (xnum + c[7]) / (xden + d[7]);
            xsq = fint(y * sixten) / sixten;
            del = (y - xsq) * (y + xsq);
            result = Math.exp(-xsq*xsq*half) * Math.exp(-del*half)*result;
            ccum = one - result;
            if (x > zero) { temp = result; result = ccum; ccum = temp; }
        }
        else {
    	/* Evaluate pnorm for |z| > sqrt(32) */
            result = zero; xsq = one / (x * x);
            xnum = p[5] * xsq; xden = xsq;
            for (i = 1; i <= 4; ++i)
            {  xnum = (xnum + p[i - 1]) * xsq;
                xden = (xden + q[i - 1]) * xsq;
            }
            result = xsq * (xnum + p[4]) / (xden + q[4]);
            result = (sqrpi - result) / y;
            xsq = fint(x * sixten) / sixten;
            del = (x - xsq) * (x + xsq);
            result = Math.exp(-xsq*xsq*half) * Math.exp(-del*half)*result;
            ccum = one - result;
            if (x > zero) { temp = result; result = ccum; ccum = temp; }
        }
        if (result < min) { result = 0.0; }
        if (ccum < min) { ccum = 0.0; }
        return result;
    }

    /** integer floor */
    public static double fint(double x)
    { return (x >= 0.0) ? Math.floor(x) : -Math.floor(-x); }


    // From R source
    /** standard normal quantile function */
    public static double qnorms(double p)
    { double q, r, val;
        double a0 = 2.50662823884;
        double a1 = -18.61500062529;
        double a2 = 41.39119773534;
        double a3 = -25.44106049637;
        double b1 = -8.47351093090;
        double b2 = 23.08336743743;
        double b3 = -21.06224101826;
        double b4 = 3.13082909833;
        double c0 = -2.78718931138;
        double c1 = -2.29796479134;
        double c2 = 4.85014127135;
        double c3 = 2.32121276858;
        double d1 = 3.54388924762;
        double d2 = 1.63706781897;
        double zero = 0.0;
        double half = 0.5;
        double one = 1.0;
        double split = 0.42;

        if (p <= 0.0 || p >= 1.0) return Double.NaN;
        q = p - half;
        if (Math.abs(q) <= split)
        { /* 0.08 < p < 0.92 */
            r = q * q;
            val = q * (((a3 * r + a2) * r + a1) * r + a0)
                    / ((((b4 * r + b3) * r + b2) * r + b1) * r + one);
        }
        else
        { /* p < 0.08 or p > 0.92, set r = min(p,1-p) */
            r = p;
            if (q > zero) r = one - p;
            r = Math.sqrt(-Math.log(r));
            val = (((c3 * r + c2) * r + c1) * r + c0)
                    / ((d2 * r + d1) * r + one);
            if (q < zero) val = -val;
        }
        val = val - (pnorms(val) - p) / dnorms(val);
        return val;
    }




    public static int signgam = 0;
  /* the sign of gamma(x), set in lgamma(.), used by gamma(.) */

    //static double Gam_pos(double);
    //static double lGam_neg(double);
    //static double asform(double); /* ASymptotic FORM */

    /** log gamma function */
    public static double lgamma(double x)
    {  signgam = 1;
        if (x <= 0.0) return (lGam_neg(x));
        if (x >  8.0) return (asform(x));
        return (Math.log(Gam_pos(x)));
    }

    /* Coefficients  from Cheney and Hart (??--REF---??) */
   /* Asymptotic form: (not quite, why are the coeff. slightly changed ? */
    //#define M 6
    public static double asform(double x)
    {
    /* Equation 6.1.41 Abramowitz and Stegun -- extended Stirling */
    /* See also ACM algorithm 291 */

    /*	double log();*/
        int M=6;
        double p1[] = { 0.83333333333333101837e-1, -.277777777735865004e-2,
                0.793650576493454e-3, -.5951896861197e-3,
                0.83645878922e-3, -.1633436431e-2, };
        double nfac, xsq;
        int i;

        xsq = 1. / (x * x);
        for (nfac = 0, i = M - 1; i >= 0; i--)
        { nfac = nfac * xsq + p1[i]; }
        return ((x - .5) * Math.log(x) - x + M_LN_SQRT_2PI + nfac / x);
    }

    public static double lGam_neg(double x)
    { /* log |gamma(x)| for  x <= 0  ### G(-x) = - pi / [ G(x) x  sin(pi x) ] */
        double sinpx;
    /*	double log(), sin(), Gam_pos();*/

        x = -x; sinpx = Math.sin(Math.PI * x);
        if (sinpx == 0.0) return 1.7976931348623157E+308;
        //DOMAIN_ERROR;
        if (sinpx < 0.0) sinpx = -sinpx;
        else signgam = -1;
        return (-Math.log(x * Gam_pos(x) * sinpx / Math.PI));
    }

  /* Coefficients for rational approximation  gamma(x),  2 <= x <= 3 : */
    //#define N 8

    /* gamma(x) for x >= 0 :*/
    public static double Gam_pos(double x)
    {  int N=8;
        double n, d, s;
        double p2[] = { -.42353689509744089647e5, -.20886861789269887364e5,
                -.87627102978521489560e4, -.20085274013072791214e4,
                -.43933044406002567613e3, -.50108693752970953015e2,
                -.67449507245925289918e1, 0.0, };
        double q2[] = { -.42353689509744090010e5, -.29803853309256649932e4,
                0.99403074150827709015e4, -.15286072737795220248e4,
                -.49902852662143904834e3, 0.18949823415702801641e3,
                -.23081551524580124562e2, 0.10000000000000000000e1, };
        int i;

        if (x < 2.0) return (Gam_pos(x + 1.0) / x);
        if (x > 3.0) return ((x - 1.0) * Gam_pos(x - 1.0));
     /*-- rational approximation for  2 <= x <= 3 */
        s = x - 2.;
        for (n = 0, d = 0, i = N - 1; i >= 0; i--)
        { n = n * s + p2[i]; d = d * s + q2[i]; }
        return (n / d);
    }

   /** gamma cdf */


    public static double pgamma(double x, double p, double scale)
    { double pn1, pn2, pn3, pn4, pn5, pn6, arg, c, rn, a, b, an;
        double sum;
        double   oflo = 1.0e+37, xbig = 1.0e+8,
                plimit = 1000.0, elimit = -88.0;

	/* check that we have valid values for x and p */

        if (p <= 0.0 || scale <= 0.0) return Double.NaN;
        x /= scale;
        if (x <= 0.0) return 0.0;

     /* use a normal approximation if p > plimit */

        if (p > plimit)
        { pn1 = 3.0 * Math.sqrt(p) * Math.pow((x / p),
                (1.0 / 3.0) + 1.0 / (9.0 * p) - 1.0);
            return pnorms(pn1);
        }
     /* if x is extremely large compared to p then return 1 */
        if (x > xbig) return 1.0;
        if (x <= 1.0 || x < p)
        { /* use pearson's series expansion. */
            arg = p * Math.log(x) - x - lgamma(p + 1.0);
            c = 1.0; sum = 1.0; a = p;
            do { a += 1.0; c *= x / a; sum += c; } while (c > DBL_EPSILON);
            arg += Math.log(sum); sum = 0.0;
            if (arg >= elimit) sum = Math.exp(arg);
        }
        else
        { /* use a continued fraction expansion */
            arg = p * Math.log(x) - x - lgamma(p);
            a = 1.0 - p; b = a + x + 1.0; c = 0.0;
            pn1 = 1.0; pn2 = x; pn3 = x + 1.0; pn4 = x * b;
            sum = pn3 / pn4;
            for (;;)
            { a += 1.0; b += 2.0; c += + 1.0; an = a * c;
                pn5 = b * pn3 - an * pn1; pn6 = b * pn4 - an * pn2;
                if (Math.abs(pn6) > 0.0)
                { rn = pn5 / pn6;
                    if (Math.abs(sum - rn) <=
                            Math.min(DBL_EPSILON, DBL_EPSILON * rn)) break;
                    sum = rn;
                }
                pn1 = pn3; pn2 = pn4; pn3 = pn5; pn4 = pn6;
                if (Math.abs(pn5) >= oflo)
                { /* re-scale terms in continued fraction if terms are large */
                    pn1 /= oflo; pn2 /= oflo; pn3 /= oflo; pn4 /= oflo;
                }
            }
            arg += Math.log(sum); sum = 1.0;
            if (arg >= elimit) sum = 1.0 - Math.exp(arg);
        }
        return sum;
    }

    /** gamma quantile function */
    public static double qgamma(double p, double alpha, double scale)
    { /* Based on Algorithm AS 91 */
        int MAXIT= 20;
        double C1=0.01, C2=0.222222, C3=0.32, C4=0.4, C5=1.24,
                C6=2.2, C7=4.67, C8=6.66, C9=6.73, C10=13.32,
                C11=60.0, C12=70.0, C13=84.0, C14=105.0, C15=120.0,
                C16=127.0, C17=140.0, C18=1175.0, C19=210.0, C20=252.0,
                C21=2264.0, C22=294.0, C23=346.0, C24=420.0, C25=462.0,
                C26=606.0, C27=672.0, C28=707.0, C29=735.0, C30=889.0,
                C31=932.0, C32=966.0, C33=1141.0, C34=1182.0, C35=1278.0,
                C36=1740.0, C37=2520.0, C38=5040.0;

        double a, b, c, ch, g, p1, v;
        double p2, q, s1, s2, s3, s4, s5, s6, t, x, xx;
        double aa = 0.6931471806;
        double e = 0.5e-6;
        double pmin = 0.000002;
        double pmax = 0.999998;
        int i;

    /* test arguments and initialise */
        if(p < pmin || p > pmax || alpha<=0 ) return Double.NaN;

        v = 2*alpha;
    /* xx = 0.5*v; */
        xx = alpha; c = xx-1.0; g = lgamma(0.5*v);
        if(v < (-C5)*Math.log(p))
        { /* starting approximation for small chi-squared */
            ch = Math.pow(p*xx*Math.exp(g+xx*aa),1.0/xx);
            if(ch < e) return Double.NaN;
        }
        else if(v > C3)
        { /* starting approximation using Wilson and Hilferty estimate */
            x = qnorms(p); p1 = C2/v;
            ch = v*Math.pow(x*Math.sqrt(p1)+1.0-p1, 3.0);
       /* starting approximation for p tending to 1 */
            if( ch>C6*v+6.0 ) ch = -2.0*(Math.log(1.0-p)-c*Math.log(0.5*ch)+g);
        }
        else
        { /* starting approximation for v less than or equal to 0.32 */
            ch = C4; a = Math.log(1.0-p);
            do { q = ch; p1 = 1.0+ch*(C7+ch); p2 = ch*(C9+ch*(C8+ch));
                t = -0.5+(C7+2.0*ch)/p1-(C9+ch*(C10+3.0*ch))/p2;
                ch = ch-(1.0-Math.exp(a+g+0.5*ch+c*aa)*p2/p1)/t;
            } while(Math.abs(q/ch-1.0) > C1);
        }

    /* algorithm as 239 and calculation of seven term taylor series */

        for( i=1 ; i<=MAXIT  ; i++ )
        { q = ch; p1 = 0.5*ch; p2 = p-pgamma(p1, xx, 1.0);
            //#ifdef HAVE_ISNAN
            //if(!finite(p2)) DOMAIN_ERROR;
            if(Double.isInfinite(p2)) return Double.NaN;
            t = p2*Math.exp(xx*aa+g+p1-c*Math.log(ch));
            b = t/ch; a = 0.5*t-b*c;
            s1 = (C19+a*(C17+a*(C14+a*(C13+a*(C12+C11*a)))))/C24;
            s2 = (C24+a*(C29+a*(C32+a*(C33+C35*a))))/C37;
            s3 = (C19+a*(C25+a*(C28+C31*a)))/C37;
            s4 = (C20+a*(C27+C34*a)+c*(C22+a*(C30+C36*a)))/C38;
            s5 = (C13+C21*a+c*(C18+C26*a))/C37;
            s6 = (C15+c*(C23+C16*c))/C38;
            ch += t*(1.0+0.5*t*s1-b*c*(s1-b*(s2-b*(s3-b*(s4-b*(s5-b*s6))))));
            if(Math.abs(q/ch-1.0) > e) return 0.5*scale*ch;
        }
    /* possible loss of precision */
    /* errno = EDOM; */
        return 0.5*scale*ch;
    }

}