package engine;

import static engine.ISU.xAxis;
import static engine.ISU.yAxis;

public class Rect extends Shape {

  // FIELDS

   private double halfWidth, halfHeight;
   private int angle_degree;
   //0: horizontal, 90:tourné d'un quart de tour, 180: retourné, 270: autre quart de tour
  // CONSTRUCTOR

   public Rect(ISU.Coord center, ISU.Dimension size, int angle_degree){
       super(center);
       assert size!=null;
       assert center!=null;
       assert size.x()>0;
       assert size.y()>0;

       this.halfWidth=size.x() /2.0;
       this.halfHeight=size.y()/2.0;
        this.angle_degree=angle_degree;

   }
  // TRANSLATION ?

  // ROTATION

   public void rotate(int angle_degree){
     this.angle_degree=Math.floorMod(angle_degree,360);
   }
  // == INTERSECTION ==
   public boolean intersects(iShape o){
        return o.intersects(this);
         }
  // === engine.Rect/Circle Intersection ===
  public boolean intersects(Circle circle){
       assert circle!=null;
       return new RectCircleIntersection(this,circle).intersects();
  }


    // === Helping inner class ===

  /**
   * @implNote Principe
   *           <UL>
   *           <LI>translate le centre du cercle vers le repère formé par les axes
   *           du
   *           rectangle,</LI>
   *           <LI>redresse le repère du rectangle en annulant la rotation du
   *           rectangle,</LI>
   *           <LI>détermine le point <i>P</i> du rectangle le plus proche du
   *           centre <i>C</i> du cercle
   *           de façon efficace car le rectangle est aligné sur les axes
   *           X,Y.</LI>
   *           </UL>
   * @implNote Il y a intersection si distance(P,C) < rayon du cercle</LI>
   */

  public class RectCircleIntersection {

    // FIELDS

     private Rect outer;
     private Circle circle;
    private double cx, cy;
    // CONSTRUCTOR

     public RectCircleIntersection(Rect outer, Circle circle){
         this.circle=circle;
         this.outer=outer;
         this.cx=circle.getCenter().x();
         this.cy=circle.getCenter().y();
     }
    // REMEDY means `set right an undesirable situation`

    /**
     * @apiNote
     * @implNote Translate virtuellement engine.Rect et Circle dans un repère centré sur le
     *           centre du rectangle donc les axes sont ceux du rectangle.
     * @implNote Les coordonnées du centre du rectangle deviennent alors (0,0)
     * @implNote On translate le centre du cercle
     * @implNote On déplace par rotation le centre du cercle de -engine.Rect.angle.
     */
   public void remedy(){
       //translation +rotation
       //placer le rect à l'origine
        double tX= cx-outer.getCenter().x();
        double tY=cy-outer.getCenter().y();
        if(xAxis.onTorus&&Math.abs(tX)>xAxis.perimeter/2)tX-=Math.signum(tX)* xAxis.perimeter;
       if(yAxis.onTorus&&Math.abs(tY)>yAxis.perimeter/2)tY-=Math.signum(tY)* yAxis.perimeter;
       //rect tourné de 90°
       double rad=Math.toRadians(-outer.angle_degree);
        double cosA=Math.cos(rad);
        double sinA=Math.sin(rad);
        //rotation au centre du cercle
        cx=tX*cosA-tY*sinA;
        cy=tX*sinA+tY*cosA;
   }
    // INTERSECTION in the easy case

   public boolean intersects(){
        remedy();
        ISU.Coord closest=closestRectpoint();
        double dx=closest.x()-cx;
        double dy=closest.y()-cy;
        return(dx*dx+dy*dy)<=(circle.radius()*circle.radius());
   }
    /**
     * @apiNote POINT LE PLUS PROCHE DU CENTRE DU CERCLE
     * @implNote on projette les coins du rectange (c1,c2) et le centre (c) du
     *           cercle sur l'axe des <i>x<i>,
     *           la coordonnées en x du point le plus proche est parmi {c1.x, c2.x,
     *           c.x}
     * @implNote on projette les coins du rectange (c1,c2) et le centre (c) du
     *           cercle sur l'axe des <i>y<i>,
     *           la coordonnées en x du point le plus proche est parmi {c1.y, c2.y,
     *           c.y}
     *
     */

    public ISU.Coord closestRectpoint(){
        double px=clamp(cx, -outer.halfWidth,outer.halfWidth);
        double py=clamp(cy,-outer.halfHeight,outer.halfHeight);
        return isu.new Coord(px,py){
            public double x() {
                return px;
            }
            public double y(){
                return py;
            }
        };
    }
    /**
     * @return &in; {p, l, r}
     * @implNote la position dans l'interval [l,r] la plus proche de p est :
     * @implNote p si p &in; [l,r]
     * @implNote l si p < l
     * @implNote r si r < p
     * @param p = position
     * @param l = borne inférieure de l'interval
     * @param r = borne supérieure de l'interval
     */
    public double clamp(double p, double l, double r){
        if (p<l)
            return l;
        if(r<p)
            return r;

        return p;
    }


  }

  // === engine.Rect/engine.Rect Intersection ===
   public boolean intersects(Rect rect){
      assert rect!=null;
        return new RectRectIntersection(this,rect).intersects();
   }
  // === Helping inner class ===

   public class RectRectIntersection {
        private Rect a,b;
        public RectRectIntersection(Rect a,Rect b){
            this.a=a;
            this.b=b;
        }
        public boolean intersects(){
           double dx=ISU.xAxis.distance(a.getCenter().x(),b.getCenter().x());
           double dy=ISU.yAxis.distance(a.getCenter().y(),b.getCenter().y());
           //chevauchement des projections sur X et Y : collision
           boolean overLapX=dx<=a.halfWidth+b.halfWidth;
           boolean overLapY=dy<=a.halfHeight+b.halfHeight;
           return overLapX&&overLapY;
        }
  }
}
