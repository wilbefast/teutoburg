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
import wjd.math.M;
import wjd.math.V2;
import wjd.teutoburg.regiment.RegimentAgent.State;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class BarbarianRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int REGIMENT_SIZE = 63; // = 1 + 2 + 4 + ... + 16 + 32
  private static final float SPEED_FACTOR = 0.2f;
  private static final double BLOCK_CHANCE = 0.1;
  private static final double ATTACK_CHANCE = 0.6;
  
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors
  
  public BarbarianRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {
	  BarbarianRegiment nearestChargingBarbarian = null;
	  float distanceFromBarbarian = Float.MAX_VALUE, tmp;
	  
	  for(Tile t : percepts)
	  {
		  if(t.agent == null || t.agent.state == State.DEAD)
			  continue;

		  if(t.agent instanceof BarbarianRegiment && t.agent != this)
		  {
			  tmp = t.agent.getCircle().centre.distance2(c.centre);
			  if((t.agent.state == State.CHARGING || t.agent.state == State.FIGHTING) && tmp < distanceFromBarbarian)
			  {
				  nearestChargingBarbarian = (BarbarianRegiment)t.agent;
				  distanceFromBarbarian = tmp;
			  }
		  }
	  }
	  // stay hidden behind trees while romans are not near enough
	  // if no romans are visible, stay and wait
	  // else if not charging or fighting, charge !!!
	  // else if charging, charge
	  // else if fighting and has an ennemy, fight
	  if(state == State.FIGHTING)
	  {
		  if(nearestEnemy == null || !nearestEnemy.getCircle().collides(this.c))
			  state = State.WAITING;
	  }
	  else if(state == State.WAITING)
	  {
		  if(nearestEnemy != null)// TODO : wait for a good moment
		  {
			  state = State.CHARGING;
		  }
		  else if(nearestChargingBarbarian != null)
		  {
			  faceTowards(nearestChargingBarbarian.getCircle().centre);
			  float min = Math.min(SPEED_FACTOR*t_delta, ((float)Math.sqrt(distanceFromBarbarian)-2*c.radius));
			  advance(min);
		  }
	  }
	  else if(state == State.CHARGING)
	  {
		  if(nearestEnemy != null)
		  {
			  V2 goal = nearestEnemy.getCircle().centre.clone();
			  //goal.add((float)Math.random()*10-5, (float)Math.random()*10-5);
			  // charge : turn in front of roman, then advance
			  faceTowards(goal);
			  float nearestEnemyDist = (float)Math.sqrt(nearestEnemyDist2);
			  float min = Math.min(SPEED_FACTOR*t_delta, nearestEnemyDist);
			  if(advance(min) == EUpdateResult.DELETE_ME)
			  	return EUpdateResult.DELETE_ME;
		  }
		  else
		  {
			  state = State.WAITING;
		  }
	  }
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
}
