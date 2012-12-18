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
		public static final State HIDING = new State(7, "hiding");
		
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
  
  /* VARIABLES */
  
  private static final V2 temp = new V2();
  
  /* METHODS */

  // constructors
  
  public BarbarianRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
    
    // initialise status
    state = BarbarianState.HIDING;
  }
  
  /* OVERRIDES -- REGIMENTAGENT */
  
  @Override
  protected EUpdateResult waiting(int t_delta)
  {
    
    if(nearestActivAlly != null)
    {
      faceTowards(nearestActivAlly.getCircle().centre);
      float min = Math.min(SPEED_FACTOR * t_delta, 
                          ((float)Math.sqrt(nearestActivAllyDist2)-2*c.radius));
      advance(min);
    }
    
    return EUpdateResult.CONTINUE;
  }
  
  @Override
  protected EUpdateResult charging(int t_delta)
  {
    if(nearestEnemy != null) // I can see an enemy
    {
      faceTowards(nearestEnemy.getCircle().centre);
      float distance = (float)Math.sqrt(nearestEnemyDist2);
      float min = Math.min(SPEED_FACTOR * t_delta, distance);
      advance(min);
    }
    
    else if(nearestActivAlly != null) // I can see an active ally
    {
      // charge towards whatever the ally is facing
      temp.reset(nearestActivAlly.getDirection())
          .scale((float)Math.sqrt(nearestActivAllyDist2))
          .sub(c.centre);
      advance(SPEED_FACTOR * t_delta);
    }
    else
    {
      state = State.WAITING;
    }
    
    return EUpdateResult.CONTINUE;
  }
  
  /* AI */
  private EUpdateResult hiding(int t_delta, Iterable<Tile> percepts)
  {
    // spring the trap when enough enemies are inside it
    
    if((perceived_threat >= AMBUSH_MIN_THREAT 
        && perceived_threat <= AMBUSH_MAX_THREAT
        && n_visible_allies >= AMBUSH_MIN_ALLIES)
    || heardHorn != null)
    {
      if(nearestEnemy != null && nearestActivAlly == null 
         && heardHorn != null && soundedHorn != null)
        soundTheHorn();
      state = State.CHARGING;
    }
    /*else if(nearestActivAlly != null)
    {
      faceTowards(nearestActivAlly.getCircle().centre);
      float min = Math.min(SPEED_FACTOR * t_delta, 
                          ((float)Math.sqrt(nearestActivAllyDist2)-2*c.radius));
      advance(min);
    }*/
    
    return EUpdateResult.CONTINUE;
  }
  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {
	  if(super.ai(t_delta, percepts) == EUpdateResult.DELETE_ME)
		  return EUpdateResult.DELETE_ME;
    
    if(state == BarbarianState.HIDING)
	  {
		  if(hiding(t_delta, percepts) == EUpdateResult.DELETE_ME)
			  return EUpdateResult.DELETE_ME;
	  }

	  return EUpdateResult.CONTINUE;
  }
  /*
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {  
	  if(state != State.FIGHTING && !combat.isEmpty())
	  {
		  state = State.FIGHTING;
	  }
	  if(state == State.FIGHTING)
	  {
		  if(!combat.isEmpty())
		  {
			  if(randomAttack() == EUpdateResult.DELETE_ME)
					return EUpdateResult.DELETE_ME;
		  }
		  else
		  {
			  state = State.WAITING;
		  }
	  }
	  if(state == State.CHARGING)
	  {
		  if(nearestEnemy != null)
		  {
			  V2 goal = nearestEnemy.getCircle().centre.clone();
			  //goal.add((float)Math.random()*10-5, (float)Math.random()*10-5);
			  // charge : turn in front of roman, then advance
			  faceTowards(goal);
			  float nearestEnemyDist = (float)Math.sqrt(nearestEnemyDist2);
			  float min = Math.min(SPEED_FACTOR*t_delta, nearestEnemyDist);
			  advance(min);
		  }
		  else
		  {
			  state = State.WAITING;
		  }
	  }
	  return EUpdateResult.CONTINUE;
  }*/
  
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
}
