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

import wjd.math.V2;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class RomanRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int REGIMENT_SIZE = 6*6;
  
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors
  
  public RomanRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- REGIMENTAGENT */

  @Override
  protected void ai(int t_delta)
  {
    // turn around in circles
    advance(0.01f*t_delta);
  }
}
