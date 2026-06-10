package engine;

import java.util.List;

public class Stunt {
   protected Model model;
   public Entity entity;
    public Stunt(Model model,Entity entity){
        assert model!=null;
        assert entity!=null;
        this.model=model;
        this.entity=entity;
        //connecter l'entite à un stunt
        entity.setStunt(this);
    }
//0: est
    //90: nord
    //180: ouest
    //270:sud
    public void set(int orientation){
        entity.setOrientation(orientation);
    }
    //mettre lentite au centre d'une cellule
    public void set(Grid.Cell c){
        assert c!=null;
        entity.retract();
        entity.setPosition(c.position);
        entity.deploy();
    }
    public void set(double x,double y){
        entity.retract();
        entity.setCoord(Game.isu().new Coord(x,y));
        entity.deploy();
    }
    //arreter l'entite quand elle entre en collision: remet la vitesse a 0
    public void collision(Entity e){
        assert e!=null;
        entity.setlSpeed(Game.isu().new Vector(0,0));
    }
    //arreter l'entite quand elle rencontre plusieurs entites
    public void collision(List<Entity> entities){
        assert entities!=null;
        entity.setlSpeed(Game.isu().new Vector(0,0));

    }
    public Entity entity(){
        return entity;
    }
}
