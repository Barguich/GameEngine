package engine;

import oop.graphics.Graphics;

public abstract class Avatar {

    protected View view;
    protected Entity entity;

    public Avatar(View view, Entity entity) {
        assert view != null;
        assert entity != null;

        this.view = view;
        this.entity = entity;

        entity.setAvatar(this);
        view.add(this);
    }

    public Entity entity() {
        return entity;
    }

    public View view() {
        return view;
    }

    public abstract void paint(Graphics g);
}