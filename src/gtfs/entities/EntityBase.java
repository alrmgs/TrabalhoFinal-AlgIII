package gtfs.entities;

public abstract class EntityBase {

    private String id;

    protected EntityBase(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean hasId(String id) {
        return this.id.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EntityBase) {
            return id.equals(((EntityBase) o).id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
