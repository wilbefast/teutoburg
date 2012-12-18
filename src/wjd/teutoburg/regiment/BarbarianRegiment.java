/*
 Copyright (C) 2012 William James Dyce, Chloé Desdouits

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
import wjd.math.V2;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class BarbarianRegiment extends RegimentAgent
{
	/* NESTING */
	public static class BarbarianState extends State
	{
		protected BarbarianState(int v, String k) 
    {
      super(v, k);
    }
	}
	
	/* CONSTANTS */
	private static final int REGIMENT_SIZE = 63; // = 1 + 2 + 4 + ... + 16 + 32
	private static final float SPEED_FACTOR = 0.6f;
	private static final double BLOCK_CHANCE = 0.1;
	private static final double ATTACK_CHANCE = 0.6;
  
  private static final int AMBUSH_MIN_THREAT = -100; 
  private static final int AMBUSH_MIN_ALLIES = 100;
  private static final int AMBUSH_MAX_THREAT = 100; 
  
  /* ATTRIBUTES */
  private boolean in_hiding = true;
  
  /* METHODS */

  // constructors
  
  public BarbarianRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
    
    // initialise status
    state = BarbarianState.WAITING;
  }
  
  /* OVERRIDES -- REGIMENTAGENT */
  
  @Override
  protected EUpdateResult waiting(int t_delta)
  {
	  if(in_hiding)
	  {
		  if(heardHorn != null)
			  in_hiding = false;
		  
		  else if(nearestEnemy != null)
		  {
			  faceTowards(nearestEnemy.getCircle().centre);
			  
			  if(n_visible_enemies > n_visible_allies / 2)// && n_visible_enemies < n_visible_allies)
			  {
				  soundTheHorn();
				  in_hiding = false;
			  }
		  }
		  
		  else if(nearestActivAlly != null)
		  {
			  faceTowards(nearestActivAlly.getCircle().centre);
			  advance(getSpeedFactor()* t_delta);
		  }
		  
		  else if(nearestFleeingAlly != null)
		  {
		  	temp1.reset(nearestFleeingAlly.getDirection())
		  			.opp()
	  				.scale(c.centre.distance(nearestFleeingAlly.getCircle().centre))
	  				.add(c.centre);
			  faceTowards(temp1);
			  advance(getSpeedFactor()* t_delta);
		  }
	  }
	  else
	  {
		  if(nearestEnemy != null)
			  state = State.CHARGING;
		  else if(nearestActivAlly != null)
		  {
			  faceTowards(nearestActivAlly.getCircle().centre);
			  advance(getSpeedFactor()* t_delta);
		  }
		  else if(heardHorn != null)
		  {
			  faceTowards(heardHorn.position);
			  advance(getSpeedFactor()* t_delta);
		  }
	  }
	  
    /*// spring the trap when enough enemies are inside it
    if((in_hiding 
        && perceived_threat >= AMBUSH_MIN_THREAT 
        && perceived_threat <= AMBUSH_MAX_THREAT
        && n_visible_allies >= AMBUSH_MIN_ALLIES)
      || heardHorn != null)
    {
      in_hiding = false;
      if(nearestActivAlly == null 
         || (heardHorn == null && n_active_allies < n_visible_allies/2))
        soundTheHorn();
      state = State.CHARGING;
    }
      
    // otherwise attack enemies on sight 
    else if(nearestEnemy != null)
    {
      state = State.CHARGING;
    }
    
    // otherwise go where allies are facing
    else if(nearestActivAlly != null)
    {
      // scaled ally direction
      temp1.reset(nearestActivAlly.getDirection())
          .scale((float)Math.sqrt(nearestActivAllyDist2))
      // position in front of ally
          .add(nearestActivAlly.getCircle().centre);
      turnTowardsGradually(temp1, MAX_TURN_RABBLE);
      advance(SPEED_FACTOR * t_delta);
    }*/
    
    return EUpdateResult.CONTINUE;
  }
  


  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {  	
	  if(super.ai(t_delta, percepts) == EUpdateResult.DELETE_ME)
		  return EUpdateResult.DELETE_ME;

	  return EUpdateResult.CONTINUE;
  }
  
  @Override
  protected double chanceToBlock(RegimentAgent attacker)
  {
    return BLOCK_CHANCE;
  }
  
  @Override
  protected double chanceToHit(RegimentAgent defender)
  {
    return ATTACK_CHANCE;
  }
  
  @Override
  protected boolean isEnemy(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof RomanRegiment);
  }
  
  @Override
  protected boolean isAlly(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof BarbarianRegiment);
  }
  
  @Override
  protected float getSpeedFactor()
  {
    return SPEED_FACTOR;
  }
  
  protected State getInitialState()
  {
  	return State.WAITING;
  }
}
