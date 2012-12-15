/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.regiment;

import wjd.amb.control.EUpdateResult;
import wjd.amb.view.ICanvas;
import wjd.math.V2;
import wjd.teutoburg.collision.Agent;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 4, 2012
 */
public abstract class RegimentAgent extends Agent
{  
	  public enum State {
		    waiting, charging, fighting
		}
	  
  /* CONSTANTS */
  private static final float ZOOM_IMPOSTER_THRESHOLD = 0.25f;
  
  /* ATTRIBUTES */
  // model
  private int strength;
  private Faction faction;
  private final V2 grid_pos = new V2();
  protected Tile tile;
  protected State state;
  // organisation
  private Formation formation;
  // view
  private boolean nearby = true;
  private V2 left = new V2();
  // ai
  private final Rect perception_box = new Rect(Tile.SIZE.clone().scale(80));


  /* METHODS */
  // constructors
  public RegimentAgent(V2 start_position, int start_strength, Tile tile_, 
                                                              Faction faction)
  {
    // default
    super(start_position);
    left.reset(direction).left();
    
    // save parameters
    this.strength = start_strength;
    this.faction = faction;
    this.tile = tile_;
    
    // calculate unit positions based on the strength of the unit
    formation = faction.createFormation(this);
    setRadius(formation.reform());

    // initialize status
    state = State.waiting;
  }

  // accessors -- package
  
  int getStrength()
  {
    return strength;
  }

  V2 getDirection()
  {
    return direction;
  }

  V2 getLeft()
  {
    return left;
  }
  
  Faction getFaction()
  {
    return faction;
  }
  
  // accessors -- public 
  
  public boolean isFormedUp()
  {
    return (formation instanceof Formation.Turtle);
  }
  
  public void setFormedUp(boolean form_up)
  {
    // skip if this is already the case
    if(form_up == isFormedUp())
      return;
      
    // otherwise change formation...
    if(form_up)
      formation = new Formation.Turtle(this);
    else
      formation = new Formation.Rabble(this);
    
    // ... and reform!
    setRadius(formation.reform());
  }
  

  // mutators
  public EUpdateResult killSoldiers(int n_killed)
  {
    strength -= n_killed;
    if(strength <= 0)
      return EUpdateResult.DELETE_ME;
    
    setRadius(formation.reform());
    return EUpdateResult.CONTINUE;
  }  
  
  
  /* INTERFACE */
  
  protected abstract void ai(int t_delta, Iterable<Tile> percepts);
  protected abstract void fight(RegimentAgent r);
  
  
  /* OVERRIDES -- AGENT */
  
  @Override
  protected void directionChange()
  {
    // default
    super.directionChange();
    left.reset(direction).left();
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible)
      formation.reposition();
  }
  
  @Override
  protected void positionChange()
  {
    // default
    super.positionChange();
    
    grid_pos.reset(c.centre).scale(Tile.ISIZE).floor();
    if(grid_pos.x != tile.grid_position.x
      || grid_pos.y != tile.grid_position.y)
    {
        Tile new_tile = tile.grid.tiles[(int)grid_pos.y][(int)grid_pos.x];
        if(new_tile.setRegiment(this))
        {
          tile.setRegiment(null);
          tile = new_tile;
        }
    }
    
    // we need to recache the soldiers' positions if in view close to us
    if(visible)
      formation.reposition();
  }
  
  @Override
  public EUpdateResult update(int t_delta)
  {
    // default
    EUpdateResult result = super.update(t_delta);
    if (result != EUpdateResult.CONTINUE)
      return result;
    
    // choose action
    ai(t_delta);

    // set level of detail
    formation.setDetail(visible && nearby);
    
    // all clear!
    return EUpdateResult.CONTINUE;
  }

  @Override
  public void render(ICanvas canvas)
  {
     // skip if not in the camera's view
    if(visible = (canvas.getCamera().canSee(visibility_box)))
    {
      // we'll turn off the details if we're too far away
      nearby = (canvas.getCamera().getZoom() >= ZOOM_IMPOSTER_THRESHOLD);
     
      // render the formation depending on the level of detail
      formation.render(canvas);
    }
    else
      nearby = false;
  }
}
