package engine.model;

public class Ticker {
    private Model model;
	
	public Ticker(Model model) {
		this.model=model;
	}
	public void tick (long elapsed) {
		model.tick(elapsed);
	}


}
