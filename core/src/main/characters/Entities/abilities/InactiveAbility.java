package characters.Entities.abilities;

import characters.Entities.Entity;
import org.json.simple.JSONObject;
import screen.LoadGame;

/**
 * This is an ability instance, that does nothing
 * Used for when an infiltrator is arrested, and the ability should be removed
 */
public class InactiveAbility extends AbilityBase<Entity, Entity> {

    public InactiveAbility() {
        super();
        this.useTime = 0;
    }

    public InactiveAbility(JSONObject object) {
        super(object);
        System.out.println("Loaded inactive");
        LoadGame.validateAndLoadObject(object, "object_type", "ability");
        LoadGame.validateAndLoadObject(object, "ability_type", "inactive");
    }

    @Override
    public JSONObject save() {
        JSONObject state = super.save();
        state.put("ability_type", "inactive");
        return state;
    }

    @Override
    public void beginUseAbility() {

    }


    @Override
    public void endUseAbility() {

    }

    @Override
    public String toString() {
        return "Inactive abiltiy";
    }
}
