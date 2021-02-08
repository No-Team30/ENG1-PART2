package characters.Entities.abilities;

import characters.Entities.Entity;
import org.json.simple.JSONObject;

/**
 * the ability can be host by player and enemy
 *
 * @param <THost>
 * @param <TTarget>
 */
public interface IAbility<THost extends Entity, TTarget extends Entity> {

    float getUseTime();

    float getUseTimeTiming();

    float getCooldownTime();

    float getCooldownTimeTiming();

    /**
     * get if this ability is ready to use
     *
     * @return
     */
    boolean isReady();

    /**
     * @return get if this ability is in use
     */
    boolean inUse();

    /**
     * set this ability disable status
     *
     * @param disable
     */
    void setDisable(boolean disable);

    /**
     * return if this ability is disable
     *
     * @return
     */
    boolean isDisable();

    /**
     * set this ability's host entity.
     *
     * @param host
     */
    void setHost(THost host);

    /**
     * get this ability's host entity.
     *
     * @return this ability's host entity.
     */
    THost getHost();

    /**
     * set this ability's host entity.
     *
     * @param target ability target
     */
    void setTarget(TTarget target);

    /**
     * get this ability's host entity.
     *
     * @return this ability's host entity.
     */
    TTarget getTarget();

    /**
     * try to start use ability
     */
    void tryUseAbility();

    /**
     * begin to use ability
     */
    void beginUseAbility();

    /**
     * Use ability,this will be called in update when useTiming > 0 ;
     */
    void useAbility(float delta);

    /**
     * end to use ability when ability useTiming is form n to 0;you can remove the ability effect here.
     */
    void endUseAbility();

    /**
     * update cooldown time and use time
     *
     * @param delta delta time in the game world
     */
    void update(float delta);

    /**
     * Exports the ability to a JSON Object
     *
     * @return The json object
     */
    JSONObject save();
}
