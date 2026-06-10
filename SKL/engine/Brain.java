package engine;

import java.util.ArrayList;
import java.util.List;

public class Brain {
    protected final Model model;
    protected final List<Bot>bots;
    public Brain(Model model){
        assert model!=null;
        this.model=model;
        this.bots=new ArrayList<>();
    }
    public void add(Bot bot){
        assert bot!=null;
        if(!bots.contains(bot))
            bots.add(bot);

    }
    public void remove(Bot bot){
        assert bot!=null;
        bots.remove(bot);
    }
    public void tick(long elapsed){
        assert elapsed>=0;
        for(Bot bot:new ArrayList<>(bots)){
            bot.think();
        }
    }
    public Model model(){
        return model;
    }
    public List<Bot>bots(){
        return new ArrayList<>(bots);
    }


}
