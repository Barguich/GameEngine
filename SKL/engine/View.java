package engine;

import oop.graphics.Graphics;
import java.util.ArrayList;
import java.util.List;

public class View {

    protected final Model model;
    protected final List<Avatar> avatars;

    public View(Model model) {
        assert model != null;
        this.model = model;
        this.avatars = new ArrayList<>();
    }

    public void add(Avatar avatar) {
        assert avatar != null;
        if (!avatars.contains(avatar)) {
            avatars.add(avatar);
        }
    }

    public void remove(Avatar avatar) {
        assert avatar != null;
        avatars.remove(avatar);
    }

    public void paint(Graphics g) {
        for (Avatar avatar : new ArrayList<>(avatars)) {
            if (model.entities().contains(avatar.entity())) {
                avatar.paint(g);
            }
        }
    }

    public Model model() {
        return model;
    }
    public java.util.List<Avatar> avatars() {
        return new java.util.ArrayList<>(avatars);
    }
}