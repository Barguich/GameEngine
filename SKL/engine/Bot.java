package engine;

import java.util.List;

public abstract class Bot {
    protected Brain brain;
    protected BasicStunt stunt;
    public Bot(Brain brain, BasicStunt stunt){
        assert brain !=null;
        assert stunt!=null;
        this.brain=brain;
        this.stunt=stunt;
        stunt.setBot(this);
    }
    public abstract void think();
    public void done(){

    }
    public void collision(Entity e){
        assert e!=null;
    }
    public void collision(List<Entity> entities){
        assert entities!=null;
    }
}
